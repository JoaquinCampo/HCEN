<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="grupo12.practico.models.HealthWorker" %>
<html>
<head>
    <title>Search Health Workers</title>
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
        .search-form button {
            padding: 8px 16px;
            background: #1976d2;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            margin-right: 8px;
        }
        .search-form button:hover {
            background: #1565c0;
        }
        .results-count {
            margin: 16px 0;
            font-weight: 500;
            color: #666;
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
<h1>Search Health Workers</h1>

<div class="search-form">
    <form method="get" action="<%= request.getContextPath() %>/healthworkers/search">
        <input type="text" name="q" placeholder="Search by name" value="<%= request.getAttribute("q") != null ? request.getAttribute("q") : "" %>">
        <button type="submit">Search</button>
    </form>
</div>

<%
    List<HealthWorker> list = (List<HealthWorker>) request.getAttribute("healthWorkers");
    String query = (String) request.getAttribute("q");
    if (list != null) {
        if (query != null && !query.trim().isEmpty()) { %>
            <div class="results-count">
                Found <%= list.size() %> health worker(s) for "<%= query %>"
            </div>
        <% }
        if (list.isEmpty()) { %>
            <p>No health workers found.</p>
        <% } else { %>
            <table>
                <thead>
                <tr>
                    <th>UUID</th>
                    <th>Document</th>
                    <th>Name</th>
                    <th>Gender</th>
                    <th>Specialty</th>
                    <th>License</th>
                    <th>Hire Date</th>
                </tr>
                </thead>
                <tbody>
                <% for (HealthWorker hw : list) { %>
                    <tr>
                        <td><%= hw.getId() %></td>
                        <td><%= hw.getDni() %></td>
                        <td><%= hw.getLastName() %>, <%= hw.getFirstName() %></td>
                        <td><%= hw.getGender() %></td>
                        <td>
                            <% if (hw.getSpecialties() != null && !hw.getSpecialties().isEmpty()) { %>
                                <%= hw.getSpecialties().iterator().next().getName() %>
                                <% if (hw.getSpecialties().size() > 1) { %> (+<%= hw.getSpecialties().size() - 1 %> more)<% } %>
                            <% } else { %>
                                -
                            <% } %>
                        </td>
                        <td><%= hw.getLicenseNumber() %></td>
                        <td><%= hw.getHireDate() != null ? hw.getHireDate() : "-" %></td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        <% }
    } %>

<div class="nav-links">
    <a href="<%= request.getContextPath() %>/healthworkers">Back to List</a>
    <a href="<%= request.getContextPath() %>/">Home</a>
</div>

</body>
</html>