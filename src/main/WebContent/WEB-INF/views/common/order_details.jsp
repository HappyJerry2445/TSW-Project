<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/functions" prefix="my" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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

        <jsp:include page="/components/error_message.jsp"/>

        <div class="order-info-section">
            <div class="row">
                <div>
                    <h4>Informazioni Ordine</h4>
                    <p><strong>Data:</strong> <c:out value="${my:formatDateTime(order.orderDate)}"/></p>
                    <p><strong>Stato:</strong> <c:out value="${order.orderStatus}"/></p>
                    <p><strong>Totale:</strong> € <fmt:formatNumber value="${fn:escapeXml(order.totalAmount)}"
                                                                    type="number"
                                                                    minFractionDigits="2" maxFractionDigits="2"/></p>
                    <p><strong>Indirizzo di Spedizione:</strong>
                        <c:choose>
                            <%--@elvariable id="shippingAddress" type="java.util.List<com.cardhaven.cardhaven.model.dto.AddressDTO>"--%>
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
                    <%--@elvariable id="orderItems" type="java.util.List<com.cardhaven.cardhaven.model.dto.OrderItemDTO>"--%>
                    <c:when test="${not empty orderItems}">
                        <div class="order-items">
                            <c:forEach var="item" items="${orderItems}">
                                <%--@elvariable id="productMap" type="java.util.Map<java.lang.Integer, com.cardhaven.cardhaven.model.dto.ProductDTO>"--%>
                                <c:set var="product" value="${productMap[item.productID]}"/>
                                <div class="order-item-card">
                                    <div class="product-content">
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
                                            <%--TODO test with real images of the product and with more product--%>
                                            <div class="product-image-container">
                                                <div>
                                                    <c:set var="image" value="${productImageMap[product.productId]}"/>
                                                    <c:choose>
                                                        <c:when test="${not empty image}">
                                                            <img src="${image}" alt="Immagine di ${product.productName}" class="product-image"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <img src="${pageContext.request.contextPath}/imgs/noimage.png" alt="Immagine non disponibile" class="product-image"/>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                            <div>
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
                    <h4>Riepilogo Ordine</h4>
                    <c:forEach var="item" items="${orderItems}">
                        <div class="summary-row">
                            <span>Quantità:</span>
                            <span class="unit-price"><c:out value="${item.quantity}"/></span>
                        </div>

                        <div class="summary-row">
                            <span>Prezzo unitario:</span>
                            <span class="unit-price">€ <fmt:formatNumber value="${fn:escapeXml(item.unitPrice)}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
                        </div>

                        <div class="summary-row">
                            <span>Subtotale:</span>
                            <span class="unit-price">€ <fmt:formatNumber value="${fn:escapeXml(order.totalAmount)}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
                        </div>

                        <div class="summary-row total-row">
                            <span>Totale:</span>
                            <span class="item-total">€ <fmt:formatNumber value="${fn:escapeXml(order.totalAmount)}" type="number"
                                                                         minFractionDigits="2"
                                                                         maxFractionDigits="2"/></span>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
</main>

<div class="text-center mt-3">
    <a href="${pageContext.request.contextPath}/common/orders" class="btn btn-primary">Torna agli ordini</a>
</div>

<jsp:include page="/components/footer.jsp"/>
</body>
</html>