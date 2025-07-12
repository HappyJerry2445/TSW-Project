<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Set page title based on status code, or a generic title --%>
<%--@elvariable id="statusCode" type="java.lang.Integer"--%>
<c:set var="pageTitle">
    <c:choose>
        <c:when test="${statusCode == 404}">Pagina Non Trovata</c:when>
        <c:when test="${statusCode == 500}">Errore Interno del Server</c:when>
        <c:otherwise>Errore</c:otherwise>
    </c:choose>
</c:set>

<!DOCTYPE html>
<html lang="it">
<head>
    <title><c:out value="${pageTitle}"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css" type="text/css">
    <%-- <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/error.css" type="text/css"> --%>
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main class="container mt-3 mb-3">
    <div class="card text-center" style="max-width: 600px; margin: auto;">
        <h2 class="section-title">
            <c:choose>
                <c:when test="${statusCode == 404}">404 - Pagina Non Trovata</c:when>
                <c:when test="${statusCode == 500}">500 - Errore Interno</c:when>
                <c:otherwise>Si è verificato un errore</c:otherwise>
            </c:choose>
        </h2>

        <p class="mb-2">
            <c:choose>
                <%--@elvariable id="userMessage" type="java.lang.String"--%>
                <c:when test="${not empty userMessage}"><c:out value="${userMessage}"/></c:when>
                <c:otherwise>Siamo spiacenti, qualcosa è andato storto. Per favore, riprova più tardi o torna alla homepage.</c:otherwise>
            </c:choose>
        </p>

        <%--@elvariable id="requestUri" type="java.lang.String"--%>
        <c:if test="${not empty requestUri}">
            <p class="mb-2 text-secondary">
                Risorsa richiesta: <code><c:out value="${requestUri}"/></code>
            </p>
        </c:if>

        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Torna alla Home</a>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
</body>
</html>