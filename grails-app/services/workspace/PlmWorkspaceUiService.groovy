package workspace

import crew.User
import org.codehaus.groovy.runtime.MethodClosure as MC
import taack.ui.dsl.UiTableSpecifier
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.IconStyle

/**
 * Native Taack workspace listing.
 * Uses the same header / rowColumn / rowField / rowAction pattern as
 * PlmFreeCadUiService.buildPartTable().
 */
class PlmWorkspaceUiService {

    UiTableSpecifier buildWorkspaceTable(Collection<PlmWorkspace> workspaces, User currentUser) {
        new UiTableSpecifier().ui {
            header {
                label 'Workspace'
                label 'Description'
                label 'Owner'
                label 'Revision'
                label 'Actions'
            }

            // The PLM history table uses row { ... } for explicit rows.
            for (PlmWorkspace w : (workspaces ?: [])) {
                row {
                    rowColumn {
                        rowAction w.name, WorkspaceController.&show as MC, w.id
                    }
                    rowField w.description ?: '—'
                    rowField w.owner?.username ?: '—'
                    rowField (w.inventoryRevision ?: 0L).toString()
                    rowColumn {
                        rowAction ActionIcon.ADD * IconStyle.SCALE_DOWN, WorkspaceController.&copyForm as MC, w.id
                        if (w.owner?.id == currentUser?.id) {
                            rowAction ActionIcon.EDIT * IconStyle.SCALE_DOWN, WorkspaceController.&editForm as MC, w.id
                            rowAction ActionIcon.SHOW * IconStyle.SCALE_DOWN, WorkspaceController.&shareWorkspace as MC, w.id
                            rowAction ActionIcon.SHOW * IconStyle.SCALE_DOWN, WorkspaceController.&deleteForm as MC, w.id
                        }
                    }
                }
            }
        }
    }
}