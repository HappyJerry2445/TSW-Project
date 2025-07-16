<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Privacy Policy | CardHaven" scope="request"/>

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
    <h1 class="section-title">Informativa sulla Privacy</h1>
    <p class="text-center">Ultimo aggiornamento: 24 Maggio 2024</p>

    <div class="static-page-content">
        <p>
            CardHaven si impegna a proteggere la privacy dei propri utenti. La presente Informativa sulla Privacy descrive come raccogliamo, utilizziamo, divulghiamo e proteggiamo le tue informazioni quando visiti il nostro sito web.
        </p>

        <h2>1. Dati che Raccogliamo</h2>
        <p>Potremmo raccogliere informazioni su di te in vari modi. Le informazioni che possiamo raccogliere sul Sito includono:</p>
        <ul>
            <li><strong>Dati Personali:</strong> Informazioni di identificazione personale, come nome, indirizzo di spedizione, indirizzo email e numero di telefono, che ci fornisci volontariamente quando ti registri al Sito o effettui un ordine.</li>
            <li><strong>Dati Finanziari:</strong> Dati relativi ai pagamenti (es. numero di carta di credito). Per la tua sicurezza, non memorizziamo i dati completi della carta di credito sui nostri server. Tutte le transazioni di pagamento sono gestite tramite gateway di pagamento sicuri e conformi agli standard PCI-DSS.</li>
            <li><strong>Dati Derivati:</strong> Informazioni che i nostri server raccolgono automaticamente quando accedi al Sito, come il tuo indirizzo IP, il tipo di browser, il sistema operativo, i tempi di accesso e le pagine che hai visualizzato direttamente prima e dopo l'accesso al Sito.</li>
        </ul>

        <h2>2. Utilizzo dei Tuoi Dati</h2>
        <p>Avere informazioni accurate su di te ci permette di fornirti un'esperienza fluida, efficiente e personalizzata. In particolare, possiamo utilizzare le informazioni raccolte su di te tramite il Sito per:</p>
        <ul>
            <li>Creare e gestire il tuo account.</li>
            <li>Elaborare i tuoi ordini e gestire i pagamenti.</li>
            <li>Inviarti email relative al tuo account o ordine.</li>
            <li>Monitorare e analizzare l'utilizzo e le tendenze per migliorare la tua esperienza con il Sito.</li>
            <li>Prevenire attività fraudolente e proteggere la sicurezza del Sito.</li>
        </ul>

        <h2>3. Divulgazione dei Tuoi Dati</h2>
        <p>Non condivideremo le tue informazioni con terze parti se non nelle seguenti situazioni:</p>
        <ul>
            <li><strong>Per Legge o per Proteggere i Diritti:</strong> Se riteniamo che il rilascio di informazioni su di te sia necessario per rispondere a processi legali, per indagare o rimediare a potenziali violazioni delle nostre policy, o per proteggere i diritti, la proprietà e la sicurezza di altri.</li>
            <li><strong>Fornitori di Servizi Terzi:</strong> Potremmo condividere le tue informazioni con terze parti che eseguono servizi per noi o per nostro conto, inclusi l'elaborazione dei pagamenti, la spedizione, l'analisi dei dati e l'assistenza clienti.</li>
        </ul>

        <h2>4. Sicurezza dei Tuoi Dati</h2>
        <p>
            Utilizziamo misure di sicurezza amministrative, tecniche e fisiche per aiutare a proteggere le tue informazioni personali. Sebbene abbiamo adottato misure ragionevoli per proteggere le informazioni personali che ci fornisci, tieni presente che nessuna misura di sicurezza è perfetta o impenetrabile e nessun metodo di trasmissione dei dati può essere garantito contro qualsiasi intercettazione o altro tipo di uso improprio.
        </p>

        <h2>5. Cookie e Tecnologie di Tracciamento</h2>
        <p>
            Potremmo utilizzare cookie e altre tecnologie di tracciamento sul Sito per aiutare a personalizzare il Sito e migliorare la tua esperienza. Quando accedi al Sito, le tue informazioni personali non vengono raccolte attraverso l'uso della tecnologia di tracciamento. La maggior parte dei browser è impostata per accettare i cookie per impostazione predefinita. Puoi scegliere di rimuovere o rifiutare i cookie, ma tieni presente che tale azione potrebbe influire sulla disponibilità e sulla funzionalità del Sito.
        </p>

        <h2>6. I Tuoi Diritti</h2>
        <p>
            Hai il diritto di accedere, correggere o cancellare i tuoi dati personali. Puoi rivedere o modificare le informazioni nel tuo account in qualsiasi momento accedendo alle impostazioni del tuo account. Se desideri chiudere il tuo account, puoi farlo dalla pagina del tuo profilo.
        </p>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
</body>
</html>
