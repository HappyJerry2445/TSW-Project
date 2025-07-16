<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Ricerca Prodotti" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
    <title>${pageTitle} | CardHaven</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/products.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/search.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main class="container mt-3">
    <h1 class="section-title">Ricerca Prodotti</h1>

    <jsp:include page="/WEB-INF/components/error_message.jsp"/>

    <div class="content-card search-filter-card">
        <h2>Filtra la tua ricerca</h2>
        <form action="${pageContext.request.contextPath}/products/search" method="get" class="search-form">
            <div class="form-group">
                <label for="query">Nome prodotto:</label>
                <input type="text" id="query" name="query" placeholder="Nome prodotto" value="${query}">
            </div>
            <div class="form-group">
                <label for="minPrice">Prezzo Minimo (€):</label>
                <input type="number" id="minPrice" name="minPrice" step="0.01" min="0" value="${minPrice}">
            </div>
            <div class="form-group">
                <label for="maxPrice">Prezzo Massimo (€):</label>
                <input type="number" id="maxPrice" name="maxPrice" step="0.01" min="0" value="${maxPrice}">
            </div>
            <div class="form-group">
                <label for="category">Categoria:</label>
                <select id="category" name="category">
                    <option value="">Tutte le categorie</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.id}" ${cat.id == selectedCategory ? 'selected' : ''}>
                            <c:out value="${cat.name}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label for="productType">Tipo Prodotto:</label>
                <select id="productType" name="productType">
                    <option value="">Tutti i tipi</option>
                    <c:forEach var="type" items="${productTypes}">
                        <option value="${type}" ${type == selectedProductType ? 'selected' : ''}>
                            <c:out value="${type}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-actions filter-actions">
                <button type="submit" class="btn btn-primary"><i class="fas fa-search"></i> Cerca</button>
                <a href="${pageContext.request.contextPath}/products/search" class="btn btn-outline">Reset Filtri</a>
            </div>
        </form>
    </div>

    <div class="search-results">
        <c:choose>
            <c:when test="${not empty products}">
                <h2 class="results-title">Risultati della ricerca (${products.size()})</h2>
                <div class="products-grid">
                    <c:forEach var="product" items="${products}">
                        <%-- Imposta gli attributi per il componente product_card --%>
                        <c:set var="product" value="${product}" scope="request"/>
                        <c:set var="productImageId" value="${productImages[product.productId].imageId}" scope="request"/>
                        <c:set var="onSale" value="${onSaleProductIds.contains(product.productId)}" scope="request"/>
                        <jsp:include page="/WEB-INF/components/product_card.jsp"/>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="no-results-message">
                    <p>Nessun prodotto trovato. Prova a modificare i filtri di ricerca.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
<script src="${pageContext.request.contextPath}/scripts/search.js" defer></script>
<script src="${pageContext.request.contextPath}/scripts/async-cart.js"></script>
</body>
</html>
