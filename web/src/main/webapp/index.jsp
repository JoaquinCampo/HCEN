<!DOCTYPE html>
<html>

<head>
  <title>Practico - Healthcare Management System</title>
  <meta charset="UTF-8" />
</head>

<body>
  <h1>Healthcare Management System</h1>
  <p>Taller de Sistemas Empresariales - Practico 1 (2025)</p>

  <h2>User Management</h2>
  <ul>
    <li><a href="<%= request.getContextPath() %>/users">View All Users</a></li>
    <li><a href="<%= request.getContextPath() %>/users/add">Add User</a></li>
    <li><a href="<%= request.getContextPath() %>/users/search">Search Users</a></li>
  </ul>

  <h2>Health Worker Management</h2>
  <ul>
    <li><a href="<%= request.getContextPath() %>/healthworkers">View All Health Workers</a></li>
    <li><a href="<%= request.getContextPath() %>/healthworkers/add">Add Health Worker</a></li>
    <li><a href="<%= request.getContextPath() %>/healthworkers/search">Search Health Workers</a></li>
  </ul>

  <h2>Health Provider Management</h2>
  <ul>
    <li><a href="<%= request.getContextPath() %>/healthproviders">View All Providers</a></li>
    <li><a href="<%= request.getContextPath() %>/healthproviders/add">Add Provider</a></li>
    <li><a href="<%= request.getContextPath() %>/healthproviders/search">Search Providers</a></li>
  </ul>

</body>

</html>