package workspace

import crew.User
import plm.PlmFreeCadPart

class PlmWorkspaceSyncService {
    PlmWorkspaceSecurityService plmWorkspaceSecurityService

    Map inventory(PlmWorkspace w, User user) {
        if (!plmWorkspaceSecurityService.canView(w, user)) throw new SecurityException('Workspace access denied')
        Map parts = [:]
        PlmWorkspacePart.findAllByWorkspace(w).each { PlmWorkspacePart entry ->
            PlmFreeCadPart p = entry.part
            String recorded = entry.recordedVersion
            String latest = "${p.majorVersion ?: 1}.${p.minorVersion ?: 0}"
            parts[p.id.toString()] = [partId: p.id, name: entry.fileName,
                file: entry.fileName, originalName: p.originalName, version: recorded, latestVersion: latest,
                updateAvailable: recorded != latest]
        }
        [workspaceId: w.id, name: w.name, inventoryRevision: w.inventoryRevision, parts: parts]
    }
}