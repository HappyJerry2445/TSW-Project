<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Prodotti: ${categoryName}" scope="request"/>


<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <%-- Inclusione dei file CSS necessari --%>
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/products.css" type="text/css">
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main>
    <div class="container">
        <%-- Titolo della pagina con il nome della categoria --%>
        <h1 class="section-title category-title">${categoryName}</h1>

        <%-- Debug info - rimuovere in produzione --%>
        <div style="display: none;">
            <p>Numero di prodotti: ${products.size()}</p>
            <c:forEach var="product" items="${products}">
                <p>ID: ${product.productId}, Nome: ${product.productName}</p>
            </c:forEach>
        </div>

        <%-- Visualizzazione degli eventuali errori --%>
        <c:if test="${not empty errors}">
            <div class="error-container">
                <c:forEach var="error" items="${errors}">
                    <p class="error-message">${error}</p>
                </c:forEach>
            </div>
        </c:if>

        <%-- Griglia dei prodotti --%>
        <div class="products-grid">
            <c:choose>
                <c:when test="${not empty products}">
                    <c:forEach var="product" items="${products}" varStatus="status">
                        <div class="product-card">
                                <%-- Tutta la card è un link ai dettagli --%>
                            <a href="${pageContext.request.contextPath}/products/detail/${product.productId}"
                               class="product-link">
                                    <%-- Immagine del prodotto --%>
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

                                    <%-- Informazioni del prodotto --%>
                                <div class="product-info">
                                    <h3 class="product-name">${product.productName} </h3>

                                        <%-- Disponibilità --%>
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

                                <%-- Prezzo e pulsante aggiungi al carrello in una riga --%>
                            <div class="product-price-row">
                                    <%-- Prezzo con formattazione --%>
                                <div class="product-price">
                                    <fmt:formatNumber value="${product.currentPrice}"
                                                      type="currency"
                                                      currencySymbol="€"
                                                      maxFractionDigits="2"/>
                                </div>

                                    <%-- Pulsante aggiungi al carrello --%>
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
                </c:when>
                <c:otherwise>
                    <div class="no-products-message">
                        <p>Nessun prodotto disponibile in questa categoria.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
</body>
</html>