<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %>

<c:set var="pageTitle" value="Login" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css" type="text/css">
    <jsp:include page="components/common_head.jsp"/>
</head>
<body>
<jsp:include page="./components/header.jsp"/>

<main>
    <div class="center-child mt-3">

        <div id="login-card" class="card">
            <h2 class="section-title">Accedi al tuo account</h2>

            <% List<String> errors = (List<String>) request.getAttribute("errors"); %>
            <% if (errors != null && !errors.isEmpty()) { %>
            <div class="alert alert-danger">
                <% for (String error : errors) { %>
                <p><%= error %>
                </p>
                <% } %>
            </div>
            <% } %>

            <form action="login" method="post">
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

            <div id="login-links">
                <a href="#">Password dimenticata?</a>
                <a href="#">Registrati</a>
            </div>
        </div>
    </div>

</main>

<jsp:include page="./components/footer.jsp"/>
</body>
</html>