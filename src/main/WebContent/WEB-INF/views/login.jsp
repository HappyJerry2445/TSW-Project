<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %>

<c:set var="pageTitle" value="Login" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user-auth.css" type="text/css">
    <jsp:include page="/components/common_head.jsp"/>
</head>
<body>
<jsp:include page="/components/header.jsp"/>

<main>
    <div class="center-child mt-3">

        <div class="card user-auth-card">
            <h2 class="section-title">Accedi al tuo account</h2>

            <jsp:include page="/components/error_message.jsp"/>

            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="mb-1">
                    <label for="email">Email</label>
                    <input id="email" type="email" name="email" class="form-input" placeholder="Inserisci la tua email">
                </div>

                <div class="mb-1">
                    <label for="password">Password</label>
                    <input id="password" type="password" name="password" class="form-input"
                           placeholder="Inserisci la tua password">
                </div>

                <button type="submit" class="btn btn-primary btn-block">Accedi</button>
            </form>

            <div class="user-auth-links">
                <a href="#">Password dimenticata?</a>
                <a href="${pageContext.request.contextPath}/register">Registrati</a>
            </div>
        </div>
    </div>

</main>

<jsp:include page="/components/footer.jsp"/>
</body>
</html>