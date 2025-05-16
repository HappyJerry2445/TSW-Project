<%--
  Created by IntelliJ IDEA.
  User: andrea
  Date: 5/16/25
  Time: 11:33 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<html>
<head>
    <title>Categorie</title>
    <style>
        table {
            border-collapse: collapse;
            width: 100%;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 8px;
        }

        tr:nth-child(even) {
            background-color: #f2f2f2;
        }
    </style>
</head>
<body>
<h1>Elenco Categorie</h1>

<table>
    <tr>
        <th>ID</th>
        <th>Nome</th>
        <th>Genitore</th>
        <th>Tipo</th>
    </tr>

    <c:forEach items="${categories}" var="category">
        <tr>
            <td>${category.id}</td>
            <td>${category.name}</td>
            <td>${category.parentId}</td>
            <td>${category.type}</td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
