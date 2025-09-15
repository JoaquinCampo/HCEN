<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ page
import="java.util.List" %> <%@ page
import="grupo12.practico.models.ClinicalDocument" %> <% List<ClinicalDocument>
  documents = (List<ClinicalDocument
    >) request.getAttribute("documents"); %>
    <html>
      <head>
        <title>Clinical Documents</title>
        <style>
          body {
            font-family: sans-serif;
            max-width: 1200px;
            margin: 24px auto;
            padding: 0 16px;
          }
          table {
            width: 100%;
            border-collapse: collapse;
            margin: 16px 0;
          }
          th,
          td {
            border: 1px solid #ddd;
            padding: 12px 8px;
            text-align: left;
          }
          th {
            background: #f5f5f5;
            font-weight: 600;
          }
          tr:nth-child(even) {
            background: #fafafa;
          }
          .toolbar {
            margin: 16px 0;
          }
          .toolbar a {
            display: inline-block;
            margin-right: 12px;
            padding: 8px 16px;
            background: #1976d2;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            font-size: 14px;
          }
          .toolbar a:hover {
            background: #1565c0;
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
        <h1>Clinical Documents</h1>

        <div class="toolbar">
          <a href="<%= request.getContextPath() %>/documents/add"
            >Add Document</a
          >
          <a href="<%= request.getContextPath() %>/documents/search">Search</a>
        </div>

        <table>
          <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Issued At</th>
            <th>Patient</th>
            <th>Author</th>
            <th>Provider</th>
          </tr>
          <% if (documents != null) { for (ClinicalDocument d : documents) { %>
          <tr>
            <td><%= d.getId() %></td>
            <td><%= d.getTitle() %></td>
            <td><%= d.getIssuedAt() %></td>
            <td>
              <%= d.getClinicalHistory() != null &&
              d.getClinicalHistory().getPatient() != null ?
              d.getClinicalHistory().getPatient().getLastName() + ", " +
              d.getClinicalHistory().getPatient().getFirstName() : "-" %>
            </td>
            <td>
              <%= d.getAuthor() != null ? d.getAuthor().getLastName() + ", " +
              d.getAuthor().getFirstName() : "-" %>
            </td>
            <td>
              <%= d.getProvider() != null ? d.getProvider().getName() : "-" %>
            </td>
          </tr>
          <% } } %>
        </table>

        <div class="nav-links">
          <a href="<%= request.getContextPath() %>/">Home</a>
        </div>
      </body>
    </html></ClinicalDocument
  ></ClinicalDocument
>
