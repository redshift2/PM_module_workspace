package workspace

import plm.PlmFreeCadPart

class PlmWorkspacePart {
    PlmWorkspace workspace
    PlmFreeCadPart part
    String fileName
    Integer recordedMajor
    Integer recordedMinor
    Date dateCreated
    Date lastUpdated

    static constraints = {
        workspace nullable: false, unique: 'part'
        part nullable: false
        fileName blank: false, maxSize: 512
        recordedMajor min: 0
        recordedMinor min: 0
    }
    static mapping = {
        table 'pm_workspace_part'
        workspace index: 'pm_workspace_part_workspace_idx'
    }
    String getRecordedVersion() { "${recordedMajor}.${recordedMinor}" }
}
