<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Chi Siamo | CardHaven" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <%-- I will create this stylesheet later --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/static-page.css">
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main class="container static-page-container">
    <h1 class="section-title">Chi Siamo</h1>

    <div class="static-page-content">
        <h2>La Nostra Passione, la Tua Collezione</h2>
        <p>
            Benvenuto su <strong>CardHaven</strong>, il punto di riferimento in Italia per tutti gli appassionati di giochi di carte collezionabili.
            Siamo nati da una passione profonda per il mondo dei TCG (Trading Card Games), un universo fatto di strategia, collezionismo e, soprattutto, community.
        </p>
        <p>
            Il nostro team è composto da giocatori e collezionisti proprio come te. Conosciamo l'emozione di aprire una bustina sperando di trovare quella carta rara, la soddisfazione di completare un set e la dedizione necessaria per costruire il mazzo perfetto. È questa conoscenza che ci guida ogni giorno nella selezione dei nostri prodotti.
        </p>

        <h2>La Nostra Missione</h2>
        <p>
            L'obiettivo di CardHaven è semplice: offrire un catalogo vasto, costantemente aggiornato e di alta qualità, che possa soddisfare sia il giocatore competitivo che il collezionista più esigente. Vogliamo essere il tuo "porto sicuro" (il tuo <em>Haven</em>) dove puoi trovare non solo le ultime uscite di giochi come Magic: The Gathering, Pokémon, Yu-Gi-Oh! e One Piece, ma anche tutti gli accessori indispensabili per proteggere e valorizzare la tua collezione.
        </p>
        <ul>
            <li><strong>Vasto Assortimento:</strong> Dalle carte singole ai box sigillati, passando per sleeves, deck box e playmat dei migliori marchi.</li>
            <li><strong>Qualità Garantita:</strong> Selezioniamo con cura ogni articolo per assicurarti prodotti autentici e in condizioni perfette.</li>
            <li><strong>Servizio Clienti Dedicato:</strong> Siamo qui per aiutarti. Se hai domande o hai bisogno di consigli, il nostro team di esperti è a tua disposizione.</li>
        </ul>

        <h2>Unisciti alla Community</h2>
        <p>
            CardHaven non è solo un negozio, ma un luogo di incontro per la community. Attraverso le recensioni dei prodotti e i nostri canali social, vogliamo creare uno spazio dove gli appassionati possano scambiarsi opinioni, consigli e vivere appieno la loro passione.
        </p>
        <p>
            Grazie per averci scelto. Buona navigazione e che tu possa trovare sempre la carta che cerchi!
        </p>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
</body>
</html>
