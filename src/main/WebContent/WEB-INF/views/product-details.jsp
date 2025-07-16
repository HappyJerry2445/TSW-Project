<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="${product.productName}" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/productdetails.css" type="text/css">
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main>
    <div class="container">
        <%-- Visualizzazione degli eventuali errori --%>
        <c:if test="${not empty errors}">
            <div class="error-container">
                <c:forEach var="error" items="${errors}">
                    <p class="error-message">${error}</p>
                </c:forEach>
            </div>
        </c:if>

        <div class="product-detail-container">
            <%-- Immagine del prodotto --%>
            <div class="product-image-container">
                <c:choose>
                    <c:when test="${not empty productImages[product.productId]}">
                        <img src="${pageContext.request.contextPath}/image/${productImages[product.productId].imageId}"
                             alt="${product.productName}"
                             class="product-detail-image">
                    </c:when>
                    <c:otherwise>
                        <img src="${pageContext.request.contextPath}/images/noimage.png"
                             alt="Immagine non disponibile"
                             class="product-detail-image">
                    </c:otherwise>
                </c:choose>
            </div>

            <%-- Informazioni del prodotto --%>
            <div class="product-info-container">
                <h1 class="product-title">${product.productName}</h1>

                <%-- Prezzo --%>
                <div class="product-price">
                    <fmt:formatNumber value="${product.currentPrice}"
                                      type="currency"
                                      currencySymbol="€"
                                      maxFractionDigits="2"/>
                </div>

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

                <%-- Descrizione --%>
                <div class="product-description">
                    <h2>Descrizione</h2>
                    <p>${product.description}</p>
                </div>

                <%-- Pulsante aggiungi al carrello --%>
                <c:if test="${product.stockQuantity > 0}">
                    <form action="${pageContext.request.contextPath}/cart/add"
                          method="POST"
                          class="add-to-cart-form">
                        <input type="hidden" name="productId" value="${product.productId}">

                        <div class="quantity-selector">
                            <label for="quantity">Quantità:</label>
                            <input type="number"
                                   id="quantity"
                                   name="quantity"
                                   value="1"
                                   min="1"
                                   max="${product.stockQuantity}">
                        </div>

                        <button type="submit" class="btn btn-primary add-to-cart-btn">
                            Aggiungi al carrello
                        </button>
                    </form>
                </c:if>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
</body>
</html>