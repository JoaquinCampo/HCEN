<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="grupo12.practico.models.HealthProvider" %>
<html>
<head>
    <title>Search Health Providers</title>
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
    <h1>Search Health Providers</h1>

    <div class="search-form">
        <form method="get" action="<%= request.getContextPath() %>/healthproviders/search">
            <input type="text" name="name" placeholder="Search by name" value="<%= request.getAttribute("searchName") != null ? request.getAttribute("searchName") : "" %>">
            <button type="submit">Search</button>
        </form>
    </div>

    <% 
        List<HealthProvider> list = (List<HealthProvider>) request.getAttribute("healthProviders");
        String searchName = (String) request.getAttribute("searchName");
        if (list != null) {
            if (searchName != null && !searchName.trim().isEmpty()) { %>
                <div class="results-count">
                    Found <%= list.size() %> health provider(s) for "<%= searchName %>"
                </div>
            <% }
            if (list.isEmpty()) { %>
                <p>No health providers found.</p>
            <% } else { %>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Address</th>
                            <th>Phone</th>
                            <th>Email</th>
                            <th>Active</th>
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
                                <td><%= hp.isActive() ? "Yes" : "No" %></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% }
        } %>

    <div class="nav-links">
        <a href="<%= request.getContextPath() %>/healthproviders">Back to List</a>
        <a href="<%= request.getContextPath() %>/">Home</a>
    </div>

</body>
</html>