<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Domande Frequenti (FAQ) | CardHaven" scope="request"/>

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
    <h1 class="section-title">Domande Frequenti (FAQ)</h1>

    <div class="static-page-content">
        <p class="text-center">
            Trova qui le risposte alle domande più comuni sui nostri prodotti, servizi e policy.
        </p>

        <div class="faq-accordion">
            <details class="faq-item">
                <summary>Quali sono i tempi e i costi di spedizione?</summary>
                <div class="faq-content">
                    <p>La spedizione standard in Italia richiede solitamente 3-5 giorni lavorativi e ha un costo di 5,90 €. Offriamo la spedizione gratuita per tutti gli ordini superiori a 79,00 €.</p>
                    <p>È disponibile anche una spedizione express (1-2 giorni lavorativi) al costo di 9,90 €.</p>
                </div>
            </details>

            <details class="faq-item">
                <summary>Quali metodi di pagamento accettate?</summary>
                <div class="faq-content">
                    <p>Accettiamo i seguenti metodi di pagamento:</p>
                    <ul>
                        <li>Carte di Credito e Debito (Visa, MasterCard, American Express)</li>
                        <li>PayPal</li>
                        <li>Bonifico Bancario Anticipato</li>
                        <li>Pagamento alla consegna (Contrassegno) con un supplemento di 4,00 €</li>
                    </ul>
                </div>
            </details>

            <details class="faq-item">
                <summary>È possibile restituire un prodotto?</summary>
                <div class="faq-content">
                    <p>Sì, è possibile restituire la maggior parte dei prodotti entro 14 giorni dalla data di consegna, a condizione che siano sigillati e nelle loro condizioni originali. Le carte singole non possono essere restituite per preservarne le condizioni. Per avviare un reso, contatta il nostro <a href="${pageContext.request.contextPath}/pages/contact">servizio clienti</a>.</p>
                </div>
            </details>

            <details class="faq-item">
                <summary>Come posso tracciare il mio ordine?</summary>
                <div class="faq-content">
                    <p>Una volta che il tuo ordine è stato spedito, riceverai un'email di conferma contenente il link per il tracciamento del pacco. Puoi anche trovare lo stato e il link di tracciamento nella sezione "I Miei Ordini" del tuo <a href="${pageContext.request.contextPath}/common/profile">profilo</a>.</p>
                </div>
            </details>

            <details class="faq-item">
                <summary>Cosa significa la condizione delle carte (Mint, Near Mint, etc.)?</summary>
                <div class="faq-content">
                    <p>La condizione delle carte singole descrive il loro stato di usura. Utilizziamo una scala standard:</p>
                    <ul>
                        <li><strong>Mint (M):</strong> Carta perfetta, appena uscita dalla bustina.</li>
                        <li><strong>Near Mint (NM):</strong> Carta quasi perfetta, con al massimo un paio di difetti minori visibili solo a un'attenta ispezione.</li>
                        <li><strong>Lightly Played (LP):</strong> Carta con lievi segni di usura sui bordi o sulla superficie.</li>
                        <li><strong>Moderately Played (MP):</strong> Carta con usura più evidente ma ancora perfettamente giocabile in tornei (con bustine protettive).</li>
                        <li><strong>Heavily Played (HP):</strong> Carta con segni di usura significativi come pieghe, graffi o bordi rovinati.</li>
                    </ul>
                </div>
            </details>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
</body>
</html>
