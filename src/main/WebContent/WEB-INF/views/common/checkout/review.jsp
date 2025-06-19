<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageTitle" value="Riepilogo Ordine" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/profile.css" type="text/css">
    <jsp:include page="/components/common_head.jsp"/>
</head>
<body>
<jsp:include page="/components/header.jsp"/>

<main>
    <div class="container mt-3 mb-3">
        <div class="card profile-card p-3">
            <h2 class="section-title">Riepilogo del Tuo Ordine</h2>
            <jsp:include page="/components/error_message.jsp"/>

            <%-- TODO --%>

        </div>
    </div>
</main>

<jsp:include page="/components/footer.jsp"/>
</body>
</html>