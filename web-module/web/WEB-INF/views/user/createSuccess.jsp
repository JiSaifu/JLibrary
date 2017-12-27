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
    <title>用户创建成功</title>
</head>
<body>
    恭喜，用户${user.userName}创建成功。
    <table>
        <th>
            <td>用户名</td>
            <td>密码</td>
            <td>真实姓名</td>
        </th>
        <tr>
            <td>${user.userName}</td>
            <td>${user.password}</td>
            <td>${user.realName}</td>
        </tr>
    </table>
</body>
</html>
