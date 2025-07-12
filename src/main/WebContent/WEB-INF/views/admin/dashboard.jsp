<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
    <title>Admin Dashboard | CardHaven</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/WEB-INF/components/header.jsp"/>

<main class="admin-container">
    <div class="admin-header">
        <h1><i class="fas fa-tachometer-alt"></i> Pannello di Amministrazione</h1>
        <p>Benvenuto nella dashboard di CardHaven. Gestisci tutti gli aspetti del tuo negozio online da qui.</p>
    </div>

    <div class="admin-stats">
        <div class="stats-card">
            <i class="fas fa-shopping-cart"></i>
            <h3>Ordini Recenti</h3>
            <p class="stats-count">0</p>
        </div>
        <div class="stats-card">
            <i class="fas fa-users"></i>
            <h3>Utenti Totali</h3>
            <p class="stats-count">0</p>
        </div>
        <div class="stats-card">
            <i class="fas fa-box"></i>
            <h3>Prodotti</h3>
            <p class="stats-count">0</p>
        </div>
        <div class="stats-card">
            <i class="fas fa-star"></i>
            <h3>Recensioni</h3>
            <p class="stats-count">0</p>
        </div>
    </div>

    <div class="admin-sections">
        <!-- Gestione Catalogo -->
        <section class="admin-section">
            <h2><i class="fas fa-tags"></i> Gestione Catalogo</h2>
            <div class="admin-cards">
                <a href="${pageContext.request.contextPath}/admin/products" class="admin-card">
                    <i class="fas fa-box-open"></i>
                    <h3>Gestione Prodotti</h3>
                    <p>Aggiungi, modifica o rimuovi prodotti dal catalogo</p>
                </a>
                <a href="${pageContext.request.contextPath}/admin/categories" class="admin-card">
                    <i class="fas fa-folder-open"></i>
                    <h3>Gestione Categorie</h3>
                    <p>Organizza i prodotti in categorie</p>
                </a>
                <a href="${pageContext.request.contextPath}/admin/inventory" class="admin-card">
                    <i class="fas fa-warehouse"></i>
                    <h3>Gestione Inventario</h3>
                    <p>Controlla e aggiorna la disponibilità dei prodotti</p>
                </a>
            </div>
        </section>

        <!-- Gestione Ordini e Utenti -->
        <section class="admin-section">
            <h2><i class="fas fa-users-gear"></i> Gestione Clienti</h2>
            <div class="admin-cards">
                <a href="${pageContext.request.contextPath}/admin/orders" class="admin-card">
                    <i class="fas fa-truck"></i>
                    <h3>Gestione Ordini</h3>
                    <p>Visualizza e gestisci gli ordini effettuati</p>
                </a>
                <a href="${pageContext.request.contextPath}/admin/users" class="admin-card">
                    <i class="fas fa-user-cog"></i>
                    <h3>Gestione Utenti</h3>
                    <p>Gestisci gli account degli utenti registrati</p>
                </a>
                <a href="${pageContext.request.contextPath}/admin/reviews" class="admin-card">
                    <i class="fas fa-star"></i>
                    <h3>Gestione Recensioni</h3>
                    <p>Modera le recensioni degli utenti</p>
                </a>
            </div>
        </section>

        <!-- Analisi e Reports -->
        <section class="admin-section">
            <h2><i class="fas fa-chart-line"></i> Analisi e Statistiche</h2>
            <div class="admin-cards">
                <a href="${pageContext.request.contextPath}/admin/analytics/sales" class="admin-card">
                    <i class="fas fa-chart-pie"></i>
                    <h3>Statistiche Vendite</h3>
                    <p>Visualizza l'andamento delle vendite</p>
                </a>
                <a href="${pageContext.request.contextPath}/admin/analytics/products" class="admin-card">
                    <i class="fas fa-chart-bar"></i>
                    <h3>Prodotti Popolari</h3>
                    <p>Analizza i prodotti più venduti</p>
                </a>
                <a href="${pageContext.request.contextPath}/admin/reports" class="admin-card">
                    <i class="fas fa-file-alt"></i>
                    <h3>Report Generali</h3>
                    <p>Genera ed esporta report dettagliati</p>
                </a>
            </div>
        </section>

        <!-- Configurazione -->
        <section class="admin-section">
            <h2><i class="fas fa-cogs"></i> Impostazioni Sistema</h2>
            <div class="admin-cards">
                <a href="${pageContext.request.contextPath}/admin/settings/store" class="admin-card">
                    <i class="fas fa-store-alt"></i>
                    <h3>Configurazione Negozio</h3>
                    <p>Gestisci le informazioni del negozio</p>
                </a>
                <a href="${pageContext.request.contextPath}/admin/settings/attributes" class="admin-card">
                    <i class="fas fa-list-check"></i>
                    <h3>Gestione Attributi</h3>
                    <p>Configura gli attributi dei prodotti</p>
                </a>
                <a href="${pageContext.request.contextPath}/admin/settings/backup" class="admin-card">
                    <i class="fas fa-database"></i>
                    <h3>Backup e Ripristino</h3>
                    <p>Gestisci backup del database</p>
                </a>
            </div>
        </section>
    </div>

    <!-- Quick Actions -->
    <section class="admin-quick-actions">
        <h2>Azioni Rapide</h2>
        <div class="quick-actions-buttons">
            <a href="${pageContext.request.contextPath}/admin/products/new" class="action-button">
                <i class="fas fa-plus"></i> Nuovo Prodotto
            </a>
            <a href="${pageContext.request.contextPath}/admin/orders/pending" class="action-button">
                <i class="fas fa-clock"></i> Ordini in Attesa
            </a>
            <a href="${pageContext.request.contextPath}/admin/inventory/low" class="action-button">
                <i class="fas fa-exclamation-triangle"></i> Prodotti in Esaurimento
            </a>
            <a href="${pageContext.request.contextPath}/admin/reviews/pending" class="action-button">
                <i class="fas fa-comment-dots"></i> Recensioni da Approvare
            </a>
        </div>
    </section>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>

<script>
    // Script per caricare i dati statistici in modo asincrono
    document.addEventListener('DOMContentLoaded', function () {
        // Qui si potrebbe implementare una chiamata AJAX per ottenere i dati delle statistiche
        // Per ora utilizziamo valori di esempio
        document.querySelectorAll('.stats-count')[0].textContent = '12';  // Ordini recenti
        document.querySelectorAll('.stats-count')[1].textContent = '87';  // Utenti totali
        document.querySelectorAll('.stats-count')[2].textContent = '254'; // Prodotti
        document.querySelectorAll('.stats-count')[3].textContent = '36';  // Recensioni
    });
</script>

</body>
</html>