<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Add Health Worker</title>
  </head>
  <body>
    <h1>Add Health Worker</h1>

    <% String error = (String) request.getAttribute("error"); if (error != null) { %>
    <div style="color: red"><%= error %></div>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/healthworkers/add">
      <label>First Name: <input type="text" name="firstName" required /></label><br />
      <label>Last Name: <input type="text" name="lastName" required /></label><br />
      <label>DNI: <input type="text" name="dni" required /></label><br />
      <label>Specialty: <input type="text" name="specialty" /></label><br />
      <label>License Number: <input type="text" name="licenseNumber" required /></label><br />
      <label>Hire Date: <input type="date" name="hireDate" /></label><br />
      <button type="submit">Save</button>
      <a href="<%= request.getContextPath() %>/healthworkers">Back to List</a>
    </form>
  </body>
  </html>


