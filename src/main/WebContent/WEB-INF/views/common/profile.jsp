<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.Locale" %>
<%@ page import="com.cardhaven.cardhaven.model.dto.UserDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Profilo Utente" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user-auth.css" type="text/css">
    <%-- Potresti voler includere un CSS specifico per il profilo se necessario --%>
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
                <div class="profile-details">
                    <p><strong>Nome:</strong> ${loggedInUser.firstName}</p>
                    <p><strong>Cognome:</strong> ${loggedInUser.lastName}</p>
                    <p><strong>Email:</strong> ${loggedInUser.email}</p>
                    <p><strong>Ruolo:</strong> ${loggedInUser.role}</p>
                    <p><strong>Data di Registrazione:</strong>
                        <%
                            UserDTO user = (UserDTO) session.getAttribute("loggedInUser");
                            Locale userLocale = Locale.ITALY;
                            DateTimeFormatter
                                    formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM, yyyy 'alle' hh:mm", userLocale);
                            String formattedDate = user.getCreatedAt().format(formatter);
                            formattedDate = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);
                        %>
                        <%=formattedDate%>
                    </p>
                </div>

                <div class="profile-actions mt-2">
                    <a href="${pageContext.request.contextPath}/common/edit-profile" class="btn btn-primary">Modifica
                        Profilo</a>
                    <a href="${pageContext.request.contextPath}/common/change-password" class="btn btn-outline">Cambia
                        Password</a>
                        <%-- Aggiungi altri link per la gestione dell'account, es. indirizzi, ordini --%>
                    <a href="${pageContext.request.contextPath}/common/addresses" class="btn btn-accent">I Miei
                        Indirizzi</a>
                    <a href="${pageContext.request.contextPath}/common/orders" class="btn btn-accent">I Miei Ordini</a>
                </div>
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
</body>
</html>