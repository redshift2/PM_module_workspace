<!doctype html>
<html><head><meta name="layout" content="main"/><title>Add part to workspace</title></head>
<body>
<h1>Add ${part.originalName?.encodeAsHTML()} to a workspace</h1>
<g:if test="${flash.error}"><p>${flash.error.encodeAsHTML()}</p></g:if>
<g:if test="${!workspaces}"><p>No editable workspaces are available. Create a workspace or request Editor access first.</p></g:if>
<g:else>
<form method="POST" action="/workspace/addPart" style="max-width:560px">
  <input type="hidden" name="partId" value="${part.id}"/>
  <label for="workspaceId">Select workspace</label>
  <select id="workspaceId" name="id" required style="display:block;width:100%;margin:12px 0">
    <g:each in="${workspaces}" var="w">
      <option value="${w.id}" data-revision="${w.inventoryRevision}">${w.name.encodeAsHTML()}</option>
    </g:each>
  </select>
  <input type="hidden" id="inventoryRevision" name="inventoryRevision" value="${workspaces[0].inventoryRevision}"/>
  <button type="submit">Add to Workspace</button>
  <a href="/plm/showPart/${part.id}">Cancel</a>
</form>
<script>
(function () {
  var select = document.getElementById('workspaceId');
  var revision = document.getElementById('inventoryRevision');
  select.addEventListener('change', function () {
    revision.value = select.options[select.selectedIndex].getAttribute('data-revision');
  });
})();
</script>
</g:else>
<p><a href="/workspace/index">Manage workspaces</a></p>
</body></html>
