<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="pageTitle" value="Riepilogo Ordine" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/profile.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/order.css" type="text/css">
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
</head>
<body>

<jsp:include page="/WEB-INF/components/header.jsp"/>

<main>
    <div class="container mt-3 mb-3">
        <div class="card profile-card p-3">
            <h2 class="section-title">Riepilogo del Tuo Ordine</h2>
            <jsp:include page="/WEB-INF/components/error_message.jsp"/>

            <!-- ORDER SUMMARY SECTION -->
            <div class="order-summary mb-4">
                <div class="summary-box">
                    <c:set var="subtotal" value="0"/>
                    <c:forEach var="item" items="${cartItems}">
                        <c:set var="product" value="${productMap[item.productId]}"/>
                        <c:if test="${not empty product}">
                            <c:set var="subtotal" value="${subtotal + (product.currentPrice * item.quantity)}"/>
                        </c:if>
                    </c:forEach>

                    <div class="summary-row">
                        <span><strong>Articoli:</strong></span>
                        <span class="subtotal">€ <fmt:formatNumber value="${subtotal}" type="number"
                                                                   minFractionDigits="2" maxFractionDigits="2"/></span>
                    </div>

                    <div class="summary-row total-row">
                        <span><strong>Totale:</strong></span>
                        <span class="item-total">€ <fmt:formatNumber value="${subtotal}" type="number"
                                                                     minFractionDigits="2"
                                                                     maxFractionDigits="2"/></span>
                    </div>
                </div>
            </div>

            <br>

            <!-- DELIVERY INFORMATION SECTION -->
            <div class="order-info-section">
                <div>
                    <p><strong>In consegna a <c:out value="${loggedInUser.firstName}"/> <c:out
                            value="${loggedInUser.lastName}"/></strong></p>
                    <c:choose>
                        <c:when test="${not empty shippingAddress}">
                            <p>
                                <c:out value="${shippingAddress.streetAddress}"/> ,
                                <c:out value="${shippingAddress.postalCode}"/> <c:out value="${shippingAddress.city}"/>
                                ,
                                <c:out value="${shippingAddress.country}"/>
                            </p>
                        </c:when>
                        <c:otherwise>
                            <p class="text-danger">Indirizzo di spedizione non disponibile</p>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Check if billing address is different from shipping -->
                <c:if test="${shippingAddress.addressID != billingAddress.addressID}">
                    <div class="billing-address mt-3">
                        <p><strong>Fatturazione a:</strong></p>
                        <c:choose>
                            <c:when test="${not empty billingAddress}">
                                <p>
                                    <c:out value="${billingAddress.streetAddress}"/>,
                                    <c:out value="${billingAddress.postalCode}"/> <c:out
                                        value="${billingAddress.city}"/>,
                                    <c:out value="${billingAddress.country}"/>
                                </p>
                            </c:when>
                            <c:otherwise>
                                <p class="text-danger">Indirizzo di fatturazione non disponibile</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:if>

                <a href="${pageContext.request.contextPath}/common/checkout/shipping">Modifica Indirizzi</a>
            </div>

            <!-- ORDERED PRODUCTS SECTION -->
            <div>
                <div class="order-info-section">
                    <c:choose>
                        <c:when test="${not empty cartItems}">
                            <c:forEach var="item" items="${cartItems}">
                                <c:set var="product" value="${productMap[item.productId]}"/>
                                <c:if test="${not empty product}">
                                    <div class="product-item">
                                        <div class="product-image-container">
                                            <c:set var="image" value="${productImageMap[product.productId]}"/>
                                            <c:choose>
                                                <c:when test="${not empty image}">
                                                    <img src="${image}" alt="Immagine di ${product.productName}"
                                                         class="product-image-small"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="${pageContext.request.contextPath}/imgs/noimage.png"
                                                         alt="Immagine non disponibile" class="product-image-small"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div>
                                            <h5 class="product-name"><c:out value="${product.productName}"/></h5>
                                            <p class="product-info">
                                                Quantità: <strong>${item.quantity}</strong><br>
                                                Prezzo unitario: <strong>€ <fmt:formatNumber
                                                    value="${product.currentPrice}" type="number" minFractionDigits="2"
                                                    maxFractionDigits="2"/></strong>
                                            </p>
                                        </div>
                                        <div class="product-total">
                                            <span class="item-total">€ <fmt:formatNumber
                                                    value="${product.currentPrice * item.quantity}" type="number"
                                                    minFractionDigits="2" maxFractionDigits="2"/></span>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-warning">
                                <p>Nessun prodotto trovato nel carrello.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <form action="${pageContext.request.contextPath}/common/checkout/confirm" method="post">
                <div class="profile-actions mt-3">
                    <a href="#" class="btn btn-secondary">Torna al Carrello</a>
                    <button type="submit" class="btn btn-primary">Conferma Ordine</button>
                </div>
            </form>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
</body>
</html>