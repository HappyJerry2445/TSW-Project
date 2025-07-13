<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %>

<c:set var="pageTitle" value="Ricerca Prodotti per Categoria" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/products.css" type="text/css">
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main class="products-container">
    <h1 class="category-title">${categoryName}</h1>

    <c:if test="${not empty errors}">
        <div class="error-container">
            <c:forEach var="error" items="${errors}">
                <p class="error-message">${error}</p>
            </c:forEach>
        </div>
    </c:if>
    <div class="products-grid">
    <c:choose>
        <c:when test="${not empty products}">
            <c:forEach var="product" items="${products}">
                <div class="product-card">
                <%-- Immagine del prodotto --%>
                <div class="product-image-container">
                <c:choose>
                    <c:when test="${not empty productImages[product.productId]}">
                        <img src="${pageContext.request.contextPath}/images/product?id=${productImages[product.productId].imageId}"
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
                <h3 class="product-name">${product.productName}</h3>

                <div class="product-price">
                <fmt:formatNumber value="${product.currentPrice}"
                                  type="currency"
                                  currencySymbol="€"
                                  maxFractionDigits="2"/>
                </div>
                <div class="product-availability">
                <c:choose>
                    <c:when test="${product.stockQuantity > 0}">
                        <span class="in-stock">Disponibile</span>
                        <span class="stock-quantity">(${product.stockQuantity} in magazzino)</span>
                    </c:when>
                    <c:otherwise>
                        <span class="out-of-stock">Non disponibile</span>
                    </c:otherwise>
                </c:choose>
                </div>

                        <%-- Pulsanti azione --%>
                    <div class="product-actions">
                        <a href="${pageContext.request.contextPath}/products/detail/${product.productId}"
                           class="btn btn-primary">
                            Dettagli
                        </a>
                        <c:if test="${product.stockQuantity > 0}">
                            <form action="${pageContext.request.contextPath}/cart/add"
                                  method="POST"
                                  class="add-to-cart-form">
                                <input type="hidden" name="productId" value="${product.productId}">
                                <input type="hidden" name="quantity" value="1">
                                <button type="submit" class="btn btn-secondary">
                                    Aggiungi al Carrello
                                </button>
                            </form>
                        </c:if>
                    </div>
                </div>
                </div>
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
</body>
</html>
