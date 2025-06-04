<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
    <jsp:include page="../components/common_head.jsp"/>
</head>
<body>
<jsp:include page="../components/header.jsp"/>
<div class="orders-container">
    <h1>I Tuoi Ordini</h1>

    <c:choose>
        <c:when test="${empty orders}">
            <p>Nessun ordine trovato</p>
        </c:when>
        <c:otherwise>
            <table>
                <tr>
                    <th>ID Ordine</th>
                    <th>Data</th>
                    <th>Stato</th>
                    <th>Totale</th>
                </tr>
                <c:forEach items="${orders}" var="order">
                    <tr>
                        <td>${order.orderId}</td>
                        <td>${order.orderDate}</td>
                        <td>${order.orderStatus}</td>
                        <td>€${order.totalAmount}</td>
                    </tr>
                </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="../components/footer.jsp" />
</body>
</html>