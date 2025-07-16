<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/functions" prefix="my" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.google.gson.JsonObject" %>
<%@ page import="java.util.Map" %>
<%@ page buffer="64kb" %>

<c:set var="pageTitle" value="Dettagli Ordine" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/order.css" type="text/css">
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main class="container mt-3 mb-3">
    <div class="orders-page-container">
        <h2>Dettagli Ordine #<%--@elvariable id="order" type="com.cardhaven.cardhaven.model.dto.OrderDTO"--%>
            <c:out value="${order.orderID}"/></h2>

        <jsp:include page="/WEB-INF/components/error_message.jsp"/>

        <div class="row">
            <div>
                <h4>Informazioni Ordine</h4>
                <p><strong>Nome utente:</strong> <c:out value="${orderUserFirstName} ${orderUserLastName}"/></p>
                <p><strong>E-mail:</strong> <c:out value="${orderUserEmail}"/></p>
                <p><strong>Data:</strong> <c:out value="${my:formatDateTime(order.orderDate)}"/></p>
                <p><strong>Stato:</strong> <c:out value="${order.orderStatus}"/></p>
                <p><strong>Totale:</strong> € <fmt:formatNumber value="${fn:escapeXml(order.totalAmount)}"
                                                                type="number"
                                                                minFractionDigits="2" maxFractionDigits="2"/></p>
                <p><strong>Indirizzo di Spedizione:</strong>
                    <c:choose>
                        <%--@elvariable id="shippingAddress" type="java.util.List<com.cardhaven.cardhaven.model.dto.AddressDTO>"--%>
                    <c:when test="${not empty shippingAddress}">
                        <c:out value="${shippingAddress.streetAddress}"/>, <c:out
                        value="${shippingAddress.postalCode}"/>, <c:out value="${shippingAddress.city}"/>,
                        <c:out
                                value="${shippingAddress.country}"/>
                    </c:when>
                    <c:otherwise>
                <p>Indirizzo non disponibile</p>
                </c:otherwise>
                </c:choose>
            </div>
        </div>

        <br>

        <%--@elvariable id="orderItems" type="java.util.List<com.cardhaven.cardhaven.model.dto.OrderItemDTO>"--%>
        <h4>Prodotti Ordinati</h4>
        <div>
            <c:choose>
                <c:when test="${not empty orderItems}">
                    <div class="order-items">
                        <c:forEach var="item" items="${orderItems}">
                            <%-- Determine product details from live product or snapshot --%>
                            <%
                                String productName = "Prodotto non più disponibile";
                                Integer imageId = null;

                                // Get the maps from request scope
                                Map<Integer, com.cardhaven.cardhaven.model.dto.ProductDTO> productMap = (Map<Integer, com.cardhaven.cardhaven.model.dto.ProductDTO>) request.getAttribute("productMap");
                                Map<Integer, Integer> productImagesMap = (Map<Integer, Integer>) request.getAttribute("productImages");

                                // Get the current item from the JSTL loop
                                com.cardhaven.cardhaven.model.dto.OrderItemDTO currentItem = (com.cardhaven.cardhaven.model.dto.OrderItemDTO) pageContext.getAttribute("item");

                                if (currentItem != null) {
                                    // Try to find the live product from the map
                                    com.cardhaven.cardhaven.model.dto.ProductDTO liveProduct = (productMap != null) ? productMap.get(currentItem.getProductID()) : null;

                                    if (liveProduct != null) {
                                        productName = liveProduct.getProductName();
                                        // Get the live image ID from the map
                                        if (productImagesMap != null) {
                                            imageId = productImagesMap.get(liveProduct.getProductId());
                                        }
                                    } else if (currentItem.getProductSnapshot() != null) {
                                        // Fallback to the JSON snapshot if the live product doesn't exist
                                        try {
                                            Gson gson = new Gson();
                                            JsonObject snapshot = gson.fromJson(currentItem.getProductSnapshot(), JsonObject.class);
                                            if (snapshot != null && snapshot.has("name")) {
                                                productName = snapshot.get("name").getAsString();
                                            }
                                            if (snapshot != null && snapshot.has("imageId")) {
                                                imageId = snapshot.get("imageId").getAsInt();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace(); // Log error
                                        }
                                    }
                                }
                            %>
                            <div class="order-item-card">
                                <div class="product-item">
                                    <div class="product-image-container">
                                        <c:choose>
                                            <c:when test="<%= imageId != null && imageId > 0 %>">
                                                <img src="${pageContext.request.contextPath}/image/<%= imageId %>"
                                                     alt="Immagine di <%= productName %>"
                                                     class="product-image-small"/>
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/images/noimage.png"
                                                     alt="Immagine non disponibile"
                                                     class="product-image-small"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div>
                                        <h5 class="product-name"><%= productName %>
                                        </h5>
                                        <p class="product-info">
                                            Quantità: <strong>${item.quantity}</strong><br>
                                            Prezzo unitario: <strong>€ <fmt:formatNumber
                                                value="${item.unitPrice}" type="number" minFractionDigits="2"
                                                maxFractionDigits="2"/></strong>
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

        <div class="order-summary">
            <div class="row">
                <div class="summary-box">
                    <h4>Riepilogo Ordine</h4>

                    <c:forEach var="item" items="${orderItems}">
                        <%
                            String summaryProductName = "Prodotto non più disponibile";
                            // Get maps and current item
                            Map<Integer, com.cardhaven.cardhaven.model.dto.ProductDTO> summaryProductMap = (Map<Integer, com.cardhaven.cardhaven.model.dto.ProductDTO>) request.getAttribute("productMap");
                            com.cardhaven.cardhaven.model.dto.OrderItemDTO summaryItem = (com.cardhaven.cardhaven.model.dto.OrderItemDTO) pageContext.getAttribute("item");

                            if (summaryItem != null) {
                                com.cardhaven.cardhaven.model.dto.ProductDTO summaryProduct = (summaryProductMap != null) ? summaryProductMap.get(summaryItem.getProductID()) : null;

                                if (summaryProduct != null) {
                                    summaryProductName = summaryProduct.getProductName();
                                } else if (summaryItem.getProductSnapshot() != null) {
                                    try {
                                        Gson gson = new Gson();
                                        JsonObject snapshot = gson.fromJson(summaryItem.getProductSnapshot(), JsonObject.class);
                                        if (snapshot != null && snapshot.has("name")) {
                                            summaryProductName = snapshot.get("name").getAsString();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        %>
                        <div class="summary-item">
                            <div class="summary-row">
                                <span>
                                    <%= summaryProductName %> :
                                </span>
                                <span class="unit-price">
                                    <fmt:formatNumber value="${fn:escapeXml(item.quantity * item.unitPrice)}"
                                                      type="currency" currencyCode="EUR"/>
                                </span>
                            </div>
                        </div>
                    </c:forEach>

                    <div class="summary-row total-row">
                        <span><strong>Totale Ordine:</strong></span>
                        <span class="item-total">
                            <strong><fmt:formatNumber value="${fn:escapeXml(order.totalAmount)}" type="currency"
                                                      currencyCode="EUR"/></strong>
                        </span>
                    </div>
                </div>
            </div>
        </div>
</main>

<div class="text-center mt-3">
    <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-primary">Torna agli ordini</a>
</div>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
</body>
</html>
