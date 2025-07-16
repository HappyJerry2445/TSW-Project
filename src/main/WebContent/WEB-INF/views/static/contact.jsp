<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Contattaci | CardHaven" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/static-page.css">
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main class="container static-page-container">
    <h1 class="section-title">Contattaci</h1>

    <div class="static-page-content">
        <p class="text-center">
            Hai domande, dubbi o hai bisogno di assistenza per un ordine? Il nostro team è qui per aiutarti.
        </p>

        <div class="contact-methods">
            <div class="contact-card">
                <h3><i class="fas fa-envelope"></i> Via Email</h3>
                <p>Per richieste generali, informazioni sui prodotti o supporto per gli ordini, inviaci un'email. Ti risponderemo entro 24 ore lavorative.</p>
                <a href="mailto:supporto@cardhaven.it" class="btn btn-outline">supporto@cardhaven.it</a>
            </div>
            <div class="contact-card">
                <h3><i class="fas fa-phone"></i> Telefonicamente</h3>
                <p>Preferisci parlare con una persona? Chiamaci durante i nostri orari di ufficio, dal Lunedì al Venerdì, dalle 9:00 alle 18:00.</p>
                <p class="contact-detail">+39 012 3456789</p>
            </div>
            <div class="contact-card">
                <h3><i class="fas fa-map-marker-alt"></i> Sede Operativa</h3>
                <p>La nostra sede operativa (non aperta al pubblico):</p>
                <p class="contact-detail">
                    Via del Collezionista, 123<br>
                    00100, Roma (RM), Italia
                </p>
            </div>
        </div>

        <h2 class="mt-3">Domande Frequenti</h2>
        <p>
            Prima di contattarci, potresti trovare una risposta rapida nella nostra sezione dedicata alle Domande Frequenti (FAQ).
            <a href="${pageContext.request.contextPath}/pages/faq">Visita la pagina FAQ</a>.
        </p>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
</body>
</html>
