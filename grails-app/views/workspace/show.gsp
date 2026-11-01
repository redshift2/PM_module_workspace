<!doctype html>
<html>
<head>
<meta charset="UTF-8"/>
<title>Workspace Details</title>
<style>
/* Extend the dark theme to the entire page, not just the workspace panel. */
html, body { min-height: 100%; background: #20252c !important; }
body { margin: 0 !important; color: #dce2e8; }
/* Self-contained styles: this server does not load Bootstrap on GSP pages. */
body .pmw{font-family:Arial,Helvetica,sans-serif;font-size:14px;line-height:1.5;color:#dce2e8;background:#20252c;padding:0 24px 24px;min-height:100vh}
body .pmw *{box-sizing:border-box}
body .pmw h1{font-size:24px;margin:0;font-weight:600;color:#f4f6f8}
body .pmw h2{font-size:16px;margin:0;color:#f4f6f8}
body .pmw a{color:#7ab8e8;text-decoration:none}
body .pmw a:hover{text-decoration:underline}
body .pmw .pmw-header{display:flex;justify-content:space-between;align-items:center;gap:12px;flex-wrap:wrap;margin:0 -24px 18px;padding:16px 24px;background:#145b91;border-bottom:1px solid #3785bb;position:sticky;top:0;z-index:100;box-shadow:0 3px 10px #0004}
body .pmw .pmw-actions{display:flex;align-items:center;gap:7px;flex-wrap:wrap}
body .pmw .pmw-card{border:1px solid #454d56;background:#2c323a;border-radius:5px;margin-bottom:16px;overflow:hidden}
body .pmw .pmw-card-title{background:#353d47;padding:11px 14px;border-bottom:1px solid #4b5561}
body .pmw .pmw-card-content{padding:14px}
body .pmw .pmw-btn{display:inline-flex;align-items:center;justify-content:center;gap:6px;padding:7px 11px;border:1px solid #596675;border-radius:4px;background:#414c58;color:#f0f4f8;font:inherit;cursor:pointer;text-decoration:none;min-height:34px}
body .pmw .pmw-btn:hover{background:#526172;color:#fff;text-decoration:none}
body .pmw .pmw-primary{background:#206ca3;border-color:#337fb7;color:#fff}
body .pmw .pmw-danger{background:#6c3038;border-color:#93444e}
body .pmw .pmw-icon{width:15px;height:15px;display:inline-block;vertical-align:middle;fill:currentColor}
body .pmw input,body .pmw select{background:#20262e;color:#f3f5f8;border:1px solid #647180;border-radius:4px;padding:7px 9px;font:inherit}
body .pmw input{max-width:100%}
body .pmw label{display:block;margin-bottom:4px;color:#dce2e8;font-weight:600}
body .pmw .pmw-form{display:flex;align-items:end;gap:10px;flex-wrap:wrap}
body .pmw .pmw-form>div{min-width:180px;flex:1}
body .pmw .pmw-form input{width:100%}
body .pmw .pmw-table-scroll{overflow-x:auto}
body .pmw table{width:100%;border-collapse:collapse;text-align:left}
body .pmw th{background:#3a434e;color:#f0f4f8;border-bottom:2px solid #65717f;padding:10px 12px;white-space:nowrap}
body .pmw td{border-bottom:1px solid #4c5662;padding:9px 12px;vertical-align:middle}
body .pmw tbody tr:nth-child(even){background:#313943}
body .pmw tbody tr:hover{background:#404d5b}
body .pmw .pmw-row-actions{display:flex;gap:6px;align-items:center;flex-wrap:wrap}
body .pmw .pmw-row-actions form{margin:0}
body .pmw .pmw-error{background:#64333b;color:#fff;padding:10px;border-radius:4px;margin:10px 0}
body .pmw .pmw-muted{color:#b4c0cc}
body .pmw dialog{background:#2d353f;color:#edf2f7;border:1px solid #667483;border-radius:6px;padding:18px;min-width:min(450px,90vw);box-shadow:0 10px 40px #0009}
body .pmw dialog::backdrop{background:#0009}
body .pmw dialog label{margin-top:10px}
body .pmw dialog input,body .pmw dialog select{width:100%}
body .pmw dialog .pmw-actions{margin-top:16px;justify-content:flex-end}

body .pmw .pmw-summary{display:flex;gap:20px;flex-wrap:wrap;color:#c8d4df;margin:0}
body .pmw .pmw-inline-form{display:flex;align-items:end;gap:8px;flex-wrap:wrap;margin:0}
body .pmw .pmw-inline-form label{margin:0}
body .pmw .pmw-inline-form input{min-width:120px}
body .pmw .pmw-inline-form select{min-width:110px}
body .pmw .pmw-subtitle{margin:0 0 12px;color:#dce2e8}
body .pmw .pmw-table-scroll form{display:inline-block;margin:0 6px 0 0}
body .pmw .pmw-card-content form{margin:0}
body .pmw .pmw-form-grid{display:flex;gap:12px;align-items:end;flex-wrap:wrap}
body .pmw .pmw-form-grid>div{flex:1;min-width:180px}
body .pmw .pmw-form-grid input{width:100%}
body .pmw .pmw-status-warning{color:#ffb76b;font-weight:600}
body .pmw .pmw-status-ok{color:#9bd9b2}
</style>
</head>
<body>
<div class="pmw">
  <header class="pmw-header">
    <h1>Workspace: ${workspace.name?.encodeAsHTML()}</h1>
    <nav class="pmw-actions" aria-label="Workspace navigation">
      <g:link action="index" class="pmw-btn">&#8592; All Workspaces</g:link>
      <g:link controller="plm" action="parts" class="pmw-btn">PLM Parts</g:link>
      <g:link uri="/" class="pmw-btn">Server Home</g:link>
    </nav>
  </header>

  <g:if test="${flash.error}"><div class="pmw-error">${flash.error.encodeAsHTML()}</div></g:if>

  <section class="pmw-card">
    <div class="pmw-card-title"><h2>Workspace Details</h2></div>
    <div class="pmw-card-content">
      <g:if test="${workspace.description}"><p class="pmw-subtitle">${workspace.description.encodeAsHTML()}</p></g:if>
      <p class="pmw-summary"><span><strong>Owner:</strong> ${workspace.owner?.username?.encodeAsHTML() ?: '—'}</span><span><strong>Inventory revision:</strong> ${workspace.inventoryRevision}</span></p>
    </div>
  </section>

  <section class="pmw-card">
    <div class="pmw-card-title"><h2>Workspace Parts</h2></div>
    <div class="pmw-table-scroll">
      <table>
        <thead><tr><th>Part</th><th>Recorded CAD Version</th><th>Latest PLM Version</th><th>Status</th><th>Actions</th></tr></thead>
        <tbody>
          <g:each in="${inventory.parts.values()}" var="p">
            <tr>
              <td>${p.name?.encodeAsHTML() ?: '—'}</td>
              <td>${p.version ?: '—'}</td>
              <td>${p.latestVersion ?: '—'}</td>
              <td><g:if test="${p.updateAvailable}"><span class="pmw-status-warning">Update available</span></g:if><g:else><span class="pmw-status-ok">Up to date</span></g:else></td>
              <td>
                <div class="pmw-row-actions">
                  <g:if test="${canEdit}">
                    <g:if test="${p.updateAvailable}">
                      <g:form action="approveVersion" id="${workspace.id}" method="POST">
                        <g:hiddenField name="partId" value="${p.partId}"/>
                        <g:hiddenField name="inventoryRevision" value="${workspace.inventoryRevision}"/>
                        <button type="submit" class="pmw-btn pmw-primary">Approve latest</button>
                      </g:form>
                    </g:if>
                    <g:form action="removePart" id="${workspace.id}" method="POST">
                      <g:hiddenField name="partId" value="${p.partId}"/>
                      <g:hiddenField name="inventoryRevision" value="${workspace.inventoryRevision}"/>
                      <button type="submit" class="pmw-btn pmw-danger" onclick="return confirm('Remove this part from the workspace?')">Remove</button>
                    </g:form>
                  </g:if>
                </div>
              </td>
            </tr>
          </g:each>
          <g:if test="${!inventory.parts}"><tr><td colspan="5" class="pmw-muted">No parts in this workspace.</td></tr></g:if>
        </tbody>
      </table>
    </div>
  </section>

  <g:if test="${canEdit}">
    <section class="pmw-card">
      <div class="pmw-card-title"><h2>Search PLM Parts</h2></div>
      <div class="pmw-card-content">
        <g:form action="show" id="${workspace.id}" method="GET" class="pmw-inline-form">
          <div style="flex:1;min-width:240px">
            <label for="pmw-part-search">Part name or label</label>
            <input id="pmw-part-search" name="partQuery" type="search" minlength="2" maxlength="120"
                   placeholder="Search PLM parts..." value="${partQuery?.encodeAsHTML()}" required/>
          </div>
          <button type="submit" class="pmw-btn pmw-primary">Search</button>
          <g:link action="show" id="${workspace.id}" class="pmw-btn">Clear</g:link>
        </g:form>
      </div>
      <g:if test="${partQuery?.size() >= 2}">
        <div class="pmw-table-scroll">
          <table>
            <thead><tr><th>Part name</th><th>Label</th><th>CAD version</th><th>Actions</th></tr></thead>
            <tbody>
              <g:each in="${searchResults}" var="part">
                <tr>
                  <td>${part.originalName?.encodeAsHTML() ?: '—'}</td>
                  <td>${part.label?.encodeAsHTML() ?: '—'}</td>
                  <td>${part.majorVersion ?: 1}.${part.minorVersion ?: 0}</td>
                  <td>
                    <g:form action="addPart" id="${workspace.id}" method="POST">
                      <g:hiddenField name="partId" value="${part.id}"/>
                      <g:hiddenField name="inventoryRevision" value="${workspace.inventoryRevision}"/>
                      <button type="submit" class="pmw-btn pmw-primary">+ Add to Workspace</button>
                    </g:form>
                  </td>
                </tr>
              </g:each>
              <g:if test="${!searchResults}"><tr><td colspan="4" class="pmw-muted">No matching PLM parts found.</td></tr></g:if>
            </tbody>
          </table>
        </div>
      </g:if>
      <g:else>
        <div class="pmw-card-content pmw-muted">Enter at least two characters to search for parts.</div>
      </g:else>
    </section>
  </g:if>

  <g:if test="${isOwner}">
    <section class="pmw-card" id="share-workspace">
      <div class="pmw-card-title"><h2>Manage Access / Share Workspace</h2></div>
      <div class="pmw-card-content">
        <g:form action="setMember" id="${workspace.id}" method="POST" class="pmw-form-grid">
          <div><label for="pmw-username">Username</label><input id="pmw-username" name="username" required/></div>
          <div><label for="pmw-role">Role</label><select id="pmw-role" name="role"><option value="VIEWER">Viewer</option><option value="EDITOR">Editor</option></select></div>
          <button type="submit" class="pmw-btn pmw-primary">Grant / Update Access</button>
        </g:form>
      </div>
      <div class="pmw-table-scroll">
        <table>
          <thead><tr><th>User</th><th>Role</th><th>Actions</th></tr></thead>
          <tbody>
            <g:each in="${members}" var="m">
              <tr>
                <td>${m.user?.username?.encodeAsHTML() ?: '—'}</td>
                <td>${m.accessRole}</td>
                <td>
                  <g:form action="removeMember" id="${workspace.id}" method="POST">
                    <g:hiddenField name="userId" value="${m.user?.id}"/>
                    <button type="submit" class="pmw-btn pmw-danger" onclick="return confirm('Revoke this user’s workspace access?')">Revoke</button>
                  </g:form>
                </td>
              </tr>
            </g:each>
            <g:if test="${!members}"><tr><td colspan="3" class="pmw-muted">No additional users have access.</td></tr></g:if>
          </tbody>
        </table>
      </div>
    </section>
    <section class="pmw-card">
      <div class="pmw-card-title"><h2>Workspace Administration</h2></div>
      <div class="pmw-card-content">
        <g:form action="delete" id="${workspace.id}" method="POST">
          <button type="submit" class="pmw-btn pmw-danger" onclick="return confirm('Delete this workspace? This cannot be undone.')">Delete Workspace</button>
        </g:form>
      </div>
    </section>
  </g:if>
</div>
</body>
</html>