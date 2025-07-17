<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="my" uri="/WEB-INF/functions" %>

<c:set var="pageTitle" value="Profilo Utente" scope="request"/>

<!-- TODO: Improve edit function (No real update if no change and ajax) -->
<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/profile.css" type="text/css">
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main>
    <div class="container mt-3">
        <div class="card profile-card">
            <h2 class="section-title">Il Mio Profilo</h2>

            <%--@elvariable id="loggedInUser" type="com.cardhaven.cardhaven.model.dto.UserDTO"--%>
            <c:if test="${not empty loggedInUser}">
                <form action="${pageContext.request.contextPath}/common/profile" method="post">
                    <div class="profile-details">
                        <div class="mb-1">
                            <label for="firstName">Nome:</label>
                            <input id="firstName" type="text" name="firstName" class="form-input"
                                   placeholder="Inserisci il tuo nome"
                                <%--@elvariable id="submittedFirstName" type="java.lang.String"--%>
                                   value="<c:out value="${not empty submittedFirstName ? submittedFirstName : loggedInUser.firstName}"/>"
                                   onblur="validateFormElement(this)"
                                   onsubmit="validateFormElement(this)"
                                   pattern="[A-Za-zÀ-ÖØ-öø-ÿ'\- ]{2,50}"
                                   disabled required>
                            <div class="errorFormElem"></div>

                        </div>
                        <div class="mb-1">
                            <label for="lastName">Cognome:</label>
                            <input id="lastName" type="text" name="lastName" class="form-input"
                                   placeholder="Inserisci il tuo cognome"
                                <%--@elvariable id="submittedLastName" type="java.lang.String"--%>
                                   value="<c:out value="${not empty submittedLastName ? submittedLastName : loggedInUser.lastName}"/>"
                                   onblur="validateFormElement(this)"
                                   onsubmit="validateFormElement(this)"
                                   pattern="[A-Za-zÀ-ÖØ-öø-ÿ'\- ]{2,50}"
                                   disabled required>
                            <div class="errorFormElem"></div>
                        </div>
                        <div class="mb-1">
                            <label for="email">Email:</label>
                            <input id="email" type="email" name="email" class="form-input"
                                   placeholder="Inserisci la tua email"
                                   onblur="validateFormElement(this)"
                                   onsubmit="validateFormElement(this)"
                                <%--@elvariable id="submittedEmail" type="java.lang.String"--%>
                                   value="<c:out value="${not empty submittedEmail ? submittedEmail : loggedInUser.email}"/>"
                                   disabled
                                   required>
                            <div class="errorFormElem"></div>
                        </div>
                        <p><strong>Ruolo:</strong> <c:out value="${loggedInUser.role}"/></p>
                        <p><strong>Data di Registrazione:</strong>
                            <c:set var="rawFormattedDate"
                                   value="${my:formatDateTimePattern(loggedInUser.createdAt, \"EEEE, d MMMM, yyyy \'alle\' HH:mm\")}"/>
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

<jsp:include page="/WEB-INF/components/footer.jsp"/>
<script defer src="${pageContext.request.contextPath}/scripts/profile.js"></script>
<script defer src="${pageContext.request.contextPath}/scripts/validation.js"></script>
</body>
</html>