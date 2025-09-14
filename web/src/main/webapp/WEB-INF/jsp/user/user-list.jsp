<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="grupo12.practico.model.User" %>
<%@ page import="grupo12.practico.model.HealthWorker" %>
<html>
<head>
    <title>Users</title>
    <style>
        table, th, td { border: 1px solid #ccc; border-collapse: collapse; }
        th, td { padding: 6px 10px; }
    </style>
    </head>
<body>
<h1>Users</h1>

<form method="get" action="<%= request.getContextPath() %>/users/search">
    <input type="text" name="q" placeholder="Search by name">
    <button type="submit">Search</button>
    <a href="<%= request.getContextPath() %>/users/add">Add User</a>
 </form>

<%
    List<User> users = (List<User>) request.getAttribute("users");
    if (users == null || users.isEmpty()) { %>
    <p>No users found.</p>
<%  } else { %>
    <table>
        <thead>
        <tr>
            <th>UUID</th>
            <th>DNI</th>
            <th>Name</th>
            <th>Date of Birth</th>
            <th>Gender</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Address</th>
            <th>Health Workers</th>
        </tr>
        </thead>
        <tbody>
        <% for (User u : users) { %>
            <tr>
                <td><%= u.getId() %></td>
                <td><%= u.getDni() %></td>
                <td><%= u.getLastName() %>, <%= u.getFirstName() %></td>
                <td><%= u.getDateOfBirth() %></td>
                <td><%= u.getGender() %></td>
                <td><%= u.getEmail() %></td>
                <td><%= u.getPhone() %></td>
                <td><%= u.getAddress() %></td>
                <td>
                    <% for (HealthWorker hw : u.getHealthWorkers()) { %>
                        <%= hw.getLastName() %>, <%= hw.getFirstName() %><br />
                    <% } %>
                </td>
            </tr>
        <% } %>
        </tbody>
    </table>
<% } %>

</body>
</html>


