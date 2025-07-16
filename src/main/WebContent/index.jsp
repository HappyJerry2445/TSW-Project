<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Homepage - CardHaven" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/homepage.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/products.css" type="text/css">
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main>
    <!-- Sezione Hero -->
    <section class="hero">
        <div class="hero-content container">
            <h1>Il Tuo Paradiso delle Carte</h1>
            <p>Esplora la nostra vasta collezione di carte collezionabili e accessori. La tua prossima grande scoperta ti aspetta!</p>
            <a href="${pageContext.request.contextPath}/products/search" class="btn btn-primary btn-lg">Scopri la Collezione</a>
        </div>
    </section>

    <!-- Sezione Nuovi Arrivi -->
    <section class="product-showcase container mt-3">
        <h2>Nuovi Arrivi</h2>
        <c:choose>
            <c:when test="${not empty newestProducts}">
                <div class="products-grid">
                    <c:forEach var="product" items="${newestProducts}">
                        <%-- Set attributes for the product_card component --%>
                        <c:set var="product" value="${product}" scope="request"/>
                        <c:set var="productImageId" value="${newestProductImages[product.productId]}" scope="request"/>
                        <%-- Check if the new product is also on sale --%>
                        <c:set var="onSale" value="${onSaleProductIds.contains(product.productId)}" scope="request"/>
                        <jsp:include page="/WEB-INF/components/product_card.jsp"/>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p class="text-center">Al momento non ci sono nuovi arrivi. Torna a trovarci presto!</p>
            </c:otherwise>
        </c:choose>
    </section>

    <!-- Sezione Prodotti in Offerta -->
    <section class="product-showcase container mt-3">
        <h2>In Offerta Speciale</h2>
        <c:choose>
            <c:when test="${not empty onSaleProducts}">
                <div class="products-grid">
                    <c:forEach var="product" items="${onSaleProducts}">
                        <%-- Set attributes for the product_card component --%>
                        <c:set var="product" value="${product}" scope="request"/>
                        <c:set var="productImageId" value="${onSaleProductImages[product.productId]}" scope="request"/>
                        <%-- Products in this section are always on sale --%>
                        <c:set var="onSale" value="${true}" scope="request"/>
                        <jsp:include page="/WEB-INF/components/product_card.jsp"/>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p class="text-center">Nessun prodotto in offerta al momento. Controlla più tardi!</p>
            </c:otherwise>
        </c:choose>
    </section>

</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
<script src="${pageContext.request.contextPath}/scripts/homepage.js"></script>
<script src="${pageContext.request.contextPath}/scripts/async-cart.js"></script>
</body>
</html>
