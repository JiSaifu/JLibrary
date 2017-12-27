<%--
  Created by IntelliJ IDEA.
  User: saifu.ji
  Date: 2017/11/20
  Time: 15:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>查看Parameter内容</title>
</head>
<body>
    <table>
        <tr>
            <td>session id</td>
            <td>${paramMap.sessionId}</td>
        </tr>
        <tr>
            <td>Accept-Encoding</td>
            <td>${paramMap.acceptEncoding}</td>
        </tr>
    </table>
</body>
</html>
