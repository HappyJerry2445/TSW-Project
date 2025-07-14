<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="pageTitle" value="Riepilogo Ordine" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/profile.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/order.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/payment.css" type="text/css">
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
                            <c:set var="subtotal" value="${subtotal + (item.price * item.quantity)}"/>
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

            <!-- PAYMENT METHOD SECTION -->
            <div class="order-info-section">
                <h5>Metodo di Pagamento</h5>
                <div class="payment-methods">
                    <div class="payment-option">
                        <input type="radio" id="credit-card" name="payment-method" value="credit-card" checked>
                        <label for="credit-card">
                            <i class="fas fa-credit-card"></i> Carta di Credito/Debito
                        </label>
                    </div>
                    <div class="payment-option">
                        <input type="radio" id="bank-transfer" name="payment-method" value="bank-transfer">
                        <label for="bank-transfer">
                            <i class="fas fa-university"></i> Bonifico Bancario
                        </label>
                    </div>
                </div>

                <!-- PAYMENT DETAILS FORM -->
                <div id="payment-details" class="payment-details-form">
                    <!-- Credit Card Form -->
                    <div id="credit-card-form" class="payment-form active">
                        <h5>Dettagli Carta di Credito</h5>
                        <div class="form-group">
                            <label for="card-number">Numero Carta:</label>
                            <input type="text" id="card-number" class="form-control" placeholder="1234 5678 9012 3456" maxlength="19">
                        </div>
                        <div class="form-row">
                            <div class="form-group col-md-6">
                                <label for="expiry-date">Data Scadenza:</label>
                                <input type="text" id="expiry-date" class="form-control" placeholder="MM/AA" maxlength="5">
                            </div>
                            <div class="form-group col-md-6">
                                <label for="cvv">CVV:</label>
                                <input type="text" id="cvv" class="form-control" placeholder="123" maxlength="4">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="cardholder-name">Nome sul Titolare:</label>
                            <input type="text" id="cardholder-name" class="form-control" placeholder="Nome Cognome">
                        </div>
                    </div>

                    <!-- Bank Transfer Form -->
                    <div id="bank-transfer-form" class="payment-form">
                        <h5>Bonifico Bancario</h5>
                        <p class="payment-info">Riceverai le istruzioni per il bonifico via email dopo la conferma dell'ordine.</p>
                        <div class="alert alert-info">
                            <strong>Nota:</strong> L'ordine verrà processato solo dopo la ricezione del pagamento.
                        </div>
                    </div>
                </div>
            </div>

            <!-- ORDERED PRODUCTS SECTION -->
            <div>
                <div class="order-info-section">
                    <c:choose>
                        <c:when test="${not empty cartItems}">
                            <c:forEach var="item" items="${cartItems}">
                                    <div class="product-item">
                                        <div class="product-image-container">
                                                    <img src="${pageContext.request.contextPath}/image/${item.imageId}" alt="Immagine di ${item.productName}"
                                                         class="product-image-small"
                                                         onerror="this.src='${pageContext.request.contextPath}/images/noimage.png'">
                                        </div>
                                        <div>
                                            <h5 class="product-name"><c:out value="${item.productName}"/></h5>
                                            <p class="product-info">
                                                Quantità: <strong>${item.quantity}</strong><br>
                                                Prezzo unitario: <strong>€ <fmt:formatNumber
                                                    value="${item.price}" type="number" minFractionDigits="2"
                                                    maxFractionDigits="2"/></strong>
                                            </p>
                                        </div>
                                        <div class="product-total">
                                            <span class="item-total">€ <fmt:formatNumber
                                                    value="${item.price * item.quantity}" type="number"
                                                    minFractionDigits="2" maxFractionDigits="2"/></span>
                                        </div>
                                    </div>
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

            <form id="checkout-form" action="${pageContext.request.contextPath}/common/checkout/confirm" method="post">
                <input type="hidden" id="selected-payment-method" name="paymentMethod" value="credit-card">
                <input type="hidden" id="payment-data" name="paymentData" value="">

                <div class="profile-actions mt-3">
                    <a href="${pageContext.request.contextPath}/cart" class="btn btn-secondary">Torna al Carrello</a>
                    <button type="submit" class="btn btn-primary">Conferma Ordine</button>
                </div>
            </form>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
<script src="${pageContext.request.contextPath}/scripts/payment-handler.js"></script>
</body>
</html>