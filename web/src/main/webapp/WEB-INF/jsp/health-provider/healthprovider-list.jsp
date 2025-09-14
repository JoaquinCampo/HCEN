<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="grupo12.practico.models.HealthProvider" %>
<%@ page import="grupo12.practico.models.User" %>
<%@ page import="grupo12.practico.models.HealthWorker" %>
<html>
<head>
    <title>Health Providers</title>
    <style>
        body {
            font-family: sans-serif;
            max-width: 1400px;
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
        .toolbar {
            margin: 16px 0;
        }
        .toolbar a {
            display: inline-block;
            margin-right: 12px;
            padding: 8px 16px;
            background: #1976d2;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            font-size: 14px;
        }
        .toolbar a:hover {
            background: #1565c0;
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
        .search-form button {
            padding: 8px 16px;
            background: #1976d2;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .search-form button:hover {
            background: #1565c0;
        }
        .stats {
            margin: 16px 0;
            padding: 12px;
            background: #e3f2fd;
            border-radius: 4px;
            font-weight: 500;
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
    <h1>Health Providers</h1>

    <div class="toolbar">
        <a href="<%= request.getContextPath() %>/healthproviders/add">Add Health Provider</a>
        <a href="<%= request.getContextPath() %>/healthproviders/search">Search Health Providers</a>
    </div>

    <div class="search-form">
        <form method="get" action="<%= request.getContextPath() %>/healthproviders/search">
            <input type="text" name="name" placeholder="Search by name">
            <button type="submit">Search</button>
        </form>
    </div>

    <% List<HealthProvider> list = (List<HealthProvider>) request.getAttribute("healthProviders");
        if (list == null || list.isEmpty()) { %>
        <p>No health providers found.</p>
    <% } else { %>
        <div class="stats">
            <strong>Total Health Providers:</strong> <%= list.size() %>
            <strong>Active:</strong> <%= list.stream().filter(HealthProvider::isActive).count() %>
        </div>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Address</th>
                    <th>Phone</th>
                    <th>Email</th>
                    <th>Registration Date</th>
                    <th>Active</th>
                    <th>Patients</th>
                    <th>Health Workers</th>
                </tr>
            </thead>
            <tbody>
                <% for (HealthProvider hp : list) { %>
                    <tr>
                        <td><%= hp.getId() %></td>
                        <td><%= hp.getName() != null ? hp.getName() : "N/A" %></td>
                        <td><%= hp.getAddress() != null ? hp.getAddress() : "N/A" %></td>
                        <td><%= hp.getPhone() != null ? hp.getPhone() : "N/A" %></td>
                        <td><%= hp.getEmail() != null ? hp.getEmail() : "N/A" %></td>
                        <td><%= hp.getRegistrationDate() != null ? hp.getRegistrationDate() : "N/A" %></td>
                        <td><%= hp.isActive() ? "Yes" : "No" %></td>
                        <td>
                            <% if (hp.getAttendedPatients() != null) { %>
                                <%= hp.getAttendedPatients().size() %> attended
                            <% } else { %>
                                0
                            <% } %>
                            <% if (hp.getAffiliatedPatients() != null) { %>
                                <br><%= hp.getAffiliatedPatients().size() %> affiliated
                            <% } %>
                        </td>
                        <td><%= hp.getHealthWorkers() != null ? hp.getHealthWorkers().size() : 0 %></td>
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