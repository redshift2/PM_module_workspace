<!doctype html><html><head><meta name="layout" content="main"/><title>PLM Workspaces</title></head><body>
<h1>PLM Workspaces</h1><g:if test="${flash.error}"><p class="alert alert-danger">${flash.error.encodeAsHTML()}</p></g:if>
<h2>Create workspace</h2><g:form controller="workspace" action="create" method="POST">
<label>Name <input name="name" required maxlength="160"/></label>
<label>Description <input name="description" maxlength="2000"/></label><button type="submit">Create</button></g:form>
<h2>Accessible workspaces</h2><table class="table"><thead><tr><th>Name</th><th>Owner</th><th>Revision</th><th>Actions</th></tr></thead><tbody>
<g:each in="${workspaces}" var="w"><tr><td><g:link action="show" id="${w.id}">${w.name.encodeAsHTML()}</g:link></td><td>${w.owner?.username?.encodeAsHTML()}</td><td>${w.inventoryRevision}</td><td>
<g:form action="duplicate" id="${w.id}" method="POST"><input name="name" required placeholder="New workspace name"/><button>Duplicate</button></g:form>
</td></tr></g:each></tbody></table></body></html>
