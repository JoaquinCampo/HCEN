<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ page
import="java.util.List" %> <%@ page
import="grupo12.practico.model.ClinicalDocument" %> <% List<ClinicalDocument>
  documents = (List<ClinicalDocument
    >) request.getAttribute("documents"); %>
    <html>
      <head>
        <title>Clinical Documents</title>
        <style>
          body {
            font-family: sans-serif;
            max-width: 1000px;
            margin: 24px auto;
          }
          table {
            width: 100%;
            border-collapse: collapse;
          }
          th,
          td {
            border: 1px solid #ddd;
            padding: 8px;
          }
          th {
            text-align: left;
          }
          .toolbar a {
            margin-right: 8px;
          }
        </style>
      </head>
      <body>
        <h1>Clinical Documents</h1>
        <p class="toolbar">
          <a href="<%= request.getContextPath() %>/documents/add"
            >Add Document</a
          >
          <a href="<%= request.getContextPath() %>/documents/search">Search</a>
          <a href="<%= request.getContextPath() %>/">Home</a>
        </p>

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
              d.getClinicalHistory().getPatient().getLastName()+",
              "+d.getClinicalHistory().getPatient().getFirstName() : "-" %>
            </td>
            <td>
              <%= d.getAuthor() != null ? d.getAuthor().getLastName()+",
              "+d.getAuthor().getFirstName() : "-" %>
            </td>
            <td>
              <%= d.getProvider() != null ? d.getProvider().getName() : "-" %>
            </td>
          </tr>
          <% } } %>
        </table>
      </body>
    </html>
  </ClinicalDocument></ClinicalDocument
>
