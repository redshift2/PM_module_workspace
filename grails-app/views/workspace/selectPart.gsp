<!doctype html><html><head><meta name="layout" content="main"/><title>Add part to workspace</title></head><body>
<h1>Add ${part.originalName?.encodeAsHTML()} to a workspace</h1>
<g:if test="${flash.error}"><p>${flash.error.encodeAsHTML()}</p></g:if>
<g:each in="${workspaces}" var="w"><g:form action="addPart" id="${w.id}" method="POST"><g:hiddenField name="partId" value="${part.id}"/><g:hiddenField name="inventoryRevision" value="${w.inventoryRevision}"/><button>Add to ${w.name.encodeAsHTML()}</button></g:form></g:each>
<p><g:link action="index">Manage workspaces</g:link></p></body></html>
