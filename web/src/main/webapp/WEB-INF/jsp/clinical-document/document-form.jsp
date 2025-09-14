<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ page
import="java.util.List" %> <%@ page import="grupo12.practico.models.User" %> <%@
page import="grupo12.practico.models.HealthWorker" %> <%@ page
import="grupo12.practico.models.HealthProvider" %> <% List<User>
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
                    padding: 0 16px;
                  }
                  form label {
                    display: block;
                    margin: 12px 0 4px 0;
                    font-weight: 500;
                  }
                  input[type="text"],
                  textarea,
                  select {
                    width: 100%;
                    max-width: 600px;
                    padding: 8px;
                    border: 1px solid #ccc;
                    border-radius: 4px;
                    font-size: 14px;
                  }
                  textarea {
                    resize: vertical;
                    min-height: 120px;
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
                <h1>Add Clinical Document</h1>

                <% if (error != null) { %>
                <div class="error"><%= error %></div>
                <% } %>

                <form
                  method="post"
                  action="<%= request.getContextPath() %>/documents/add"
                >
                  <label
                    >Title:
                    <input type="text" name="title" required />
                  </label>

                  <label
                    >Content:
                    <textarea
                      name="content"
                      rows="8"
                      cols="60"
                      required
                    ></textarea>
                  </label>

                  <label
                    >Patient:
                    <select name="userId" required>
                      <% if (users != null) { for (User u : users) { %>
                      <option value="<%= u.getId() %>">
                        <%= u.getLastName() %>, <%= u.getFirstName() %>
                      </option>
                      <% } } %>
                    </select>
                  </label>

                  <label
                    >Author (Health Worker) - optional:
                    <select name="authorId">
                      <option value="">-- Select Health Worker --</option>
                      <% if (healthWorkers != null) { for (HealthWorker hw :
                      healthWorkers) { %>
                      <option value="<%= hw.getId() %>">
                        <%= hw.getLastName() %>, <%= hw.getFirstName() %>
                      </option>
                      <% } } %>
                    </select>
                  </label>

                  <label
                    >Provider (Custodian) - optional:
                    <select name="providerId">
                      <option value="">-- Select Health Provider --</option>
                      <% if (healthProviders != null) { for (HealthProvider hp :
                      healthProviders) { %>
                      <option value="<%= hp.getId() %>">
                        <%= hp.getName() %>
                      </option>
                      <% } } %>
                    </select>
                  </label>

                  <div>
                    <button type="submit">Save</button>
                  </div>
                </form>

                <div class="nav-links">
                  <a href="<%= request.getContextPath() %>/documents"
                    >Back to List</a
                  >
                  <a href="<%= request.getContextPath() %>/">Home</a>
                </div>
              </body>
            </html></HealthProvider
          ></HealthProvider
        ></HealthWorker
      ></HealthWorker
    ></User
  ></User
>
