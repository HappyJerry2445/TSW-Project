<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%--
  Reusable Product Card Component.
  Expects the following attributes to be set in the request scope before inclusion:
  - 'product': A ProductDTO object.
  - 'productImageId': The ID of the image to display for this product.
  - 'onSale': (Optional) A boolean flag (true/false) to indicate if the product is on sale.
--%>

<div class="product-card">
    <%-- The entire top part of the card is a link to the product detail page --%>
    <a href="${pageContext.request.contextPath}/products/detail/${product.productId}" class="product-card-link">
        <div class="product-image-container">
            <%-- Display sale badge if the 'onSale' flag is true --%>
            <c:if test="${onSale}">
                <span class="sale-badge">SCONTO</span>
            </c:if>

            <%-- Display the product image or a placeholder --%>
            <c:choose>
                <c:when test="${not empty productImageId}">
                    <img src="${pageContext.request.contextPath}/image/${productImageId}"
                         alt="Immagine di ${product.productName}" class="product-image" loading="lazy">
                </c:when>
                <c:otherwise>
                    <img src="${pageContext.request.contextPath}/images/noimage.png" alt="Immagine non disponibile"
                         class="product-image" loading="lazy">
                </c:otherwise>
            </c:choose>
        </div>

        <div class="product-info">
            <h3 class="product-name">${product.productName}</h3>

            <%-- Price section: handles both regular and sale prices --%>
            <div class="price-container">
                <c:choose>
                    <c:when test="${onSale}">
                        <div class="price-on-sale">
                            <span class="original-price">
                                <fmt:formatNumber value="${product.basePrice}" type="currency" currencySymbol="€"/>
                            </span>
                            <span class="current-price">
                                 <fmt:formatNumber value="${product.currentPrice}" type="currency" currencySymbol="€"/>
                            </span>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="price">
                            <fmt:formatNumber value="${product.currentPrice}" type="currency" currencySymbol="€"/>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </a>

    <%-- Add to Cart form, separate from the main link --%>
    <div class="product-actions">
        <c:if test="${product.stockQuantity > 0}">
            <form action="${pageContext.request.contextPath}/cart/add" method="post" class="add-to-cart-form">
                <input type="hidden" name="productId" value="${product.productId}">
                <input type="hidden" name="quantity" value="1">
                <button type="submit" class="btn btn-primary btn-block">
                    <i class="fas fa-cart-plus"></i> Aggiungi
                </button>
            </form>
        </c:if>
        <c:if test="${product.stockQuantity <= 0}">
             <div class="out-of-stock-indicator">
                Esaurito
             </div>
        </c:if>
    </div>
</div>
