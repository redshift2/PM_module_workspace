package workspace

import crew.User
import jakarta.annotation.PostConstruct
import org.codehaus.groovy.runtime.MethodClosure
import taack.app.TaackApp
import taack.app.TaackAppRegisterService

class PlmWorkspaceSecurityService {

    // Eager initialization is needed so the application tile is registered
    // during server startup, even before a workspace action is requested.
    static lazyInit = false

    @PostConstruct
    void init() {
        def iconStream = this.class.getResourceAsStream('/workspace/workspace.svg')
        if (iconStream == null) {
            throw new IllegalStateException(
                'Workspace icon missing: src/main/resources/workspace/workspace.svg'
            )
        }
        String svg = iconStream.withCloseable { stream ->
            new String(stream.readAllBytes(), 'UTF-8')
        }
        TaackAppRegisterService.register(
            new TaackApp(WorkspaceController.&index as MethodClosure, svg)
        )
    }

    boolean canView(PlmWorkspace workspace, User user) {
        if (workspace == null || user == null) return false
        workspace.owner?.id == user.id ||
            PlmWorkspaceMember.countByWorkspaceAndUser(workspace, user) > 0
    }

    boolean canEdit(PlmWorkspace workspace, User user) {
        if (workspace == null || user == null) return false
        workspace.owner?.id == user.id ||
            PlmWorkspaceMember.countByWorkspaceAndUserAndAccessRole(workspace, user, 'EDITOR') > 0
    }

    boolean isOwner(PlmWorkspace workspace, User user) {
        workspace != null && user != null && workspace.owner?.id == user.id
    }
}