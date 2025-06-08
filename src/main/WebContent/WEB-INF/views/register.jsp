<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %>

<c:set var="pageTitle" value="Registrazione" scope="request"/>

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
            <h2 class="section-title">Crea un nuovo account</h2>

            <jsp:include page="/components/error_message.jsp"/>

            <form action="${pageContext.request.contextPath}/register" method="post">
                <div class="mb-1">
                    <label for="firstName">Nome</label>
                    <input id="firstName" type="text" name="firstName" class="form-input"
                           placeholder="Inserisci il tuo nome"
                           value="<c:out value="${param.firstName != null ? param.firstName : ''}"/>">
                </div>

                <div class="mb-1">
                    <label for="lastName">Cognome</label>
                    <input id="lastName" type="text" name="lastName" class="form-input"
                           placeholder="Inserisci il tuo cognome"
                           value="<c:out value="${param.lastName != null ? param.lastName : ''}"/>">
                </div>

                <div class="mb-1">
                    <label for="email">Email</label>
                    <input id="email" type="email" name="email" class="form-input" placeholder="Inserisci la tua email"
                           value="<c:out value="${param.email != null ? param.email : ''}"/>">
                </div>

                <div class="mb-1">
                    <label for="password">Password</label>
                    <input id="password" type="password" name="password" class="form-input"
                           placeholder="Inserisci la tua password">
                </div>

                <div class="mb-1">
                    <label for="confirm_password">Conferma Password</label>
                    <input id="confirm_password" type="password" name="confirmPassword" class="form-input"
                           placeholder="Conferma la tua password">
                </div>

                <button type="submit" class="btn btn-primary btn-block">Registrati</button>
            </form>

            <div class="user-auth-links">
                <a href="${pageContext.request.contextPath}/login">Hai già un account? Accedi</a>
            </div>
        </div>
    </div>

</main>

<jsp:include page="/components/footer.jsp"/>
</body>
</html>