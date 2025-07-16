<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Ricerca Prodotti" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/products.css">
    <%-- For product grid layout --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/search.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main>
    <div class="container mt-3">
        <h1 class="section-title">Ricerca Prodotti</h1>

        <jsp:include page="/WEB-INF/components/error_message.jsp"/>

        <div class="content-card search-filter-card">
            <h2>Filtra la tua ricerca</h2>
            <form action="${pageContext.request.contextPath}/products/search" method="get" class="search-form">
                <div class="form-group">
                    <label for="query">Nome prodotto:</label>
                    <input type="text" id="query" name="query" placeholder="Nome prodotto"
                           value="${query}">
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
                        <%--@elvariable id="categories" type="java.util.Collection<com.cardhaven.cardhaven.model.dto.CategoryDTO>"--%>
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
                        <%--@elvariable id="productTypes" type="com.cardhaven.cardhaven.model.dto.ProductDTO.ProductType[]"--%>
                        <c:forEach var="type" items="${productTypes}">
                            <option value="${type}" ${type == selectedProductType ? 'selected' : ''}>
                                <c:out value="${type}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-actions filter-actions">
                    <button type="submit" class="btn btn-primary"><i class="fas fa-search"></i> Cerca</button>
                    <a href="${pageContext.request.contextPath}/products/search" class="btn btn-outline">Reset
                        Filtri</a>
                </div>
            </form>
        </div>

        <div class="search-results">
            <c:choose>
                <c:when test="${not empty products}">
                    <h2 class="results-title">Risultati della ricerca (${products.size()})</h2>
                    <div class="products-grid">
                        <c:forEach var="product" items="${products}">
                            <div class="product-card">
                                <a href="${pageContext.request.contextPath}/products/detail/${product.productId}"
                                   class="product-link">
                                    <div class="product-image-container">
                                        <c:choose>
                                            <c:when test="${not empty productImages[product.productId]}">
                                                <img src="${pageContext.request.contextPath}/image/${productImages[product.productId].imageId}"
                                                     alt="${product.productName}"
                                                     class="product-image"
                                                     loading="lazy">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/images/noimage.png"
                                                     alt="Immagine non disponibile"
                                                     class="product-image"
                                                     loading="lazy">
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="product-info">
                                        <h3 class="product-name">${product.productName} </h3>
                                        <div class="product-availability">
                                            <c:choose>
                                                <c:when test="${product.stockQuantity > 0}">
                                                    <span class="in-stock">Disponibile</span>
                                                    <span class="stock-quantity">(${product.stockQuantity} pezzi)</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="out-of-stock">Non disponibile</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </a>
                                <div class="product-price-row">
                                    <div class="product-price">
                                        <fmt:formatNumber value="${product.currentPrice}"
                                                          type="currency"
                                                          currencySymbol="€"
                                                          maxFractionDigits="2"/>
                                    </div>
                                    <c:if test="${product.stockQuantity > 0}">
                                        <form action="${pageContext.request.contextPath}/cart/add"
                                              method="POST"
                                              class="add-to-cart-form">
                                            <input type="hidden" name="productId" value="${product.productId}">
                                            <input type="hidden" name="quantity" value="1">
                                            <button type="submit" class="btn btn-secondary">
                                                Aggiungi
                                            </button>
                                        </form>
                                    </c:if>
                                </div>
                            </div>
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
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
<script src="${pageContext.request.contextPath}/scripts/search.js" defer></script>
</body>
</html>

