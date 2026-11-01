package workspace

import crew.User
import plm.PlmFreeCadPart
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

/**
 * JSON API for FreeCAD Global Workspaces.
 * Conventional Grails URLs: /workspaceApi/<action>[/<id>]
 * Uses the same authenticated session and roles as the HTML workspace UI.
 */
@Secured(['ROLE_PLM_USER', 'ROLE_ADMIN'])
class WorkspaceApiController {
    def springSecurityService
    PlmWorkspaceService plmWorkspaceService
    PlmWorkspaceSyncService plmWorkspaceSyncService
    PlmWorkspaceSecurityService plmWorkspaceSecurityService
    def plmFreeCadSecurityService

    private User currentUser() { springSecurityService.currentUser as User }

    private void jsonResponse(Object data, int status = 200) {
        response.status = status
        render(data as JSON)
    }

    private void failure(int status, String message) {
        jsonResponse([success: false, error: message], status)
    }

    private Map body() {
        if (!request.contentType?.toLowerCase()?.contains('application/json')) return [:]
        def parsed = request.JSON
        parsed instanceof Map ? (Map) parsed : [:]
    }

    private Map inputs() {
        Map values = [:]
        values.putAll(params)
        values.putAll(body())
        values
    }

    private Long number(Object value) {
        if (value == null || value.toString().trim() == '') return null
        try { return Long.valueOf(value.toString()) }
        catch (NumberFormatException ignored) { return null }
    }

    private boolean requirePost() {
        if (request.method == 'POST') return true
        failure(405, 'POST required')
        false
    }

    private PlmWorkspace readable(Long id) {
        PlmWorkspace workspace = id == null ? null : PlmWorkspace.get(id)
        if (!plmWorkspaceSecurityService.canView(workspace, currentUser())) {
            failure(403, 'Workspace not found or access denied')
            return null
        }
        workspace
    }

    private Map workspaceData(PlmWorkspace w) {
        User u = currentUser()
        String role = plmWorkspaceSecurityService.isOwner(w, u) ? 'OWNER' :
            (plmWorkspaceSecurityService.canEdit(w, u) ? 'EDITOR' : 'VIEWER')
        [id: w.id, name: w.name, description: w.description ?: '',
         owner: w.owner?.username, inventoryRevision: w.inventoryRevision,
         role: role, canEdit: role != 'VIEWER', canManageMembers: role == 'OWNER']
    }

    private void handleFailure(Exception e) {
        if (e instanceof SecurityException) failure(403, 'Workspace access denied')
        else if (e instanceof IllegalStateException) failure(409, e.message ?: 'Workspace revision conflict')
        else if (e instanceof IllegalArgumentException) failure(400, e.message ?: 'Invalid request')
        else {
            log.error('Workspace API request failed', e)
            failure(500, 'Workspace operation failed')
        }
    }

    /** GET /workspaceApi/list */
    def list() {
        jsonResponse([success: true, workspaces:
            plmWorkspaceService.accessible(currentUser()).collect { workspaceData(it) }])
    }

    /** GET /workspaceApi/inventory/<id> */
    def inventory(Long id) {
        PlmWorkspace w = readable(id)
        if (w == null) return
        try {
            jsonResponse([success: true, workspace: workspaceData(w),
                inventory: plmWorkspaceSyncService.inventory(w, currentUser())])
        } catch (Exception e) { handleFailure(e) }
    }

    /** POST JSON {name, description} */
    def create() {
        if (!requirePost()) return
        Map p = inputs()
        try {
            PlmWorkspace w = plmWorkspaceService.create(currentUser(),
                p.name?.toString(), p.description?.toString())
            jsonResponse([success: true, workspace: workspaceData(w)], 201)
        } catch (Exception e) { handleFailure(e) }
    }

    /** POST /workspaceApi/duplicate/<id> JSON {name} */
    def duplicate(Long id) {
        if (!requirePost()) return
        Map p = inputs()
        try {
            PlmWorkspace w = plmWorkspaceService.duplicate(currentUser(), id, p.name?.toString())
            jsonResponse([success: true, workspace: workspaceData(w)], 201)
        } catch (Exception e) { handleFailure(e) }
    }

    /** POST /workspaceApi/addPart/<id> JSON {partId, inventoryRevision} */
    def addPart(Long id) {
        if (!requirePost()) return
        Map p = inputs()
        Long partId = number(p.partId)
        Long revision = number(p.inventoryRevision)
        if (id == null || partId == null || revision == null) {
            failure(400, 'id, partId and inventoryRevision are required'); return
        }
        try {
            PlmFreeCadPart part = PlmFreeCadPart.get(partId)
            if (part == null || !plmFreeCadSecurityService.canDownloadFile(part, currentUser())) {
                failure(403, 'Part not found or access denied'); return
            }
            plmWorkspaceService.addPart(currentUser(), id, partId, revision)
            inventory(id)
        } catch (Exception e) { handleFailure(e) }
    }

    /** POST /workspaceApi/removePart/<id> JSON {partId, inventoryRevision} */
    def removePart(Long id) {
        if (!requirePost()) return
        Map p = inputs()
        Long partId = number(p.partId)
        Long revision = number(p.inventoryRevision)
        if (id == null || partId == null || revision == null) {
            failure(400, 'id, partId and inventoryRevision are required'); return
        }
        try {
            plmWorkspaceService.removePart(currentUser(), id, partId, revision)
            inventory(id)
        } catch (Exception e) { handleFailure(e) }
    }

    /** POST /workspaceApi/approveVersion/<id> JSON {partId, inventoryRevision} */
    def approveVersion(Long id) {
        if (!requirePost()) return
        Map p = inputs()
        Long partId = number(p.partId)
        Long revision = number(p.inventoryRevision)
        if (id == null || partId == null || revision == null) {
            failure(400, 'id, partId and inventoryRevision are required'); return
        }
        try {
            plmWorkspaceService.approveLatest(currentUser(), id, partId, revision)
            inventory(id)
        } catch (Exception e) { handleFailure(e) }
    }

    /** GET /workspaceApi/members/<id> (owner only) */
    def members(Long id) {
        PlmWorkspace w = readable(id)
        if (w == null) return
        if (!plmWorkspaceSecurityService.isOwner(w, currentUser())) {
            failure(403, 'Only the owner can manage members'); return
        }
        jsonResponse([success: true, members: PlmWorkspaceMember.findAllByWorkspace(w).collect {
            [userId: it.user.id, username: it.user.username, role: it.accessRole]
        }])
    }

    /** POST /workspaceApi/setMember/<id> JSON {username, role} */
    def setMember(Long id) {
        if (!requirePost()) return
        Map p = inputs()
        try {
            User member = User.findByUsername(p.username?.toString())
            plmWorkspaceService.setMember(currentUser(), id, member, p.role?.toString())
            members(id)
        } catch (Exception e) { handleFailure(e) }
    }

    /** POST /workspaceApi/removeMember/<id> JSON {userId} */
    def removeMember(Long id) {
        if (!requirePost()) return
        Long userId = number(inputs().userId)
        if (userId == null) { failure(400, 'userId required'); return }
        try {
            plmWorkspaceService.removeMember(currentUser(), id, User.get(userId))
            members(id)
        } catch (Exception e) { handleFailure(e) }
    }

    /** POST /workspaceApi/delete/<id> */
    def delete(Long id) {
        if (!requirePost()) return
        try {
            plmWorkspaceService.deleteWorkspace(currentUser(), id)
            jsonResponse([success: true, deletedId: id])
        } catch (Exception e) { handleFailure(e) }
    }
}
