package workspace

import crew.User

class PlmWorkspace {
    String name
    String description
    User owner
    Long inventoryRevision = 0L
    Date dateCreated
    Date lastUpdated

    static constraints = {
        name blank: false, maxSize: 160, unique: 'owner'
        description nullable: true, maxSize: 2000
        owner nullable: false
        inventoryRevision min: 0L
    }
    static mapping = {
        table 'pm_workspace'
        description type: 'text'
        owner index: 'pm_workspace_owner_idx'
    }
}
