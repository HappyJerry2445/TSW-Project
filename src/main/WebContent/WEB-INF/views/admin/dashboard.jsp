<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/dashboard.css">
</head>
<body>

<jsp:include page="/WEB-INF/components/header.jsp"/>

<main class="admin-container">
    <h1>Pannello di Amministrazione</h1>
    <p>Benvenuto nella dashboard di CardHaven. Seleziona un'opzione per iniziare.</p>

    <nav class="admin-nav">
        <ul>
            <li><a href="${pageContext.request.contextPath}/admin/products">Gestione Prodotti</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/orders">Visualizza Ordini</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/users">Gestione Utenti</a></li>
        </ul>
    </nav>

</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>

</body>
</html>
