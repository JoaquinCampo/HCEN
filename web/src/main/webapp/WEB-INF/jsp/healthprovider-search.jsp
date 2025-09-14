<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="java.util.List" %>
        <%@ page import="grupo12.practico.model.HealthProvider" %>
            <html>

            <head>
                <title>Search Health Providers</title>
            </head>

            <body>
                <h1>Search Health Providers</h1>

                <form method="get" action="<%= request.getContextPath() %>/healthproviders/search">
                    <input type="text" name="name" placeholder="Search by name" value="<%= request.getAttribute("
                        searchName") !=null ? request.getAttribute("searchName") : "" %>">
                    <button type="submit">Search</button>
                    <a href="<%= request.getContextPath() %>/healthproviders">Back to List</a>
                </form>

                <% List<HealthProvider> list = (List<HealthProvider>) request.getAttribute("healthProviders");
                        if (list == null || list.isEmpty()) { %>
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
                                            <td>
                                                <%= hp.getId() %>
                                            </td>
                                            <td>
                                                <%= hp.getName() !=null ? hp.getName() : "N/A" %>
                                            </td>
                                            <td>
                                                <%= hp.getAddress() !=null ? hp.getAddress() : "N/A" %>
                                            </td>
                                            <td>
                                                <%= hp.getPhone() !=null ? hp.getPhone() : "N/A" %>
                                            </td>
                                            <td>
                                                <%= hp.getEmail() !=null ? hp.getEmail() : "N/A" %>
                                            </td>
                                            <td>
                                                <%= hp.isActive() ? "Yes" : "No" %>
                                            </td>
                                        </tr>
                                        <% } %>
                                </tbody>
                            </table>
                            <% } %>

            </body>

            </html>