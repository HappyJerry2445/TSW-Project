<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
    <title>Il tuo carrello | CardHaven</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/cart.css">
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main>
    <div class="container mt-3">
        <div class="cart-page">
            <div class="cart-header">
                <h1 class="section-title"><i class="fas fa-shopping-cart"></i> Il tuo carrello</h1>
            </div>

            <c:choose>
                <%--@elvariable id="cartItems" type="java.util.Collection<com.cardhaven.cardhaven.model.dto.CartItemDetailDTO>"--%>
                <c:when test="${empty cartItems}">
                    <div class="empty-cart">
                        <div class="empty-cart-icon">
                            <i class="fas fa-shopping-basket"></i>
                        </div>
                        <h2>Il tuo carrello è vuoto</h2>
                        <p>Sembra che non hai ancora aggiunto nessun prodotto al carrello.</p>
                        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Inizia lo shopping</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <%-- Calculate Grand Total --%>
                    <c:set var="grandTotal" value="0"/>
                    <c:forEach var="item" items="${cartItems}">
                        <c:set var="grandTotal" value="${grandTotal + (item.price * item.quantity)}"/>
                    </c:forEach>

                    <div class="cart-content">
                        <div class="cart-items">
                            <div class="cart-table-wrapper">
                                <table class="cart-table">
                                    <thead>
                                    <tr>
                                        <th>Prodotto</th>
                                        <th>Prezzo</th>
                                        <th>Quantità</th>
                                        <th>Totale</th>
                                        <th>Azioni</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="item" items="${cartItems}">
                                        <tr>
                                            <td data-label="Prodotto">
                                                <div class="product-info">
                                                    <div class="product-image">
                                                        <img src="${pageContext.request.contextPath}/image/${item.imageId}"
                                                             alt="${item.productName}"
                                                             onerror="this.src='${pageContext.request.contextPath}/images/noimage.png'">
                                                    </div>
                                                    <div class="product-details">
                                                        <span class="product-name">${item.productName}</span>
                                                        <span class="product-id">ID: ${item.productId}</span>
                                                    </div>
                                                </div>
                                            </td>
                                            <td data-label="Prezzo" class="price-cell">
                                                <fmt:formatNumber value="${item.price}" type="currency"
                                                                  currencyCode="EUR"/>
                                            </td>
                                            <td data-label="Quantità" class="quantity-cell">
                                                <form class="quantity-control"
                                                      action="${pageContext.request.contextPath}/cart/update"
                                                      method="post">
                                                    <input type="hidden" name="cartItemId" value="${item.cartItemId}">
                                                    <button type="button" class="quantity-btn decrease"
                                                            onclick="decreaseQuantity(this); updateCartCount()">
                                                        <i class="fas fa-minus"></i>
                                                    </button>
                                                    <input type="number" name="quantity" value="${item.quantity}"
                                                           min="1" class="quantity-input form-input" required>
                                                    <button type="button" class="quantity-btn increase"
                                                            onclick="increaseQuantity(this)">
                                                        <i class="fas fa-plus"></i>
                                                    </button>
                                                    <button type="submit" class="btn btn-sm">
                                                        <i class="fas fa-sync-alt"></i> Aggiorna
                                                    </button>
                                                </form>
                                            </td>
                                            <td data-label="Totale" class="total-cell">
                                                <fmt:formatNumber value="${item.price * item.quantity}" type="currency"
                                                                  currencyCode="EUR"/>
                                            </td>
                                            <td data-label="Azioni" class="actions-cell">
                                                <form action="${pageContext.request.contextPath}/cart/delete"
                                                      method="post">
                                                    <input type="hidden" name="cartItemId" value="${item.cartItemId}">
                                                    <button type="submit" class="btn btn-danger btn-sm">
                                                        <i class="fas fa-trash-alt"></i> Rimuovi
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <div class="cart-summary">
                            <div class="cart-summary-header">
                                <h3><i class="fas fa-clipboard-list"></i> Riepilogo Ordine</h3>
                            </div>
                            <div class="cart-summary-content">
                                <!--
                                <div class="summary-row">
                                    <span>Subtotale</span>
                                    <span><fmt:formatNumber value="${grandTotal}" type="currency"
                                                            currencyCode="EUR"/></span>
                                </div>
                                <div class="summary-row">
                                    <span>Spedizione</span>
                                    <span>Calcolata al checkout</span>
                                </div>
                                <div class="summary-row">
                                    <span>Tasse</span>
                                    <span>Calcolate al checkout</span>
                                </div>
                                -->
                                <div class="summary-total">
                                    <span>Totale Complessivo</span>
                                    <span><fmt:formatNumber value="${grandTotal}" type="currency"
                                                            currencyCode="EUR"/></span>
                                </div>
                                <div class="cart-actions">
                                    <a href="${pageContext.request.contextPath}/common/checkout/shipping"
                                       class="btn btn-primary btn-block">
                                        <i class="fas fa-credit-card"></i> Procedi al checkout
                                    </a>
                                    <a href="${pageContext.request.contextPath}/" class="btn btn-outline btn-block">
                                        <i class="fas fa-arrow-left"></i> Continua lo shopping
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>

<script>
    function increaseQuantity(button) {
        const input = button.parentElement.querySelector('.quantity-input');
        input.value = parseInt(input.value) + 1;
    }

    function decreaseQuantity(button) {
        const input = button.parentElement.querySelector('.quantity-input');
        const value = parseInt(input.value);
        if (value > 1) {
            input.value = value - 1;
        }
    }


    // Animate the cart items when page loads
    document.addEventListener('DOMContentLoaded', function () {
        const items = document.querySelectorAll('.cart-table tbody tr');
        items.forEach((item, index) => {
            item.style.animationDelay = `${index * 0.1}s`;
        });
    });
</script>
</body>
</html>