<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %>

<c:set var="pageTitle" value="Ricerca Prodotti per Categoria" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/user-auth.css" type="text/css">
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main class="products-container">
    <h1>Prodotti: ${categoryName}</h1>

    <div class="products-grid">
        <c:forEach items="${products}" var="product">
            <div class="product-card"></div>
                <img src="${pageContext.request.contextPath}/images/${product.image}" alt="${product.name}">
        </c:forEach>
    </div>

</main>
</body>
</html>
