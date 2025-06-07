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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user-auth.css" type="text/css">
    <jsp:include page="../../../components/common_head.jsp"/>
</head>
<body>
<jsp:include page="../../../components/header.jsp"/>

<main class="container mt-3 mb-3">
    <div class="orders-page-container">
        <h2>Dettagli Ordine #<c:out value="${order.orderId}"/></h2>
        <p>Data: <c:out value="${my:formatDateTime(order.orderDate)}"/></p>
        <p>Stato: <c:out value="${order.orderStatus}"/></p>
        <p>Totale: € <fmt:formatNumber value="${order.totalAmount}" type="currency" currencyCode="EUR" minFractionDigits="2" maxFractionDigits="2"/></p>

        <br>

        <h4>Prodotti ordinati</h4>
        <div class="table-responsive">
            <table class="table table-striped table-hover">
                <thead>
                <tr>
                    <%--TODO definire meglio--%>
                    <th>Prodotto</th>
                    <th>Variante</th>
                    <th>Quantità</th>
                    <th>Prezzo unitario</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${orderItems}">
                    <tr>
                            <%--TODO--%>
                        <td></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="user-auth-links">
            <a href="<c:url value="${pageContext.request.contextPath}/common/orders"/>" class="btn btn-secondary">Torna agli ordini</a>
        </div>
    </div>
</main>

<jsp:include page="../../../components/footer.jsp"/>
</body>
</html>