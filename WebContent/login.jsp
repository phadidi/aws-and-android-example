<%--
  Created by IntelliJ IDEA.
  User: Owner
  Date: 4/14/2020
  Time: 9:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Insert title here</title>
</head>
<body>

Keep the page ideal for 30 seconds
<br> And try reloading the page
<br> you will be redirected to home page automatically
<br>


    <%
        if (session != null) {
            if (session.getAttribute("user") != null) {
                String name = (String) session.getAttribute("name");

            } else {
                response.sendRedirect("index.html");
            }
        }
    %>
</br>
</br>
<form action="Logout" method="post">
    <input type="submit" value="Logout">
</form>
</body>
</html>