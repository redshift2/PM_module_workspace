package workspace

import crew.User

class PlmWorkspaceMember {
    PlmWorkspace workspace
    User user
    String accessRole = 'VIEWER' // VIEWER or EDITOR; owner is workspace.owner
    Date dateCreated
    Date lastUpdated

    static constraints = {
        workspace nullable: false, unique: 'user'
        user nullable: false
        accessRole inList: ['VIEWER', 'EDITOR']
    }
    static mapping = {
        table 'pm_workspace_member'
        workspace index: 'pm_workspace_member_workspace_idx'
    }
}
