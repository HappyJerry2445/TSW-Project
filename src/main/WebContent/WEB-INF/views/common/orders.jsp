<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> <%-- For date formatting --%>
<%@ taglib uri="/WEB-INF/functions" prefix="my" %>


<c:set var="pageTitle" value="I Miei Ordini" scope="request"/>

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
        <h1 class="section-title">I Miei Ordini</h1>


        <jsp:include page="/components/error_message.jsp"/>

        <c:choose>
            <%--@elvariable id="orders" type="java.util.List<com.cardhaven.cardhaven.model.dto.OrderDTO>"--%>
            <c:when test="${empty orders and empty errors}">
                <div class="links" role="alert">
                    <p class="mb-1">🛒 Nessun ordine trovato. <a href="#">Inizia a fare acquisti!</a>
                    </p>
                </div>
            </c:when>
            <c:when test="${not empty orders}">
                <!-- Tabella per desktop -->
                <div>
                    <div class="table-responsive">
                        <table class="table table-striped table-hover">
                            <!-- Testata tabella -->
                            <thead>
                            <tr>
                                <th scope="col">ID Ordine</th>
                                <th scope="col">Data</th>
                                <th scope="col">Stato</th>
                                <th scope="col">Totale</th>
                                <th scope="col">Azioni</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${orders}" var="order">
                                <tr>
                                    <td>#<c:out value="${order.orderId}"/></td>
                                    <td><c:out value="${my:formatDateTime(order.orderDate)}"/></td>
                                    <td><c:out value="${order.orderStatus}"/></td>
                                    <td>€ <fmt:formatNumber value="${order.totalAmount}"
                                                            type="currency" currencyCode="EUR" minFractionDigits="2" maxFractionDigits="2"/></td>
                                    <td>
                                        <a href="<c:url value='/common/orders/${order.orderId}'/>"
                                           class="btn btn-info">Dettagli</a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Card per mobile -->
                <div>
                    <div class="table-cards" id="cards">
                        <c:forEach items="${orders}" var="order">
                            <div class="card-item">
                                <div class="card-header">Ordine #<c:out value="${order.orderId}"/></div>
                                <div class="card-row">
                                    <span class="card-label">Data:</span>
                                    <span class="card-value"><c:out value="${my:formatDateTime(order.orderDate)}"/></span>
                                </div>
                                <div class="card-row">
                                    <span class="card-label">Stato:</span>
                                    <span class="card-value"><c:out value="${order.orderStatus}"/></span>
                                </div>
                                <div class="card-row">
                                    <span class="card-label">Totale:</span>
                                    <span class="card-value">€ <fmt:formatNumber value="${order.totalAmount}"
                                                                                 type="currency" currencyCode="EUR" minFractionDigits="2" maxFractionDigits="2"/></span>
                                </div>
                                <div class="card-row">
                                    <span class="card-label">Azioni:</span>
                                    <span class="card-value">
                            <a href="<c:url value='/common/orders/${order.orderId}'/>"
                               class="btn btn-info">Dettagli</a>
                        </span>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:when>
        </c:choose>
    </div>

    <div class="text-center mt-3">
        <a href="${pageContext.request.contextPath}/common/profile" class="btn btn-primary">Torna al
            Profilo</a>
    </div>
</main>

<jsp:include page="/components/footer.jsp"/>
</body>
</html>