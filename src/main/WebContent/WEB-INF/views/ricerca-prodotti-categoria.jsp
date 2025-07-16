<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Prodotti: ${categoryName}" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle} | CardHaven</title>
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/products.css" type="text/css">
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main class="container">
    <h1 class="category-title">${categoryName}</h1>

    <c:if test="${not empty errors}">
        <div class="alert alert-danger">
            <c:forEach var="error" items="${errors}">
                <p>${error}</p>
            </c:forEach>
        </div>
    </c:if>

    <div class="products-grid">
        <c:choose>
            <c:when test="${not empty products}">
                <c:forEach var="product" items="${products}">
                    <%-- Set up attributes required by the product_card component --%>
                    <c:set var="product" value="${product}" scope="request"/>
                    <c:set var="productImageId" value="${productImages[product.productId].imageId}" scope="request"/>
                    <c:set var="onSale" value="${onSaleProductIds.contains(product.productId)}" scope="request"/>
                    <jsp:include page="/WEB-INF/components/product_card.jsp"/>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="no-products-message">
                    <p>Nessun prodotto disponibile in questa categoria.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
<script src="${pageContext.request.contextPath}/scripts/async-cart.js"></script>
</body>
</html>
