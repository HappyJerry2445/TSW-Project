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
            <h3>Ordini totali</h3>
            <p class="stats-count"><i class="fas fa-cog spin"></i></p>
        </div>
        <div class="stats-card">
            <i class="fas fa-users"></i>
            <h3>Utenti Totali</h3>
            <p class="stats-count"><i class="fas fa-cog spin"></i></p>
        </div>
        <div class="stats-card">
            <i class="fas fa-box"></i>
            <h3>Prodotti Totali</h3>
            <p class="stats-count"><i class="fas fa-cog spin"></i></p>
        </div>
        <div class="stats-card">
            <i class="fas fa-star"></i>
            <h3>Recensioni Totali</h3>
            <p class="stats-count"><i class="fas fa-cog spin"></i></p>
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
            </div>
        </section>

        <!-- Gestione Clienti -->
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

    </div>

</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>

<script src="${pageContext.request.contextPath}/scripts/admin/dashboard-stats.js"></script>

</body>
</html>