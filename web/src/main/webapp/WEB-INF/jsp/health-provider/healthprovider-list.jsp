<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="java.util.List" %>
        <%@ page import="grupo12.practico.model.HealthProvider" %>
            <%@ page import="grupo12.practico.model.User" %>
                <%@ page import="grupo12.practico.model.HealthWorker" %>
                    <html>

                    <head>
                        <title>Health Providers</title>
                        <style>
                          body { font-family: sans-serif; max-width: 1000px; margin: 24px auto; }
                          table { width: 100%; border-collapse: collapse; }
                          th, td { border: 1px solid #ddd; padding: 8px; }
                          th { text-align: left; }
                          .action-links a { margin-right: 8px; }
                          .search-form { margin: 12px 0; }
                        </style>
                    </head>

                    <body>
                        <h1>Health Providers</h1>

                        <div class="action-links">
                            <a href="<%= request.getContextPath() %>/healthproviders/add">Add Health Provider</a>
                            <a href="<%= request.getContextPath() %>/healthproviders/search">Search Health Providers</a>
                            <a href="<%= request.getContextPath() %>/">Back to Home</a>
                        </div>

                        <div class="search-form">
                            <form method="get" action="<%= request.getContextPath() %>/healthproviders/search">
                                <input type="text" name="name" placeholder="Search by name" style="width: 200px;">
                                <button type="submit">Search</button>
                            </form>
                        </div>

                        <% List<HealthProvider> list = (List<HealthProvider>) request.getAttribute("healthProviders");
                                if (list == null || list.isEmpty()) { %>
                                <p>No health providers found.</p>
                                <% } else { %>
                                    <div class="stats">
                                        <strong>Total Health Providers:</strong>
                                        <%= list.size() %>
                                            <strong>Active:</strong>
                                            <%= list.stream().filter(HealthProvider::isActive).count() %>
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
                                                        <%= hp.getRegistrationDate() !=null ? hp.getRegistrationDate()
                                                            : "N/A" %>
                                                    </td>
                                                    <td>
                                                        <%= hp.isActive() ? "Yes" : "No" %>
                                                    </td>
                                                    <td>
                                                        <% if (hp.getAttendedPatients() !=null) { %>
                                                            <%= hp.getAttendedPatients().size() %> attended
                                                                <% } else { %>
                                                                    0
                                                                    <% } %>
                                                                        <% if (hp.getAffiliatedPatients() !=null) { %>
                                                                            <br>
                                                                            <%= hp.getAffiliatedPatients().size() %>
                                                                                affiliated
                                                                                <% } %>
                                                    </td>
                                                    <td>
                                                        <%= hp.getHealthWorkers() !=null ? hp.getHealthWorkers().size()
                                                            : 0 %>
                                                    </td>
                                                </tr>
                                                <% } %>
                                        </tbody>
                                    </table>
                                    <% } %>

                    </body>

                    </html>