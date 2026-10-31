package workspace

import crew.User
import plm.PlmFreeCadPart
import grails.gorm.transactions.Transactional

@Transactional
class PlmWorkspaceService {
    PlmWorkspaceSecurityService plmWorkspaceSecurityService

    private static void require(boolean allowed) {
        if (!allowed) throw new SecurityException('Workspace access denied')
    }
    private static void checkRevision(PlmWorkspace w, Long expected) {
        if (expected == null || w.inventoryRevision != expected) {
            throw new IllegalStateException('Workspace inventory changed; refresh and retry')
        }
    }
    private static void bump(PlmWorkspace w) {
        w.inventoryRevision = (w.inventoryRevision ?: 0L) + 1L
        w.save(failOnError: true, flush: true)
    }
    List<PlmWorkspace> accessible(User user) {
        if (!user) return []
        List<PlmWorkspace> owned = PlmWorkspace.findAllByOwner(user)
        List<PlmWorkspaceMember> shared = PlmWorkspaceMember.findAllByUser(user)
        ((owned + shared.collect { it.workspace }).findAll { it != null }).unique { it.id }.sort { it.name.toLowerCase() }
    }
    PlmWorkspace create(User user, String name, String description = null) {
        require(user != null)
        new PlmWorkspace(owner: user, name: name?.trim(), description: description?.trim(), inventoryRevision: 0L)
                .save(failOnError: true, flush: true)
    }
    PlmWorkspace duplicate(User user, Long sourceId, String name) {
        PlmWorkspace source = PlmWorkspace.get(sourceId)
        require(plmWorkspaceSecurityService.canView(source, user))
        PlmWorkspace target = create(user, name, source.description)
        PlmWorkspacePart.findAllByWorkspace(source).each { PlmWorkspacePart entry ->
            new PlmWorkspacePart(workspace: target, part: entry.part, fileName: entry.fileName,
                recordedMajor: entry.recordedMajor, recordedMinor: entry.recordedMinor)
                .save(failOnError: true)
        }
        bump(target)
        target
    }
    PlmWorkspaceMember setMember(User owner, Long workspaceId, User member, String role) {
        PlmWorkspace w = PlmWorkspace.get(workspaceId)
        require(plmWorkspaceSecurityService.isOwner(w, owner))
        require(member != null && member.id != owner.id)
        if (!(role in ['VIEWER', 'EDITOR'])) throw new IllegalArgumentException('Invalid role')
        PlmWorkspaceMember m = PlmWorkspaceMember.findByWorkspaceAndUser(w, member) ?: new PlmWorkspaceMember(workspace: w, user: member)
        m.accessRole = role
        m.save(failOnError: true, flush: true)
    }
    void removeMember(User owner, Long workspaceId, User member) {
        PlmWorkspace w = PlmWorkspace.get(workspaceId)
        require(plmWorkspaceSecurityService.isOwner(w, owner))
        PlmWorkspaceMember.findByWorkspaceAndUser(w, member)?.delete(flush: true)
    }
    PlmWorkspacePart addPart(User user, Long workspaceId, Long partId, Long expectedRevision) {
        PlmWorkspace w = PlmWorkspace.lock(workspaceId)
        require(plmWorkspaceSecurityService.canEdit(w, user))
        checkRevision(w, expectedRevision)
        PlmFreeCadPart part = PlmFreeCadPart.get(partId)
        if (!part || part.active == false || part.nextVersion != null) throw new IllegalArgumentException('Part not found or not current')
        PlmWorkspacePart entry = PlmWorkspacePart.findByWorkspaceAndPart(w, part)
        if (entry) return entry
        entry = new PlmWorkspacePart(workspace: w, part: part,
            fileName: (part.originalName ?: "part-${part.id}.FCStd"),
            recordedMajor: part.majorVersion ?: 1, recordedMinor: part.minorVersion ?: 0)
        entry.save(failOnError: true, flush: true)
        bump(w)
        entry
    }
    void removePart(User user, Long workspaceId, Long partId, Long expectedRevision) {
        PlmWorkspace w = PlmWorkspace.lock(workspaceId)
        require(plmWorkspaceSecurityService.canEdit(w, user))
        checkRevision(w, expectedRevision)
        PlmWorkspacePart entry = PlmWorkspacePart.findByWorkspaceAndPart(w, PlmFreeCadPart.get(partId))
        if (entry) { entry.delete(flush: true); bump(w) }
    }
    PlmWorkspacePart approveLatest(User user, Long workspaceId, Long partId, Long expectedRevision) {
        PlmWorkspace w = PlmWorkspace.lock(workspaceId)
        require(plmWorkspaceSecurityService.canEdit(w, user))
        checkRevision(w, expectedRevision)
        PlmWorkspacePart entry = PlmWorkspacePart.findByWorkspaceAndPart(w, PlmFreeCadPart.get(partId))
        if (!entry) throw new IllegalArgumentException('Part is not in workspace')
        // The recorded version changes ONLY when an editor explicitly approves it.
        entry.recordedMajor = entry.part.majorVersion ?: 1
        entry.recordedMinor = entry.part.minorVersion ?: 0
        entry.save(failOnError: true, flush: true)
        bump(w)
        entry
    }
    void deleteWorkspace(User user, Long id) {
        PlmWorkspace w = PlmWorkspace.get(id)
        require(plmWorkspaceSecurityService.isOwner(w, user))
        PlmWorkspacePart.findAllByWorkspace(w).each { it.delete() }
        PlmWorkspaceMember.findAllByWorkspace(w).each { it.delete() }
        w.delete(flush: true)
    }
}
