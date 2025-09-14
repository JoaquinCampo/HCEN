<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="grupo12.practico.model.ClinicalDocument" %>
<html>
  <head>
    <title>Search Clinical Documents</title>
    <style>
      table, th, td { border: 1px solid #ccc; border-collapse: collapse; }
      th, td { padding: 6px 10px; }
    </style>
  </head>
  <body>
    <h1>Search Clinical Documents</h1>
    <form method="get" action="<%= request.getContextPath() %>/documents/search">
      <p>Search by name. Choose the scope (patient, author, provider) and enter a name:</p>
      <input type="text" name="q" placeholder="e.g. Smith" value="<%= request.getAttribute("q") != null ? request.getAttribute("q") : (request.getParameter("q") != null ? request.getParameter("q") : "") %>">
      <br/>
      <label><input type="radio" name="scope" value="patient" <%= "patient".equals(String.valueOf(request.getAttribute("scope") != null ? request.getAttribute("scope") : request.getParameter("scope") != null ? request.getParameter("scope") : "patient")) ? "checked" : "" %>> Patient</label>
      <label><input type="radio" name="scope" value="author" <%= "author".equals(String.valueOf(request.getAttribute("scope") != null ? request.getAttribute("scope") : request.getParameter("scope"))) ? "checked" : "" %>> Author</label>
      <label><input type="radio" name="scope" value="provider" <%= "provider".equals(String.valueOf(request.getAttribute("scope") != null ? request.getAttribute("scope") : request.getParameter("scope"))) ? "checked" : "" %>> Provider</label>
      <label><input type="radio" name="scope" value="all" <%= "all".equals(String.valueOf(request.getAttribute("scope") != null ? request.getAttribute("scope") : request.getParameter("scope"))) ? "checked" : "" %>> Any</label>
      <br/>
      <button type="submit">Search</button>
      <a href="<%= request.getContextPath() %>/documents">Back to List</a>
    </form>

    <hr/>

    <%
      List<ClinicalDocument> documents = (List<ClinicalDocument>) request.getAttribute("documents");
      if (documents != null) {
        if (documents.isEmpty()) { %>
          <p>No documents found.</p>
        <% } else { %>
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Issued At</th>
                <th>Patient</th>
                <th>Author</th>
                <th>Provider</th>
              </tr>
            </thead>
            <tbody>
            <% for (ClinicalDocument d : documents) { %>
              <tr>
                <td><%= d.getId() %></td>
                <td><%= d.getTitle() %></td>
                <td><%= d.getIssuedAt() %></td>
                <td><%= d.getPatient() != null ? (d.getPatient().getLastName()+", "+d.getPatient().getFirstName()) : "-" %></td>
                <td><%= d.getAuthor() != null ? (d.getAuthor().getLastName()+", "+d.getAuthor().getFirstName()) : "-" %></td>
                <td><%= d.getProvider() != null ? d.getProvider().getName() : "-" %></td>
              </tr>
            <% } %>
            </tbody>
          </table>
        <% }
      }
    %>
  </body>
  </html>
