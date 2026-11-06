package workspace

import crew.User
import plm.PlmFreeCadPart
import plm.PlmPartLifecycleState
import plm.PlmPartLifecycleService
import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_PLM_USER', 'ROLE_ADMIN'])
class WorkspaceController {
    def springSecurityService
    PlmPartLifecycleService plmPartLifecycleService
    PlmWorkspaceService plmWorkspaceService
    PlmWorkspaceSyncService plmWorkspaceSyncService
    PlmWorkspaceSecurityService plmWorkspaceSecurityService

    private User user() { springSecurityService.currentUser as User }
    def index() {
        User current = user()
        def available = plmWorkspaceService.accessible(current)
        def checkedOut = PlmFreeCadPart.findAllByCheckedOutBy(current).findAll { part ->
            part.lifecycleState == PlmPartLifecycleState.CHECKED_OUT &&
                part.active == true && part.nextVersion == null
        }
        def rows = checkedOut.collect { part ->
            def memberships = PlmWorkspacePart.findAllByPart(part).findAll { entry ->
                available.any { w -> w.id == entry.workspace?.id }
            }.collect { entry ->
                [id: entry.workspace.id, name: entry.workspace.name]
            }
            [part: part, workspaces: memberships]
        }
        [workspaces: available, checkedOutParts: rows]
    }

    def undoCheckOut(Long id) {
        if (request.method != 'POST') { response.sendError(405); return }
        try {
            plmPartLifecycleService.cancelCheckOut(id, user())
            flash.message = 'Check-out cancelled.'
        } catch (Exception ex) {
            log.warn('Unable to cancel check-out for part ' + id, ex)
            flash.error = ex.message ?: 'Unable to cancel check-out.'
        }
        response.sendRedirect('/workspace/index')
    }
    def show(Long id) {
        PlmWorkspace w = PlmWorkspace.get(id)
        if (!plmWorkspaceSecurityService.canView(w, user())) { response.sendError(403); return }
        [workspace: w, inventory: plmWorkspaceSyncService.inventory(w, user()),
         canEdit: plmWorkspaceSecurityService.canEdit(w, user()),
         isOwner: plmWorkspaceSecurityService.isOwner(w, user()),
         members: PlmWorkspaceMember.findAllByWorkspace(w)]
    }
    @Secured(['ROLE_PLM_USER', 'ROLE_ADMIN'])
    def debugAccess() {
        User current = springSecurityService.currentUser as User

        render([
            username: current?.username,
            authorities: springSecurityService.authentication?.authorities
                ?.collect { it.authority }
        ] as grails.converters.JSON)
}
    def create() {
        if (request.method != 'POST') { response.sendError(405); return }
        try { def w = plmWorkspaceService.create(user(), params.name, params.description); redirect(action: 'show', id: w.id) }
        catch (Exception e) { flash.error = e.message; redirect(action: 'index') }
    }
    def duplicate(Long id) {
        if (request.method != 'POST') { response.sendError(405); return }
        try { def w = plmWorkspaceService.duplicate(user(), id, params.name); redirect(action: 'show', id: w.id) }
        catch (Exception e) { flash.error = e.message; redirect(action: 'index') }
    }
    def addPart(Long id, Long partId) {
        if (request.method != 'POST') { response.sendError(405); return }
        try { plmWorkspaceService.addPart(user(), id, partId, params.long('inventoryRevision')); redirect(action: 'show', id: id) }
        catch (Exception e) { flash.error = e.message; redirect(action: 'show', id: id) }
    }
    def removePart(Long id, Long partId) {
        if (request.method != 'POST') { response.sendError(405); return }
        try { plmWorkspaceService.removePart(user(), id, partId, params.long('inventoryRevision')); redirect(action: 'show', id: id) }
        catch (Exception e) { flash.error = e.message; redirect(action: 'show', id: id) }
    }
    def approveVersion(Long id, Long partId) {
        if (request.method != 'POST') { response.sendError(405); return }
        try { plmWorkspaceService.approveLatest(user(), id, partId, params.long('inventoryRevision')); redirect(action: 'show', id: id) }
        catch (Exception e) { flash.error = e.message; redirect(action: 'show', id: id) }
    }
    def setMember(Long id) {
        if (request.method != 'POST') { response.sendError(405); return }
        try {
            User member = User.findByUsername(params.username)
            plmWorkspaceService.setMember(user(), id, member, params.role)
        } catch (Exception e) { flash.error = e.message }
        redirect(action: 'show', id: id)
    }
    def removeMember(Long id, Long userId) {
        if (request.method != 'POST') { response.sendError(405); return }
        try { plmWorkspaceService.removeMember(user(), id, User.get(userId)) }
        catch (Exception e) { flash.error = e.message }
        redirect(action: 'show', id: id)
    }
    def delete(Long id) {
        if (request.method != 'POST') { response.sendError(405); return }
        try { plmWorkspaceService.deleteWorkspace(user(), id); redirect(action: 'index') }
        catch (Exception e) { flash.error = e.message; redirect(action: 'show', id: id) }
    }
    // GET /workspace/selectPart?partId=... : standalone entry point from PLM screens.
    def selectPart(Long partId) {
        PlmFreeCadPart part = PlmFreeCadPart.get(partId)
        if (!part) { response.sendError(404); return }
        [part: part, workspaces: plmWorkspaceService.accessible(user()).findAll {
            plmWorkspaceSecurityService.canEdit(it, user())
        }]
    }
}
