<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Add User</title>
  </head>
  <body>
    <h1>Add User</h1>

    <% String error = (String) request.getAttribute("error"); if (error != null)
    { %>
    <div style="color: red"><%= error %></div>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/users/add">
      <label>First Name: <input type="text" name="firstName" required /></label
      ><br />
      <label>Last Name: <input type="text" name="lastName" required /></label
      ><br />
      <label>DNI: <input type="text" name="dni" required /></label><br />
      <label
        >Date of Birth: <input type="date" name="dateOfBirth" required /></label
      ><br />
      <label
        >Gender:
        <select name="gender" required>
          <% Object genders = request.getAttribute("genders"); if (genders !=
          null) { for (grupo12.practico.model.Gender g :
          (grupo12.practico.model.Gender[]) genders) { %>
          <option value="<%= g.name() %>"><%= g.name() %></option>
          <% } } %>
        </select> </label
      ><br />
      <label>Email: <input type="email" name="email" /></label><br />
      <label>Phone: <input type="text" name="phone" /></label><br />
      <label>Address: <input type="text" name="address" /></label><br />
      <button type="submit">Save</button>
      <a href="<%= request.getContextPath() %>/users">Back to List</a>
    </form>
  </body>
</html>
