<div id="header">
  <h2>Users</h2>
</div>
<div id="content">
  <table class="datatable">
    <tr>
      <th>Username</th>
      <th>Email</th>
    </tr>
    <#list users as user>
      <tr>
        <td>${user.username}</td>
        <td>${user.email}</td>
      </tr>
    </#list>
  </table>
</div>