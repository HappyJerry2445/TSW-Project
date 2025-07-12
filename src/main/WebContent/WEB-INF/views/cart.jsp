<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> <%-- Add this for currency formatting --%>

<jsp:include page="/WEB-INF/components/common_head.jsp"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Il tuo carrello</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/cart.css">
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main>
    <div class="container mt-3">
        <div class="card">
            <h2 class="section-title">Il tuo carrello</h2>

            <c:choose>
                <%--@elvariable id="cartItems" type="java.util.Collection<com.cardhaven.cardhaven.model.dto.CartItemDetailDTO>"--%>
                <c:when test="${empty cartItems}">
                    <p class="text-center">Il tuo carrello è vuoto.</p>
                </c:when>
                <c:otherwise>
                    <%-- Calculate Grand Total --%>
                    <c:set var="grandTotal" value="0"/>
                    <c:forEach var="item" items="${cartItems}">
                        <c:set var="grandTotal" value="${grandTotal + (item.price * item.quantity)}"/>
                    </c:forEach>

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
                                    <td data-label="Prodotto">${item.productName}</td>
                                    <td data-label="Prezzo"><fmt:formatNumber value="${item.price}" type="currency"
                                                                              currencyCode="EUR"/></td>
                                    <td data-label="Quantità">
                                        <form class="cell-content-wrapper"
                                              action="${pageContext.request.contextPath}/cart/update"
                                              method="post">
                                            <input type="hidden" name="cartItemId" value="${item.cartItemId}">
                                            <input type="number" name="quantity" value="${item.quantity}" min="1"
                                                   required>
                                            <button type="submit" class="btn btn-update btn-sm">Aggiorna</button>
                                        </form>
                                    </td>
                                    <td data-label="Totale"><fmt:formatNumber value="${item.price * item.quantity}"
                                                                              type="currency" currencyCode="EUR"/></td>
                                    <td data-label="Azioni">
                                        <form class="cell-content-wrapper"
                                              action="${pageContext.request.contextPath}/cart/delete"
                                              method="post">
                                            <input type="hidden" name="cartItemId" value="${item.cartItemId}">
                                            <button type="submit" class="btn btn-danger btn-sm">Rimuovi</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <div class="cart-footer">
                        <div class="cart-total">
                            <strong>Totale Complessivo: </strong>
                            <fmt:formatNumber value="${grandTotal}" type="currency" currencyCode="EUR"/>
                        </div>
                        <a href="${pageContext.request.contextPath}/common/checkout/shipping" class="btn btn-primary">Procedi al
                            checkout</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
</body>
</html>