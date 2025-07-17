<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %>

<c:set var="pageTitle" value="Registrazione" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/user-auth.css" type="text/css">
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main>
    <div class="center-child mt-3">

        <div class="card user-auth-card">
            <h2 class="section-title">Crea un nuovo account</h2>

            <jsp:include page="/WEB-INF/components/error_message.jsp"/>

            <form action="${pageContext.request.contextPath}/register" method="post">
                <div class="mb-1">
                    <label for="firstName">Nome</label>
                    <input id="firstName" type="text" name="firstName" class="form-input"
                           onsubmit="validateFormElement(this)"
                           onblur="validateFormElement(this)"
                           placeholder="Inserisci il tuo nome" required
                           pattern="[A-Za-zÀ-ÖØ-öø-ÿ'\- ]{2,50}"
                           title="Il nome può contenere solo lettere, spazi, apostrofi e trattini."
                           value="<c:out value="${param.firstName != null ? param.firstName : ''}"/>">
                    <div class="errorFormElem"></div>
                </div>

                <div class="mb-1">
                    <label for="lastName">Cognome</label>
                    <input id="lastName" type="text" name="lastName" class="form-input"
                           onsubmit="validateFormElement(this)"
                           onblur="validateFormElement(this)"
                           placeholder="Inserisci il tuo cognome" required
                           pattern="[A-Za-zÀ-ÖØ-öø-ÿ'\- ]{2,50}"
                           title="Il cognome può contenere solo lettere, spazi, apostrofi e trattini."
                           value="<c:out value="${param.lastName != null ? param.lastName : ''}"/>">
                    <div class="errorFormElem"></div>
                </div>

                <div class="mb-1">
                    <label for="email">Email</label>
                    <input id="email" type="email" name="email" class="form-input" placeholder="Inserisci la tua email"
                           title="La email deve essere nel formato username@dominio.ext"
                           onsubmit="validateFormElement(this)"
                           onblur="validateFormElement(this)"
                           onchange="validateFormElement(this)"
                           required
                           value="<c:out value="${param.email != null ? param.email : ''}"/>">
                    <div class="errorFormElem"></div>
                </div>

                <div class="mb-1">
                    <label for="password">Password</label>
                    <input id="password" type="password" name="password" class="form-input"
                           onsubmit="validateFormElement(this)"
                           onblur="validateFormElement(this)"
                           placeholder="Inserisci la tua password" required minlength="8"
                           pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,}"
                           title="La password deve contenere almeno 8 caratteri, inclusi una maiuscola, una minuscola, un numero e un carattere speciale.">
                    <div class="errorFormElem"></div>
                </div>

                <div class="mb-1">
                    <label for="confirm_password">Conferma Password</label>
                    <input id="confirm_password" type="password" name="confirmPassword" class="form-input"
                           placeholder="Conferma la tua password" required>
                    <span id="password-match-message" class="errorFormElem" style="display: none;"></span>
                </div>

                <button type="submit" class="btn btn-primary btn-block">Registrati</button>
            </form>

            <div class="user-auth-links">
                <a href="${pageContext.request.contextPath}/login">Hai già un account? Accedi</a>
            </div>
        </div>
    </div>

</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
<script src="${pageContext.request.contextPath}/scripts/register-validation.js"></script>
<script src="${pageContext.request.contextPath}/scripts/validation.js"></script>
</body>
</html>
