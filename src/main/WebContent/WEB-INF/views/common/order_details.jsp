<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/functions" prefix="my" %>

<c:set var="pageTitle" value="Dettagli Ordine" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/order.css" type="text/css">
    <jsp:include page="/components/common_head.jsp"/>
</head>
<body>
<jsp:include page="/components/header.jsp"/>

<main class="container mt-3 mb-3">
    <div class="orders-page-container">
        <h2>Dettagli Ordine #<c:out value="${order.orderId}"/></h2>

        <c:if test="${not empty errors}">
        <div class="alert alert-danger">
            <ul>
                <c:forEach var="error" items="${errors}">
                    <li>${error}</li>
                </c:forEach>
            </ul>
        </div>
        </c:if>

        <div class="order-info-section">
            <div class="row">
                <div>
                    <h4>Informazioni Ordine</h4>
                    <p><strong>Data:</strong> <c:out value="${my:formatDateTime(order.orderDate)}"/></p>
                    <p><strong>Stato:</strong> <c:out value="${order.orderStatus}"/></p>
                    <p><strong>Totale:</strong> € <fmt:formatNumber value="${order.totalAmount}" type="number"
                                                                    minFractionDigits="2" maxFractionDigits="2"/></p>
                    <p><strong>Indirizzo di Spedizione:</strong>
                        <c:choose>
                        <c:when test="${not empty shippingAddress}">
                    <p><c:out value="${shippingAddress.postalCode}"/> <c:out value="${shippingAddress.city}"/>, <c:out
                            value="${shippingAddress.country}"/></p>
                    </c:when>
                    <c:otherwise>
                        <p>Indirizzo non disponibile</p>
                    </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <br>

        <div class="orders-page-container">
            <h4>Prodotti Ordinati</h4>
            <div>
                <c:choose>
                    <c:when test="${not empty orderItems}">
                        <div class="order-items">
                            <c:forEach var="item" items="${orderItems}">
                                <c:set var="product" value="${productMap[item.productID]}"/>
                                <div class="order-item-card">
                                    <div class="row align-items-center">
                                            <%--TODO mettere immagine--%>
                                        <div>
                                            <h5 class="product-name">
                                                <c:choose>
                                                    <c:when test="${not empty product}">
                                                        <c:out value="${product.productName}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        Prodotto non disponibile (ID: ${item.productID})
                                                    </c:otherwise>
                                                </c:choose>
                                            </h5>
                                        </div>

                                        <div class="text-center">
                                            <p>
                                                <strong>Quantità:</strong><br>
                                                <span class="quantity-value"><c:out value="${item.quantity}"/></span>
                                            </p>
                                        </div>

                                        <div class="text-right">
                                            <p>
                                                <strong>Prezzo unitario:</strong><br>
                                                <span class="unit-price">
                                                € <fmt:formatNumber value="${item.unitPrice}" type="number"
                                                                    minFractionDigits="2" maxFractionDigits="2"/>
                                            </span>
                                            </p>
                                            <p class="total-price">
                                                <strong>Subtotale:</strong><br>
                                                <span class="item-total">
                                                € <fmt:formatNumber value="${item.unitPrice * item.quantity}"
                                                                    type="number" minFractionDigits="2"
                                                                    maxFractionDigits="2"/>
                                            </span>
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert-danger">
                            <p>Nessun prodotto trovato per questo ordine.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="order-summary">
            <div class="row">
                <div class="summary-box">
                    <h5>Riepilogo Ordine</h5>
                    <div class="summary-row">
                        <span>Subtotale:</span>
                        <span>€ <fmt:formatNumber value="${order.totalAmount}" type="number" minFractionDigits="2"
                                                  maxFractionDigits="2"/></span>
                    </div>
                    <div class="summary-row total-row">
                        <strong>
                            <span>Totale:</span>
                            <span>€ <fmt:formatNumber value="${order.totalAmount}" type="number" minFractionDigits="2"
                                                      maxFractionDigits="2"/></span>
                        </strong>
                    </div>
                </div>
            </div>

            <div class="text-center links mt-3">
                <a href="${pageContext.request.contextPath}/common/orders" class="btn btn-primary">Torna agli ordini</a>
            </div>
        </div>
</main>

<jsp:include page="/components/footer.jsp"/>
</body>
</html>