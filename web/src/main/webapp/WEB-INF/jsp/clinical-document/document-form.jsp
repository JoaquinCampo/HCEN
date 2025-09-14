<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ page
import="java.util.List" %> <%@ page import="grupo12.practico.model.User" %> <%@
page import="grupo12.practico.model.HealthWorker" %> <%@ page
import="grupo12.practico.model.HealthProvider" %> <% List<User>
  users = (List<User
    >) request.getAttribute("users"); List<HealthWorker>
      healthWorkers = (List<HealthWorker
        >) request.getAttribute("healthWorkers"); List<HealthProvider>
          healthProviders = (List<HealthProvider
            >) request.getAttribute("healthProviders"); String error = (String)
            request.getAttribute("error"); %>
            <html>
              <head>
                <title>Add Clinical Document</title>
                <style>
                  body {
                    font-family: sans-serif;
                    max-width: 900px;
                    margin: 24px auto;
                  }
                  form label {
                    display: block;
                    margin: 8px 0;
                  }
                  input[type="text"],
                  textarea,
                  select {
                    width: 100%;
                    max-width: 600px;
                  }
                  .actions {
                    margin-top: 12px;
                  }
                  .error {
                    color: #b00020;
                    margin: 8px 0;
                  }
                  a {
                    margin-left: 8px;
                  }
                </style>
              </head>
              <body>
                <h1>Add Clinical Document</h1>
                <% if (error != null) { %>
                <div class="error"><%= error %></div>
                <% } %>

                <form
                  method="post"
                  action="<%= request.getContextPath() %>/documents/add"
                >
                  <label
                    >Title
                    <input type="text" name="title" required />
                  </label>
                  <label
                    >Content
                    <textarea
                      name="content"
                      rows="8"
                      cols="60"
                      required
                    ></textarea>
                  </label>

                  <label
                    >Patient
                    <select name="userId" required>
                      <% if (users != null) { for (User u : users) { %>
                      <option value="<%= u.getId() %>">
                        <%= u.getLastName() %>, <%= u.getFirstName() %>
                      </option>
                      <% } } %>
                    </select>
                  </label>

                  <label
                    >Author (Health Worker)
                    <select name="authorId" required>
                      <% if (healthWorkers != null) { for (HealthWorker hw :
                      healthWorkers) { %>
                      <option value="<%= hw.getId() %>">
                        <%= hw.getLastName() %>, <%= hw.getFirstName() %>
                      </option>
                      <% } } %>
                    </select>
                  </label>

                  <label
                    >Provider (Custodian)
                    <select name="providerId" required>
                      <% if (healthProviders != null) { for (HealthProvider hp :
                      healthProviders) { %>
                      <option value="<%= hp.getId() %>">
                        <%= hp.getName() %>
                      </option>
                      <% } } %>
                    </select>
                  </label>

                  <div class="actions">
                    <button type="submit">Save</button>
                    <a href="<%= request.getContextPath() %>/documents"
                      >Back to List</a
                    >
                  </div>
                </form>
              </body>
            </html>
          </HealthProvider></HealthProvider
        ></HealthWorker
      ></HealthWorker
    ></User
  ></User
>
