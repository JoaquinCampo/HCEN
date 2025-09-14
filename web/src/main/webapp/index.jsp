<!DOCTYPE html>
<html>
  <head>
    <title>Practico - Healthcare Management System</title>
    <meta charset="UTF-8" />
    <style>
      body {
        font-family: sans-serif;
        max-width: 800px;
        margin: 24px auto;
        padding: 0 16px;
        line-height: 1.6;
      }
      h1 {
        color: #1976d2;
        border-bottom: 2px solid #1976d2;
        padding-bottom: 8px;
      }
      .subtitle {
        color: #666;
        font-style: italic;
        margin-bottom: 32px;
      }
      h2 {
        color: #333;
        margin-top: 32px;
        margin-bottom: 16px;
      }
      ul {
        list-style: none;
        padding: 0;
      }
      li {
        margin: 8px 0;
      }
      a {
        display: inline-block;
        padding: 8px 16px;
        background: #1976d2;
        color: white;
        text-decoration: none;
        border-radius: 4px;
        font-size: 14px;
        min-width: 200px;
        text-align: center;
      }
      a:hover {
        background: #1565c0;
      }
      .section {
        margin-bottom: 32px;
        padding: 16px;
        background: #f9f9f9;
        border-radius: 8px;
      }
    </style>
  </head>

  <body>
    <h1>Healthcare Management System</h1>
    <p class="subtitle">Taller de Sistemas Empresariales - Practico 1 (2025)</p>

    <div class="section">
      <h2>User Management</h2>
      <ul>
        <li>
          <a href="<%= request.getContextPath() %>/users">View All Users</a>
        </li>
        <li>
          <a href="<%= request.getContextPath() %>/users/add">Add User</a>
        </li>
        <li>
          <a href="<%= request.getContextPath() %>/users/search"
            >Search Users</a
          >
        </li>
      </ul>
    </div>

    <div class="section">
      <h2>Health Worker Management</h2>
      <ul>
        <li>
          <a href="<%= request.getContextPath() %>/healthworkers"
            >View All Health Workers</a
          >
        </li>
        <li>
          <a href="<%= request.getContextPath() %>/healthworkers/add"
            >Add Health Worker</a
          >
        </li>
        <li>
          <a href="<%= request.getContextPath() %>/healthworkers/search"
            >Search Health Workers</a
          >
        </li>
      </ul>
    </div>

    <div class="section">
      <h2>Health Provider Management</h2>
      <ul>
        <li>
          <a href="<%= request.getContextPath() %>/healthproviders"
            >View All Providers</a
          >
        </li>
        <li>
          <a href="<%= request.getContextPath() %>/healthproviders/add"
            >Add Provider</a
          >
        </li>
        <li>
          <a href="<%= request.getContextPath() %>/healthproviders/search"
            >Search Providers</a
          >
        </li>
      </ul>
    </div>

    <div class="section">
      <h2>Clinical Documents</h2>
      <ul>
        <li>
          <a href="<%= request.getContextPath() %>/documents"
            >View All Documents</a
          >
        </li>
        <li>
          <a href="<%= request.getContextPath() %>/documents/add"
            >Add Document</a
          >
        </li>
        <li>
          <a href="<%= request.getContextPath() %>/documents/search"
            >Search Documents</a
          >
        </li>
      </ul>
    </div>
  </body>
</html>
