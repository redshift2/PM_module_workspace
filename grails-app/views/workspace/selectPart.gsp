<!doctype html><html><head><meta name="layout" content="main"/><title>Add Part to Workspace</title></head><body>
<div class="container-fluid py-3">
  <nav aria-label="breadcrumb"><ol class="breadcrumb"><li class="breadcrumb-item"><g:link controller="plm" action="parts">PLM Parts</g:link></li><li class="breadcrumb-item"><g:link action="index">Workspaces</g:link></li><li class="breadcrumb-item active">Add Part</li></ol></nav>
  <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3"><h1 class="h3 mb-0">Add ${part.originalName?.encodeAsHTML()} to a Workspace</h1><div><g:link action="index" class="btn btn-outline-secondary">← Workspaces</g:link> <g:link controller="plm" action="parts" class="btn btn-outline-secondary">Back to PLM</g:link> <g:link uri="/" class="btn btn-outline-secondary">Server Home</g:link></div></div>
  <g:if test="${flash.error}"><div class="alert alert-danger">${flash.error.encodeAsHTML()}</div></g:if>
  <div class="card"><div class="card-header"><strong>Available Workspaces</strong></div><div class="table-responsive"><table class="table table-striped table-hover align-middle mb-0"><thead><tr><th>Workspace</th><th>Owner</th><th>Action</th></tr></thead><tbody>
  <g:each in="${workspaces}" var="w"><tr><td>${w.name.encodeAsHTML()}</td><td>${w.owner?.username?.encodeAsHTML()}</td><td><g:form action="addPart" id="${w.id}" method="POST"><g:hiddenField name="partId" value="${part.id}"/><g:hiddenField name="inventoryRevision" value="${w.inventoryRevision}"/><button class="btn btn-sm btn-primary" type="submit">Add to Workspace</button></g:form></td></tr></g:each>
  <g:if test="${!workspaces}"><tr><td colspan="3" class="text-center py-4">No editable workspaces available.</td></tr></g:if>
  </tbody></table></div></div>
</div></body></html>
