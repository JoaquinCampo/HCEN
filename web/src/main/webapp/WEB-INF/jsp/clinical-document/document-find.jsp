<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="grupo12.practico.models.ClinicalDocument" %>
<html>
  <head>
    <title>Search Clinical Documents</title>
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
      th, td {
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
      .search-form {
        margin: 16px 0;
        padding: 16px;
        background: #f9f9f9;
        border-radius: 4px;
      }
      .search-form input[type="text"] {
        padding: 8px;
        border: 1px solid #ccc;
        border-radius: 4px;
        margin: 8px 0;
        width: 300px;
        display: block;
      }
      .search-form button {
        padding: 8px 16px;
        background: #1976d2;
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        margin: 8px 0;
      }
      .search-form button:hover {
        background: #1565c0;
      }
      .radio-group {
        margin: 12px 0;
      }
      .radio-group label {
        display: inline-block;
        margin-right: 16px;
        margin-bottom: 8px;
      }
      .radio-group input[type="radio"] {
        margin-right: 4px;
      }
      .results-count {
        margin: 16px 0;
        font-weight: 500;
        color: #666;
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
      hr {
        margin: 24px 0;
        border: none;
        border-top: 1px solid #ddd;
      }
    </style>
  </head>
  <body>
    <h1>Search Clinical Documents</h1>
    
    <div class="search-form">
      <form method="get" action="<%= request.getContextPath() %>/documents/search">
        <p>Search by name. Choose the scope (patient, author, provider) and enter a name:</p>
        
        <input type="text" name="q" placeholder="e.g. Smith" value="<%= request.getAttribute("q") != null ? request.getAttribute("q") : (request.getParameter("q") != null ? request.getParameter("q") : "") %>">
        
        <div class="radio-group">
          <label><input type="radio" name="scope" value="patient" <%= "patient".equals(String.valueOf(request.getAttribute("scope") != null ? request.getAttribute("scope") : request.getParameter("scope") != null ? request.getParameter("scope") : "patient")) ? "checked" : "" %>> Patient</label>
          <label><input type="radio" name="scope" value="author" <%= "author".equals(String.valueOf(request.getAttribute("scope") != null ? request.getAttribute("scope") : request.getParameter("scope"))) ? "checked" : "" %>> Author</label>
          <label><input type="radio" name="scope" value="provider" <%= "provider".equals(String.valueOf(request.getAttribute("scope") != null ? request.getAttribute("scope") : request.getParameter("scope"))) ? "checked" : "" %>> Provider</label>
          <label><input type="radio" name="scope" value="all" <%= "all".equals(String.valueOf(request.getAttribute("scope") != null ? request.getAttribute("scope") : request.getParameter("scope"))) ? "checked" : "" %>> Any</label>
        </div>
        
        <button type="submit">Search</button>
      </form>
    </div>

    <hr/>

    <%
      List<ClinicalDocument> documents = (List<ClinicalDocument>) request.getAttribute("documents");
      String query = (String) request.getAttribute("q");
      if (documents != null) {
        if (query != null && !query.trim().isEmpty()) { %>
          <div class="results-count">
            Found <%= documents.size() %> document(s) for "<%= query %>"
          </div>
        <% }
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
                <td><%= d.getClinicalHistory() != null && d.getClinicalHistory().getPatient() != null ? (d.getClinicalHistory().getPatient().getLastName()+", "+d.getClinicalHistory().getPatient().getFirstName()) : "-" %></td>
                <td><%= d.getAuthor() != null ? (d.getAuthor().getLastName()+", "+d.getAuthor().getFirstName()) : "-" %></td>
                <td><%= d.getProvider() != null ? d.getProvider().getName() : "-" %></td>
              </tr>
            <% } %>
            </tbody>
          </table>
        <% }
      }
    %>
    
    <div class="nav-links">
      <a href="<%= request.getContextPath() %>/documents">Back to List</a>
      <a href="<%= request.getContextPath() %>/">Home</a>
    </div>
  </body>
</html>