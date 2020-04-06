<%@ page import="com.cs122b.movielist.JDBCMovieList" %>
<%--
  Created by IntelliJ IDEA.
  User: Owner
  Date: 4/1/2020
  Time: 8:08 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>$Top 20 Movies List$</title>
</head>
<body>
<h1>Movie</h1>
<%= JDBCMovieList.getTestMessage()%>
</body>
</html>
