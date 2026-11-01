package workspace

import crew.User
import jakarta.annotation.PostConstruct
import org.codehaus.groovy.runtime.MethodClosure
import taack.app.TaackApp
import taack.app.TaackAppRegisterService

class PlmWorkspaceSecurityService {

    // True only after the Workspace module has initialized successfully.
    // The PLM serverInfo endpoint can inspect this flag when the module is present.
    static volatile boolean FEATURE_GLOBAL_WORKSPACE = false


    // Eager initialization is needed so the application tile is registered
    // during server startup, even before a workspace action is requested.
    static lazyInit = false

    @PostConstruct
    void init() {
        FEATURE_GLOBAL_WORKSPACE = false
        log.info("WORKSPACE: Starting application registration")

        def resource = this.class.getResourceAsStream('/workspace/workspace.svg')

        if (resource == null) {
            log.error("WORKSPACE: workspace.svg resource not found")
            return
        }

        String svg = resource.withCloseable {
            new String(it.readAllBytes(), 'UTF-8')
        }

        TaackAppRegisterService.register(
            new TaackApp(
                WorkspaceController.&index as MethodClosure,
                svg
            )
        )

        FEATURE_GLOBAL_WORKSPACE = true
        log.info("WORKSPACE: Application registered successfully")
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