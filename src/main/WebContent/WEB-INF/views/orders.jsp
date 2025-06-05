<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> <%-- For date formatting --%>

<c:set var="pageTitle" value="I Miei Ordini" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/orders.css" type="text/css">
    <jsp:include page="../../components/common_head.jsp"/>
</head>
<body>
<jsp:include page="../../components/header.jsp"/>

<main class="container mt-3 mb-3">
    <div class="orders-page-container">
        <h1 class="section-title">I Tuoi Ordini</h1>

        <c:if test="${not empty errors}">
            <div class="alert alert-danger" role="alert">
                <c:forEach var="error" items="${errors}">
                    <p class="mb-0">${error}</p>
                </c:forEach>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty orders and empty errors}">
                <div class="alert alert-info" role="alert">
                    <p class="mb-0">🛒 Nessun ordine trovato. <a href="#" class="alert-link">Inizia a fare acquisti!</a></p>
                </div>
            </c:when>
            <c:when test="${not empty orders}">
                <div class="table-responsive">
                    <table class="table table-striped table-hover"><thead>
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
                                <td>
                                        <%-- Assuming order.orderDate is a java.util.Date or similar; format it --%>
                                    <fmt:formatDate value="${order.orderDate}" type="BOTH" dateStyle="medium" timeStyle="short"/>
                                </td>
                                <td><c:out value="${order.orderStatus}"/></td>
                                <td>€ <fmt:formatNumber value="${order.totalAmount}" type="currency" currencyCode="EUR" minFractionDigits="2" maxFractionDigits="2"/></td>
                                <td>
                                    <a href="<c:url value='/common/orders/${order.orderId}'/>" class="btn btn-sm btn-info">Dettagli</a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
        </c:choose>
    </div>
</main>

<jsp:include page="../../components/footer.jsp" />
</body>
</html>