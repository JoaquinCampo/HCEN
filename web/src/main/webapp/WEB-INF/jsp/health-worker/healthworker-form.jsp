<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ page
import="java.util.List" %> <%@ page import="grupo12.practico.models.Gender" %>
<%@ page import="grupo12.practico.models.HealthProvider" %> <%
List<HealthProvider>
  healthProviders = (List<HealthProvider
    >) request.getAttribute("healthProviders"); %>
    <html>
      <head>
        <title>Add Health Worker</title>
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
          input[type="date"],
          select {
            width: 100%;
            max-width: 400px;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 14px;
          }
          select[multiple] {
            height: 120px;
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
        <h1>Add Health Worker</h1>

        <% String error = (String) request.getAttribute("error"); if (error !=
        null) { %>
        <div class="error"><%= error %></div>
        <% } %>

        <form
          method="post"
          action="<%= request.getContextPath() %>/healthworkers/add"
        >
          <label
            >First Name:
            <input type="text" name="firstName" required />
          </label>

          <label
            >Last Name:
            <input type="text" name="lastName" required />
          </label>

          <label
            >DNI:
            <input type="text" name="dni" required />
          </label>

          <label
            >Gender:
            <select name="gender" required>
              <% Object genders = request.getAttribute("genders"); if (genders
              != null) { for (Gender g : (Gender[]) genders) { %>
              <option value="<%= g.name() %>"><%= g.name() %></option>
              <% } } %>
            </select>
          </label>

          <label
            >Specialty:
            <input type="text" name="specialty" />
          </label>

          <label
            >License Number:
            <input type="text" name="licenseNumber" required />
          </label>

          <label
            >Hire Date:
            <input type="date" name="hireDate" />
          </label>

          <label
            >Health Providers (optional):
            <select name="healthProviders" multiple>
              <% if (healthProviders != null) { for (HealthProvider hp :
              healthProviders) { %>
              <option value="<%= hp.getId() %>"><%= hp.getName() %></option>
              <% } } %>
            </select>
          </label>

          <div>
            <button type="submit">Save</button>
          </div>
        </form>

        <div class="nav-links">
          <a href="<%= request.getContextPath() %>/healthworkers"
            >Back to List</a
          >
          <a href="<%= request.getContextPath() %>/">Home</a>
        </div>
      </body>
    </html>
  </HealthProvider></HealthProvider
>
