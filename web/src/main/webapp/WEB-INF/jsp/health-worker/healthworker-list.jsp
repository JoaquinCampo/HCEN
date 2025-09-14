<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="grupo12.practico.model.HealthWorker" %>
<html>
<head>
    <title>Health Workers</title>
    <style>
        table, th, td { border: 1px solid #ccc; border-collapse: collapse; }
        th, td { padding: 6px 10px; }
    </style>
    </head>
<body>
<h1>Health Workers</h1>

<form method="get" action="<%= request.getContextPath() %>/healthworkers/search">
    <input type="text" name="q" placeholder="Search by name">
    <button type="submit">Search</button>
    <a href="<%= request.getContextPath() %>/healthworkers/add">Add Health Worker</a>
 </form>

<%
    List<HealthWorker> list = (List<HealthWorker>) request.getAttribute("healthWorkers");
    if (list == null || list.isEmpty()) { %>
    <p>No health workers found.</p>
<%  } else { %>
    <table>
        <thead>
        <tr>
            <th>UUID</th>
            <th>DNI</th>
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
                <td><%= hw.getSpecialty() %></td>
                <td><%= hw.getLicenseNumber() %></td>
                <td><%= hw.getHireDate() %></td>
            </tr>
        <% } %>
        </tbody>
    </table>
<% } %>

</body>
</html>


