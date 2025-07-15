<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/functions" prefix="my" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
    <title>Gestione Ordini | CardHaven</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/products.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/admin-orders.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/WEB-INF/components/header.jsp"/>

<main class="admin-container">
    <div class="admin-page-header">
        <div class="admin-header-content">
            <h1><i class="fas fa-truck"></i> Gestione Ordini</h1>
            <p>Visualizza e gestisci tutti gli ordini ricevuti.</p>
        </div>
        <div class="admin-header-actions">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline">
                <i class="fas fa-arrow-left"></i> Torna alla Dashboard
            </a>
        </div>
    </div>

    <jsp:include page="/WEB-INF/components/error_message.jsp"/>
    <div class="content-card filter-card">
        <h2>Filtra Ordini</h2>
        <form action="${pageContext.request.contextPath}/admin/orders" method="get" class="filter-form">
            <div class="dateFilterForm">
                <div class="form-group">
                    <label for="startDate">Da Data:</label>
                    <input type="date" id="startDate" name="startDate" value="${startDate}">
                </div>
                <div class="form-group">
                    <label for="endDate">A Data:</label>
                    <input type="date" id="endDate" name="endDate" value="${endDate}">
                </div>
            </div>
            <div class="form-group">
                <label for="orderUserEmail">Email Cliente:</label>
                <input type="email" id="orderUserEmail" name="orderUserEmail" placeholder="Es. email@example.com"
                       value="${orderUserEmail}">
            </div>
            <div class="form-actions filter-actions">
                <button type="submit" class="btn btn-primary"><i class="fas fa-filter"></i> Applica Filtri</button>
                <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-outline">Reset Filtri</a>
            </div>
        </form>
    </div>

    <div class="content-card">
        <div class="table-container">
            <table class="orders-table">
                <thead>
                <tr>
                    <th>ID Ordine</th>
                    <th>Cliente</th>
                    <th>Data Ordine</th>
                    <th>Importo Totale</th>
                    <th>Indirizzo Spedizione</th>
                    <th>Stato</th>
                    <th>Azioni</th>
                </tr>
                </thead>
                <tbody>
                <%--@elvariable id="orders" type="java.util.Collection<com.cardhaven.cardhaven.model.dto.OrderDTO>"--%>
                <%--@elvariable id="userMap" type="java.util.Map<java.lang.Integer, com.cardhaven.cardhaven.model.dto.UserDTO>"--%>
                <%--@elvariable id="shippingAddressMap" type="java.util.Map<java.lang.Integer, com.cardhaven.cardhaven.model.dto.OrderAddressDTO>"--%>
                <%--@elvariable id="billingAddressMap" type="java.util.Map<java.lang.Integer, com.cardhaven.cardhaven.model.dto.OrderAddressDTO>"--%>
                <%--@elvariable id="orderStatuses" type="com.cardhaven.cardhaven.model.dto.OrderDTO.OrderStatus[]"--%>
                <c:forEach var="order" items="${orders}">
                    <tr>
                        <td data-label="ID Ordine">#<c:out value="${order.orderID}"/></td>
                        <td data-label="Cliente">
                            <c:set var="customer" value="${userMap[order.userID]}"/>
                            <c:choose>
                                <c:when test="${not empty customer}">
                                    <c:out value="${customer.firstName}"/> <c:out value="${customer.lastName}"/><br>
                                    <span class="customer-email"><c:out value="${customer.email}"/></span>
                                </c:when>
                                <c:otherwise>
                                    N/A
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td data-label="Data Ordine"><c:out value="${my:formatDateTime(order.orderDate)}"/></td>
                        <td data-label="Importo Totale">
                            <fmt:setLocale value="it_IT"/>
                            <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="€"/>
                        </td>
                        <td data-label="Indirizzo Spedizione" class="address-cell">
                            <c:set var="shippingAddress" value="${shippingAddressMap[order.shippingAddressId]}"/>
                            <c:choose>
                                <c:when test="${not empty shippingAddress}">
                                    <c:out value="${shippingAddress.streetAddress}"/><br>
                                    <c:out value="${shippingAddress.city}"/>, <c:out
                                        value="${shippingAddress.postalCode}"/><br>
                                    <c:out value="${shippingAddress.country}"/>
                                </c:when>
                                <c:otherwise>
                                    Indirizzo non disponibile
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td data-label="Stato" class="order-status-cell">
                            <form class="update-status-form" action="${pageContext.request.contextPath}/admin/orders"
                                  method="post">
                                <input type="hidden" name="action" value="updateStatus">
                                <input type="hidden" name="orderId" value="${order.orderID}">
                                <input type="hidden" name="startDate" value="${startDate}">
                                <input type="hidden" name="endDate" value="${endDate}">
                                <input type="hidden" name="orderUserEmail" value="${orderUserEmail}">
                                <select name="newStatus" class="status-select">
                                    <c:forEach var="status" items="${orderStatuses}">
                                        <option value="${status}" ${order.orderStatus == status ? 'selected' : ''}>
                                            <c:out value="${status}"/>
                                        </option>
                                    </c:forEach>
                                </select>
                            </form>
                        </td>
                        <td data-label="Azioni">
                            <div class="actions-cell">

                                <a href="${pageContext.request.contextPath}/admin/orders/${order.orderID}"
                                   class="action-btn btn-view" title="Visualizza Dettagli">
                                    <i class="fas fa-eye"></i>
                                </a>
                                <form action="${pageContext.request.contextPath}/admin/orders" method="post"
                                      onsubmit="return confirm('Sei sicuro di voler eliminare questo ordine? L\'azione è irreversibile.');"
                                      style="display: inline;">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="orderId" value="${order.orderID}">
                                    <button type="submit" class="action-btn btn-delete" title="Elimina Ordine">
                                        <i class="fas fa-trash-alt"></i>
                                    </button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty orders}">
                    <tr>
                        <td colspan="7" class="text-center" style="padding: 2rem;">Nessun ordine trovato.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
<script src="${pageContext.request.contextPath}/scripts/admin/admin-orders.js" defer></script>
</body>
</html>


