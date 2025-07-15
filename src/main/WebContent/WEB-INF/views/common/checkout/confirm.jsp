<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="pageTitle" value="Ordine Confermato" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/profile.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/order.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/confirm.css" type="text/css">
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
</head>
<body>

<jsp:include page="/WEB-INF/components/header.jsp"/>

<main>
    <div class="container mt-3 mb-3">
        <div class="card profile-card p-3">
            <div class="success-container">
            <!-- Success Icon and Title -->
            <div class="success-icon">
                <i class="fas fa-check-circle"></i>
            </div>
            <h1 class="success-title">Ordine #${lastOrder.orderID} Confermato!</h1>
            <p class="lead">Grazie per il tuo acquisto,
                <c:if test="${not empty loggedInUser}">
                    ${loggedInUser.firstName} ${loggedInUser.lastName}
                </c:if>!</p>
            </div>

            <div class="profile-actions mt-3">
                <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">Continua lo Shopping</a>

                <a href="${pageContext.request.contextPath}/common/orders" class="btn btn-primary"> Visualizza I Miei Ordini</a>
            </div>

        </div>
    </div>

</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>


</body>
</html>