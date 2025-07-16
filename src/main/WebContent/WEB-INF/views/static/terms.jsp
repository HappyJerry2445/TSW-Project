<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Termini e Condizioni | CardHaven" scope="request"/>

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
    <h1 class="section-title">Termini e Condizioni di Servizio</h1>
    <p class="text-center">Ultimo aggiornamento: 24 Maggio 2024</p>

    <div class="static-page-content">
        <h2>1. Introduzione</h2>
        <p>
            Benvenuti su CardHaven. I presenti Termini e Condizioni di Servizio ("Termini") regolano l'utilizzo del sito web CardHaven (il "Sito") e l'acquisto di prodotti da esso. Accedendo al Sito e/o effettuando un acquisto, l'utente accetta di essere vincolato da questi Termini.
        </p>

        <h2>2. Account Utente</h2>
        <p>
            Per effettuare acquisti sul Sito, potrebbe essere necessario creare un account. L'utente è responsabile della salvaguardia della propria password e di tutte le attività che si verificano sotto il proprio account. L'utente si impegna a notificare immediatamente a CardHaven qualsiasi violazione della sicurezza o uso non autorizzato del proprio account.
        </p>

        <h2>3. Prodotti e Prezzi</h2>
        <p>
            CardHaven si impegna a descrivere e visualizzare i prodotti nel modo più accurato possibile. Tuttavia, non garantiamo che le descrizioni dei prodotti o altri contenuti del Sito siano completamente accurati, completi o privi di errori. I prezzi dei prodotti sono soggetti a modifiche senza preavviso. Ci riserviamo il diritto di correggere eventuali errori nei prezzi o nelle descrizioni.
        </p>

        <h2>4. Ordini e Pagamenti</h2>
        <p>
            La ricezione di una conferma d'ordine non costituisce la nostra accettazione dell'ordine. Ci riserviamo il diritto di rifiutare o annullare un ordine per qualsiasi motivo, inclusa la limitazione delle quantità disponibili per l'acquisto, inesattezze nelle informazioni sul prodotto o sui prezzi, o problemi identificati dal nostro dipartimento di prevenzione frodi.
        </p>

        <h2>5. Spedizione e Consegna</h2>
        <p>
            I tempi di spedizione e consegna sono stime e non sono garantiti. Il rischio di perdita e la proprietà degli articoli acquistati passano all'utente al momento della nostra consegna al corriere. CardHaven non è responsabile per ritardi nella spedizione o per pacchi smarriti o danneggiati una volta affidati al corriere.
        </p>

        <h2>6. Diritto di Recesso</h2>
        <p>
            Conformemente alla legge, l'utente ha il diritto di recedere dal contratto di acquisto entro 14 giorni dalla ricezione dei prodotti senza fornire alcuna motivazione. Questo diritto non si applica alle carte singole, che sono escluse dal diritto di recesso per la loro natura e per preservarne l'integrità e il valore. I prodotti sigillati devono essere restituiti nelle loro condizioni originali, non aperti e non danneggiati.
        </p>

        <h2>7. Limitazione di Responsabilità</h2>
        <p>
            Nella misura massima consentita dalla legge, CardHaven non sarà responsabile per danni indiretti, incidentali, speciali o consequenziali derivanti da o in connessione con l'uso del Sito o l'acquisto di prodotti da esso.
        </p>

        <h2>8. Legge Applicabile</h2>
        <p>
            I presenti Termini e qualsiasi controversia derivante da essi saranno regolati e interpretati in conformità con le leggi dello Stato Italiano, senza dar luogo a principi di conflitto di leggi.
        </p>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
</body>
</html>
