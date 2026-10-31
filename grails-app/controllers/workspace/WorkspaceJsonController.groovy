package workspace

import crew.User
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_PLM_USER', 'ROLE_ADMIN'])
class WorkspaceJsonController {
    def springSecurityService
    PlmWorkspaceService plmWorkspaceService
    PlmWorkspaceSyncService plmWorkspaceSyncService
    PlmWorkspaceSecurityService plmWorkspaceSecurityService

    private User currentUser() { springSecurityService.currentUser as User }
    private Map payload() { (request.JSON instanceof Map ? request.JSON : [:]) as Map }
    private Long asId(Object o) { o == null || o.toString().trim() == '' ? null : o.toString().toLong() }
    private void output(Object value, int code = 200) { response.status = code; render(value as JSON) }
    private void fail(Exception e) {
        int code = e instanceof SecurityException ? 403 : e instanceof IllegalStateException ? 409 : e instanceof IllegalArgumentException ? 400 : 500
        if (code == 500) log.error('Workspace request failed', e)
        output([error: code == 500 ? 'Internal server error' : e.message], code)
    }
    def index() { redirect(controller: 'workspace', action: 'index') }
    def list() {
        output([workspaces: plmWorkspaceService.accessible(currentUser()).collect { w ->
            [id: w.id, name: w.name, ownerId: w.owner?.id, inventoryRevision: w.inventoryRevision,
             canEdit: plmWorkspaceSecurityService.canEdit(w, currentUser())]
        }])
    }
    def details(Long id) {
        try {
            PlmWorkspace w = PlmWorkspace.get(id)
            if (!plmWorkspaceSecurityService.canView(w, currentUser())) throw new SecurityException('Workspace access denied')
            output([id: w.id, name: w.name, description: w.description, ownerId: w.owner.id,
                inventoryRevision: w.inventoryRevision,
                canEdit: plmWorkspaceSecurityService.canEdit(w, currentUser()),
                canManage: plmWorkspaceSecurityService.isOwner(w, currentUser())])
        } catch (Exception e) { fail(e) }
    }
    def inventory(Long id) {
        try { output(plmWorkspaceSyncService.inventory(PlmWorkspace.get(id), currentUser())) }
        catch (Exception e) { fail(e) }
    }
    def create() {
        if (request.method != 'POST') { response.sendError(405); return }
        try { Map p = payload(); def w = plmWorkspaceService.create(currentUser(), p.name as String, p.description as String); output([id: w.id], 201) }
        catch (Exception e) { fail(e) }
    }
    def duplicate() {
        if (request.method != 'POST') { response.sendError(405); return }
        try { Map p = payload(); def w = plmWorkspaceService.duplicate(currentUser(), asId(p.id), p.name as String); output([id: w.id], 201) }
        catch (Exception e) { fail(e) }
    }
    def addParts() {
        if (request.method != 'POST') { response.sendError(405); return }
        try {
            Map p = payload(); Long id = asId(p.id); Long revision = asId(p.inventoryRevision)
            // Each mutation uses optimistic revision; clients refresh after every successful change.
            List<Long> partIds = (p.partIds ?: []).collect { asId(it) }
            if (!partIds) throw new IllegalArgumentException('partIds required')
            partIds.each { Long partId ->
                plmWorkspaceService.addPart(currentUser(), id, partId, revision)
                revision = PlmWorkspace.get(id).inventoryRevision
            }
            output(plmWorkspaceSyncService.inventory(PlmWorkspace.get(id), currentUser()))
        } catch (Exception e) { fail(e) }
    }
    def removeParts() {
        if (request.method != 'POST') { response.sendError(405); return }
        try {
            Map p = payload(); Long id = asId(p.id); Long revision = asId(p.inventoryRevision)
            (p.partIds ?: []).each { partId ->
                plmWorkspaceService.removePart(currentUser(), id, asId(partId), revision)
                revision = PlmWorkspace.get(id).inventoryRevision
            }
            output(plmWorkspaceSyncService.inventory(PlmWorkspace.get(id), currentUser()))
        } catch (Exception e) { fail(e) }
    }
    def approveVersion() {
        if (request.method != 'POST') { response.sendError(405); return }
        try {
            Map p = payload()
            plmWorkspaceService.approveLatest(currentUser(), asId(p.id), asId(p.partId), asId(p.inventoryRevision))
            output(plmWorkspaceSyncService.inventory(PlmWorkspace.get(asId(p.id)), currentUser()))
        } catch (Exception e) { fail(e) }
    }
    def members(Long id) {
        try {
            PlmWorkspace w = PlmWorkspace.get(id)
            if (!plmWorkspaceSecurityService.canView(w, currentUser())) throw new SecurityException('Workspace access denied')
            output([members: PlmWorkspaceMember.findAllByWorkspace(w).collect { [userId: it.user.id, username: it.user.username, role: it.accessRole] }])
        } catch (Exception e) { fail(e) }
    }
    def setMember() {
        if (request.method != 'POST') { response.sendError(405); return }
        try {
            Map p = payload(); User member = User.get(asId(p.userId))
            def m = plmWorkspaceService.setMember(currentUser(), asId(p.id), member, p.role as String)
            output([userId: m.user.id, role: m.accessRole])
        } catch (Exception e) { fail(e) }
    }
    def removeMember() {
        if (request.method != 'POST') { response.sendError(405); return }
        try { Map p = payload(); plmWorkspaceService.removeMember(currentUser(), asId(p.id), User.get(asId(p.userId))); output([ok: true]) }
        catch (Exception e) { fail(e) }
    }
    def delete() {
        if (request.method != 'POST') { response.sendError(405); return }
        try { Map p = payload(); plmWorkspaceService.deleteWorkspace(currentUser(), asId(p.id)); output([ok: true]) }
        catch (Exception e) { fail(e) }
    }
    // A sync request reports the authoritative inventory; it never silently overwrites versions.
    def sync() {
        if (request.method != 'POST') { response.sendError(405); return }
        try { Map p = payload(); output(plmWorkspaceSyncService.inventory(PlmWorkspace.get(asId(p.id)), currentUser())) }
        catch (Exception e) { fail(e) }
    }
}
