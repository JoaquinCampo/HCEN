<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Search Clinical Documents</title>
  </head>
  <body>
    <h1>Search Clinical Documents</h1>
    <form method="post" action="<%= request.getContextPath() %>/documents/search">
      <p>Provide one of the following IDs (patient, author or provider). Precedence: patient > author > provider.</p>
      <label>Patient ID: <input type="text" name="patientId" /></label><br/>
      <label>Author ID: <input type="text" name="authorId" /></label><br/>
      <label>Provider ID: <input type="text" name="providerId" /></label><br/>
      <button type="submit">Search</button>
      <a href="<%= request.getContextPath() %>/documents">Back to List</a>
    </form>
  </body>
  </html>
