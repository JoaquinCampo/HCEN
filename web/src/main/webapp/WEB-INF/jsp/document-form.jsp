<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="grupo12.practico.model.User" %>
<%@ page import="grupo12.practico.model.HealthWorker" %>
<%@ page import="grupo12.practico.model.HealthProvider" %>
<%
List<User> users = (List<User>) request.getAttribute("users");
List<HealthWorker> healthWorkers = (List<HealthWorker>) request.getAttribute("healthWorkers");
List<HealthProvider> healthProviders = (List<HealthProvider>) request.getAttribute("healthProviders");
String error = (String) request.getAttribute("error");
%>
<html>
  <head>
    <title>Add Clinical Document</title>
  </head>
  <body>
    <h1>Add Clinical Document</h1>
    <% if (error != null) { %>
      <div style="color:red"><%= error %></div>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/documents/add">
      <label>Title: <input type="text" name="title" required/></label><br/>
      <label>Content:<br/>
        <textarea name="content" rows="8" cols="60" required></textarea>
      </label><br/>

      <label>Patient:
        <select name="userId" required>
          <% if (users != null) { for (User u : users) { %>
          <option value="<%= u.getId() %>"><%= u.getLastName() %>, <%= u.getFirstName() %></option>
          <% } } %>
        </select>
      </label><br/>

      <label>Author (Health Worker):
        <select name="authorId" required>
          <% if (healthWorkers != null) { for (HealthWorker hw : healthWorkers) { %>
          <option value="<%= hw.getId() %>"><%= hw.getLastName() %>, <%= hw.getFirstName() %></option>
          <% } } %>
        </select>
      </label><br/>

      <label>Provider (Custodian):
        <select name="providerId" required>
          <% if (healthProviders != null) { for (HealthProvider hp : healthProviders) { %>
          <option value="<%= hp.getId() %>"><%= hp.getName() %></option>
          <% } } %>
        </select>
      </label><br/>

      <button type="submit">Save</button>
      <a href="<%= request.getContextPath() %>/documents">Back to List</a>
    </form>
  </body>
  </html>
