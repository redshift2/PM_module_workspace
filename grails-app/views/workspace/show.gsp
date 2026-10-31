<!doctype html><html><head><meta name="layout" content="main"/><title>Workspace</title></head><body>
<p><g:link action="index">← Workspaces</g:link></p><h1>${workspace.name.encodeAsHTML()}</h1>
<p>${workspace.description?.encodeAsHTML()}</p><p>Owner: ${workspace.owner?.username?.encodeAsHTML()} · Inventory revision: ${workspace.inventoryRevision}</p>
<g:if test="${flash.error}"><p class="alert alert-danger">${flash.error.encodeAsHTML()}</p></g:if>
<h2>Parts</h2><table class="table"><thead><tr><th>Part</th><th>Recorded CAD version</th><th>Latest PLM version</th><th>Status</th><th>Actions</th></tr></thead><tbody>
<g:each in="${inventory.parts.values()}" var="p"><tr><td>${p.name?.encodeAsHTML()}</td><td>${p.version}</td><td>${p.latestVersion}</td><td>${p.updateAvailable ? 'Update available' : 'Up to date'}</td><td>
<g:if test="${canEdit}"><g:if test="${p.updateAvailable}"><g:form action="approveVersion" id="${workspace.id}" method="POST"><g:hiddenField name="partId" value="${p.partId}"/><g:hiddenField name="inventoryRevision" value="${workspace.inventoryRevision}"/><button>Approve latest version</button></g:form></g:if>
<g:form action="removePart" id="${workspace.id}" method="POST"><g:hiddenField name="partId" value="${p.partId}"/><g:hiddenField name="inventoryRevision" value="${workspace.inventoryRevision}"/><button>Remove</button></g:form></g:if>
</td></tr></g:each></tbody></table>
<g:if test="${canEdit}"><h2>Add part by PLM ID</h2><g:form action="addPart" id="${workspace.id}" method="POST"><g:hiddenField name="inventoryRevision" value="${workspace.inventoryRevision}"/><input type="number" name="partId" required min="1"/><button>Add part</button></g:form></g:if>
<g:if test="${isOwner}"><h2>Manage access</h2><g:form action="setMember" id="${workspace.id}" method="POST"><label>Username <input name="username" required/></label><select name="role"><option>VIEWER</option><option>EDITOR</option></select><button>Grant/update access</button></g:form>
<table class="table"><thead><tr><th>User</th><th>Role</th><th></th></tr></thead><tbody><g:each in="${members}" var="m"><tr><td>${m.user?.username?.encodeAsHTML()}</td><td>${m.accessRole}</td><td><g:form action="removeMember" id="${workspace.id}" method="POST"><g:hiddenField name="userId" value="${m.user.id}"/><button>Revoke</button></g:form></td></tr></g:each></tbody></table>
<g:form action="delete" id="${workspace.id}" method="POST"><button onclick="return confirm('Delete workspace?')">Delete workspace</button></g:form></g:if>
</body></html>
