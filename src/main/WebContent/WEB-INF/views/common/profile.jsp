<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.Locale" %>
<%@ page import="com.cardhaven.cardhaven.model.dto.UserDTO" %>
<%@ page import="com.cardhaven.cardhaven.util.DateTimeFormatterUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="my" uri="/WEB-INF/functions" %>

<c:set var="pageTitle" value="Profilo Utente" scope="request"/>

<!-- TODO: Improve edit function (No real update if no change and ajax) -->
<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user-auth.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/profile.css" type="text/css">
    <jsp:include page="/components/common_head.jsp"/>
</head>
<body>
<jsp:include page="/components/header.jsp"/>

<main>
    <div class="container mt-3">
        <div class="card profile-card">
            <h2 class="section-title">Il Mio Profilo</h2>

            <c:if test="${not empty loggedInUser}">
                <form action="${pageContext.request.contextPath}/common/profile" method="post">
                    <div class="profile-details">
                        <div class="mb-1">
                            <label for="firstName">Nome:</label>
                            <input id="firstName" type="text" name="firstName" class="form-input"
                                   placeholder="Inserisci il tuo nome"
                                   value="${not empty submittedFirstName ? submittedFirstName : loggedInUser.firstName}"
                                   disabled required>
                        </div>
                        <div class="mb-1">
                            <label for="lastName">Cognome:</label>
                            <input id="lastName" type="text" name="lastName" class="form-input"
                                   placeholder="Inserisci il tuo cognome"
                                   value="${not empty submittedLastName ? submittedLastName : loggedInUser.lastName}"
                                   disabled required>
                        </div>
                        <div class="mb-1">
                            <label for="email">Email:</label>
                            <input id="email" type="email" name="email" class="form-input"
                                   placeholder="Inserisci la tua email"
                                   value="${not empty submittedEmail ? submittedEmail : loggedInUser.email}" disabled
                                   required>
                        </div>
                        <p><strong>Ruolo:</strong> ${loggedInUser.role}</p>
                        <p><strong>Data di Registrazione:</strong>
                            <c:set var="rawFormattedDate"
                                   value="${my:formatDateTimePattern(sessionScope.loggedInUser.createdAt, \"EEEE, d MMMM, yyyy \'alle\' hh:mm\")}"/>
                            <c:set var="finalFormattedDate" value="${my:capitalize(rawFormattedDate)}"/>

                            <c:out value="${finalFormattedDate}"/>
                        </p>
                    </div>

                    <!-- TODO: Change profile actions accordingly -->
                    <div class="profile-actions mt-2">
                        <button type="button" id="edit-button" class="btn btn-primary" onclick="editProfile()">Modifica
                            Profilo
                        </button>
                        <button type="submit" id="save-button" class="btn btn-primary d-none">
                            Salva Profilo
                        </button>
                        <a href="${pageContext.request.contextPath}/common/change-password" class="btn btn-outline">Cambia
                            Password</a>
                            <%-- Aggiungi altri link per la gestione dell'account, es. indirizzi, ordini --%>
                        <a href="${pageContext.request.contextPath}/common/addresses" class="btn btn-accent">I Miei
                            Indirizzi</a>
                        <a href="${pageContext.request.contextPath}/common/orders" class="btn btn-accent">I Miei
                            Ordini</a>
                    </div>
                </form>
            </c:if>
            <c:if test="${empty loggedInUser}">
                <p class="text-center">Nessun utente loggato. Effettua l'accesso per visualizzare il tuo profilo.</p>
                <div class="text-center mt-2">
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Accedi Ora</a>
                </div>
            </c:if>
        </div>
    </div>
</main>

<jsp:include page="/components/footer.jsp"/>
<script src="/scripts/profile.js"></script>
</body>
</html>