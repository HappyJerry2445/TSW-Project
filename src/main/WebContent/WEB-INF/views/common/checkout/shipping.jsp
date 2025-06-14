<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Checkout - Indirizzo di Spedizione" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/profile.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/addresses.css" type="text/css">
    <jsp:include page="/components/common_head.jsp"/>
</head>
<body>
<jsp:include page="/components/header.jsp"/>

<main>
    <div class="container mt-3 mb-3">
        <div class="card profile-card p-3">
            <h2 class="section-title">Seleziona gli indirizzi da utilizzare per il tuo ordine</h2>
            <jsp:include page="/components/error_message.jsp"/>

            <c:if test="${not empty addresses}">

                <form action="${pageContext.request.contextPath}/common/checkout/shipping" method="post">

                        <%-- SELEZIONE INDIRIZZO SPEDIZIONE --%>
                    <div class="mb-2">
                            <%-- La classe "label" è già stilizzata in style.css --%>
                        <label for="shipping-address">Indirizzo di Spedizione:</label>
                            <%-- Il tag <select> eredita gli stili base dei form da style.css --%>
                        <select id="shipping-address" name="shippingAddressId" required>
                            <c:forEach var="address" items="${addresses}">
                                <option value="${address.addressID}" <c:if test="${address.isDefault()}">selected</c:if>>
                                    <c:out value="${address.streetAddress}, ${address.city}, ${address.postalCode}"/>
                                    <c:if test="${address.isDefault()}"> (Predefinito)</c:if>
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                                <%-- CHECKBOX "SAME AS SHIPPING" --%>
                            <div class="mb-3">
                                <input type="checkbox" id="same-as-shipping" name="sameAsShipping" checked>
                                <label for="same-as-shipping">L'indirizzo di fatturazione è lo stesso di quello di spedizione.</label>
                            </div>

                                <%-- SELEZIONE INDIRIZZO FATTURAZIONE (controllato da JS) --%>
                            <div class="mb-3" id="billing-address-container">
                                <label for="billing-address">Indirizzo di Fatturazione:</label>
                                <select id="billing-address" name="billingAddressId" required>
                                    <c:forEach var="address" items="${addresses}">
                                        <option value="${address.addressID}" <c:if test="${address.isDefault()}">selected</c:if>>
                                            <c:out value="${address.streetAddress}, ${address.city}"/> (<c:out value="${address.addressType}"/>)<c:if test="${address.isDefault()}"> (Predefinito)</c:if>
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                        <%-- Azioni: pulsanti stilizzati per proseguire o aggiungere un nuovo indirizzo --%>
                    <div class="profile-actions">
                        <a href="${pageContext.request.contextPath}/common/addresses/add" class="btn btn-secondary">Aggiungi Nuovo Indirizzo</a>
                        <button type="submit" class="btn btn-primary">Vai al Riepilogo</button>
                    </div>
                </form>
            </c:if>

            <%-- Messaggio se non ci sono indirizzi --%>
            <c:if test="${empty addresses}">
                <div class="alert-danger text-center p-2">
                    <p>Non hai ancora aggiunto nessun indirizzo.</p>
                    <a href="${pageContext.request.contextPath}/common/addresses/add" class="btn btn-primary mt-1">Aggiungine uno per proseguire</a>
                </div>
            </c:if>
        </div>
    </div>
</main>

<jsp:include page="/components/footer.jsp"/>

<%--L'attributo 'defer' assicura che lo script venga eseguito dopo il parsing del documento. --%>
<script src="${pageContext.request.contextPath}/scripts/address-form-handler.js" defer></script>

</body>
</html>