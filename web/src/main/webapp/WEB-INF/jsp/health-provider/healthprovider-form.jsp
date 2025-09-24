<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Add Health Provider</title>
    <style>
      body {
        font-family: sans-serif;
        max-width: 800px;
        margin: 24px auto;
        padding: 0 16px;
      }
      form label {
        display: block;
        margin: 12px 0 4px 0;
        font-weight: 500;
      }
      input[type="text"],
      input[type="email"],
      input[type="date"] {
        width: 100%;
        max-width: 400px;
        padding: 8px;
        border: 1px solid #ccc;
        border-radius: 4px;
        font-size: 14px;
      }
      input[type="checkbox"] {
        margin-right: 8px;
      }
      button {
        background: #1976d2;
        color: white;
        border: none;
        padding: 10px 20px;
        border-radius: 4px;
        cursor: pointer;
        margin: 16px 8px 0 0;
      }
      button:hover {
        background: #1565c0;
      }
      .error {
        color: #b00020;
        background: #ffebee;
        padding: 8px;
        border-radius: 4px;
        margin: 8px 0;
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
    <h1>Add Health Provider</h1>

    <% String error = (String) request.getAttribute("error"); if (error != null)
    { %>
    <div class="error"><%= error %></div>
    <% } %>

    <form
      method="post"
      action="<%= request.getContextPath() %>/healthproviders/add"
    >
      <label
        >Name:
        <input type="text" name="name" required />
      </label>

      <label
        >Address:
        <input type="text" name="address" required />
      </label>

      <label
        >Phone:
        <input type="text" name="phone" />
      </label>

      <label
        >Email:
        <input type="email" name="email" />
      </label>

      <label
        >Registration Number:
        <input type="text" name="registrationNumber" />
      </label>

      <label
        >Clinic Type:
        <select name="type" required>
          <option value="">-- Select Type --</option>
          <option value="HOSPITAL">Hospital</option>
          <option value="POLYCLINIC">Polyclinic</option>
          <option value="PRIVATE_PRACTICE">Private Practice</option>
          <option value="LABORATORY">Laboratory</option>
          <option value="DIAGNOSTIC_CENTER">Diagnostic Center</option>
          <option value="SPECIALTY_CLINIC">Specialty Clinic</option>
          <option value="EMERGENCY_ROOM">Emergency Room</option>
          <option value="REHABILITATION_CENTER">Rehabilitation Center</option>
          <option value="NURSING_HOME">Nursing Home</option>
          <option value="PHARMACY">Pharmacy</option>
        </select>
      </label>

      <label
        >Registration Date:
        <input type="date" name="registrationDate" />
      </label>

      <label>
        <input type="checkbox" name="active" checked />
        Active
      </label>

      <div>
        <button type="submit">Save</button>
      </div>
    </form>

    <div class="nav-links">
      <a href="<%= request.getContextPath() %>/healthproviders">Back to List</a>
      <a href="<%= request.getContextPath() %>/">Home</a>
    </div>
  </body>
</html>
