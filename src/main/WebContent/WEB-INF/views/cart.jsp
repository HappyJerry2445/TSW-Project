<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/components/common_head.jsp"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Il tuo carrello</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cart.css">
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
                                    <td data-label="Prezzo">€${item.price}</td>
                                    <td data-label="Quantità">
                                            <%-- Add class here --%>
                                        <form class="cell-content-wrapper"
                                              action="${pageContext.request.contextPath}/common/cart/update"
                                              method="post">
                                            <input type="hidden" name="cartId" value="${item.cartId}">
                                            <input type="number" name="quantity" value="${item.quantity}" min="1"
                                                   required>
                                            <button type="submit" class="btn btn-update btn-sm">Aggiorna</button>
                                        </form>
                                    </td>
                                    <td data-label="Totale">€${item.price * item.quantity}</td>
                                    <td data-label="Azioni">
                                            <%-- And add class here --%>
                                        <form class="cell-content-wrapper"
                                              action="${pageContext.request.contextPath}/common/cart/delete"
                                              method="post">
                                            <input type="hidden" name="cartId" value="${item.cartId}">
                                            <button type="submit" class="btn btn-danger btn-sm">Rimuovi</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <div class="cart-footer">
                        <a href="${pageContext.request.contextPath}/common/checkout" class="btn btn-primary">Procedi al
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