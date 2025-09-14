<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="grupo12.practico.model.User" %>
<html>
<head>
    <title>Search Users</title>
    <style>
        table, th, td { border: 1px solid #ccc; border-collapse: collapse; }
        th, td { padding: 6px 10px; }
    </style>
</head>
<body>
<h1>Search Users</h1>

<form method="get" action="<%= request.getContextPath() %>/users/search">
    <input type="text" name="q" placeholder="Search by name" value="<%= request.getAttribute("q") != null ? request.getAttribute("q") : "" %>">
    <button type="submit">Search</button>
    <a href="<%= request.getContextPath() %>/users">Back to List</a>
</form>

<%
    List<User> users = (List<User>) request.getAttribute("users");
    if (users != null) {
        if (users.isEmpty()) { %>
            <p>No users found.</p>
        <% } else { %>
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
                    </tr>
                <% } %>
                </tbody>
            </table>
        <% }
    } %>

</body>
</html>


