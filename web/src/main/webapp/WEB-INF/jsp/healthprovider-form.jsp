<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>

    <head>
        <title>Add Health Provider</title>
    </head>

    <body>
        <h1>Add Health Provider</h1>

        <% String error=(String) request.getAttribute("error"); if (error !=null) { %>
            <div>
                <%= error %>
            </div>
            <% } %>

                <form method="post" action="<%= request.getContextPath() %>/healthproviders/add">
                    <label>Name: <input type="text" name="name" required /></label><br />
                    <label>Address: <input type="text" name="address" required /></label><br />
                    <label>Phone: <input type="text" name="phone" /></label><br />
                    <label>Email: <input type="email" name="email" /></label><br />
                    <label>Registration Number: <input type="text" name="registrationNumber" /></label><br />
                    <label>Registration Date: <input type="date" name="registrationDate" /></label><br />
                    <label>Active: <input type="checkbox" name="active" checked /></label><br />
                    <button type="submit">Save</button>
                    <a href="<%= request.getContextPath() %>/healthproviders">Back to List</a>
                </form>
    </body>

    </html>