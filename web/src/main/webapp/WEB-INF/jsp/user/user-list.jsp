<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="grupo12.practico.models.User" %>
<%@ page import="grupo12.practico.models.HealthWorker" %>
<html>
<head>
    <title>Users</title>
    <style>
        body {
            font-family: sans-serif;
            max-width: 1200px;
            margin: 24px auto;
            padding: 0 16px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 16px 0;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 12px 8px;
            text-align: left;
        }
        th {
            background: #f5f5f5;
            font-weight: 600;
        }
        tr:nth-child(even) {
            background: #fafafa;
        }
        .search-form {
            margin: 16px 0;
            padding: 16px;
            background: #f9f9f9;
            border-radius: 4px;
        }
        .search-form input[type="text"] {
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            margin-right: 8px;
            width: 200px;
        }
        .search-form button,
        .toolbar a {
            display: inline-block;
            padding: 8px 16px;
            background: #1976d2;
            color: white;
            text-decoration: none;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            margin-right: 8px;
        }
        .search-form button:hover,
        .toolbar a:hover {
            background: #1565c0;
        }
        .nav-links {
            margin: 16px 0;
        }
        .nav-links a {
            margin-right: 12px;
            color: #1976d2;
            text-decoration: none;
        }
        .nav-links a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<h1>Users</h1>

<div class="search-form">
    <form method="get" action="<%= request.getContextPath() %>/users/search" style="display: inline;">
        <input type="text" name="q" placeholder="Search by name">
        <button type="submit">Search</button>
    </form>
    <a href="<%= request.getContextPath() %>/users/add" class="toolbar">Add User</a>
</div>

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
                <td><%= u.getEmail() != null ? u.getEmail() : "-" %></td>
                <td><%= u.getPhone() != null ? u.getPhone() : "-" %></td>
                <td><%= u.getAddress() != null ? u.getAddress() : "-" %></td>
                <td>
                    <% if (u.getHealthWorkers() != null) {
                        for (HealthWorker hw : u.getHealthWorkers()) { %>
                        <%= hw.getLastName() %>, <%= hw.getFirstName() %><br />
                    <% } } else { %>
                        -
                    <% } %>
                </td>
            </tr>
        <% } %>
        </tbody>
    </table>
<% } %>

<div class="nav-links">
    <a href="<%= request.getContextPath() %>/">Home</a>
</div>

</body>
</html>