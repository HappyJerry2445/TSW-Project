<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="my" uri="/WEB-INF/functions" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
    <title>Gestione Utenti | CardHaven</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/products.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/admin-orders.css">
    <%-- Reuse styles from orders for table/filter layout --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/admin-users.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/WEB-INF/components/header.jsp"/>

<main class="admin-container">
    <div class="admin-page-header">
        <div class="admin-header-content">
            <h1><i class="fas fa-user-cog"></i> Gestione Utenti</h1>
            <p>Visualizza e gestisci gli account degli utenti registrati.</p>
        </div>
        <div class="admin-header-actions">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline">
                <i class="fas fa-arrow-left"></i> Torna alla Dashboard
            </a>
        </div>
    </div>

    <jsp:include page="/WEB-INF/components/error_message.jsp"/>

    <div class="content-card filter-card">
        <h2>Filtra Utenti</h2>
        <form action="${pageContext.request.contextPath}/admin/users" method="get" class="filter-form">
            <div class="form-group">
                <label for="firstName">Nome:</label>
                <input type="text" id="firstName" name="firstName" placeholder="Nome" value="${firstName}">
            </div>
            <div class="form-group">
                <label for="lastName">Cognome:</label>
                <input type="text" id="lastName" name="lastName" placeholder="Cognome" value="${lastName}">
            </div>
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="text" id="email" name="email" placeholder="email@example.com" value="${email}">
            </div>
            <div class="form-group">
                <label for="role">Ruolo:</label>
                <select id="role" name="role">
                    <option value="">Tutti</option>
                    <%--@elvariable id="userRoles" type="com.cardhaven.cardhaven.model.dto.UserDTO.Role[]"--%>
                    <c:forEach var="userRole" items="${userRoles}">
                        <option value="${userRole}" ${userRole == param.role ? 'selected' : ''}>
                            <c:out value="${userRole}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-actions filter-actions">
                <button type="submit" class="btn btn-primary"><i class="fas fa-filter"></i> Applica Filtri</button>
                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline">Reset Filtri</a>
            </div>
        </form>
    </div>

    <div class="content-card">
        <div class="table-container">
            <table class="users-table">
                <thead>
                <tr>
                    <th>ID Utente</th>
                    <th>Nome</th>
                    <th>Email</th>
                    <th>Ruolo</th>
                    <th>Data Registrazione</th>
                    <th>Ultimo Accesso</th>
                    <th>Azioni</th>
                </tr>
                </thead>
                <tbody>
                <%--@elvariable id="users" type="java.util.Collection<com.cardhaven.cardhaven.model.dto.UserDTO>"--%>
                <c:forEach var="user" items="${users}">
                    <tr>
                        <td data-label="ID Utente">#<c:out value="${user.id}"/></td>
                        <td data-label="Nome"><c:out value="${user.firstName}"/> <c:out value="${user.lastName}"/></td>
                        <td data-label="Email"><c:out value="${user.email}"/></td>
                        <td data-label="Ruolo" class="user-role-cell">
                            <form class="update-role-form" action="${pageContext.request.contextPath}/admin/users"
                                  method="post">
                                <input type="hidden" name="action" value="updateRole">
                                <input type="hidden" name="userId" value="${user.id}">
                                <input type="hidden" name="firstName" value="${firstName}">
                                <input type="hidden" name="lastName" value="${lastName}">
                                <input type="hidden" name="email" value="${email}">
                                <input type="hidden" name="role" value="${param.role}">
                                <select name="newRole" class="role-select">
                                    <c:forEach var="userRole" items="${userRoles}">
                                        <option value="${userRole}" ${user.role == userRole ? 'selected' : ''}>
                                            <c:out value="${userRole}"/>
                                        </option>
                                    </c:forEach>
                                </select>
                            </form>
                        </td>
                        <td data-label="Data Registrazione"><c:out value="${my:formatDateTime(user.createdAt)}"/></td>
                        <td data-label="Ultimo Accesso">
                            <c:choose>
                                <c:when test="${not empty user.lastLogin}">
                                    <c:out value="${my:formatDateTime(user.lastLogin)}"/>
                                </c:when>
                                <c:otherwise>
                                    Mai
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td data-label="Azioni">
                            <div class="actions-cell">
                                <form action="${pageContext.request.contextPath}/admin/users" method="post"
                                      onsubmit="return confirm('Sei sicuro di voler eliminare questo utente? L\'azione è irreversibile.');"
                                      style="display: inline;">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="userId" value="${user.id}">
                                    <input type="hidden" name="firstName" value="${firstName}">
                                    <input type="hidden" name="lastName" value="${lastName}">
                                    <input type="hidden" name="email" value="${email}">
                                    <input type="hidden" name="role" value="${param.role}">
                                    <button type="submit" class="action-btn btn-delete" title="Elimina Utente">
                                        <i class="fas fa-trash-alt"></i>
                                    </button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty users}">
                    <tr>
                        <td colspan="7" class="text-center" style="padding: 2rem;">Nessun utente trovato con i filtri
                            selezionati.
                        </td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
<script src="${pageContext.request.contextPath}/scripts/admin/admin-users.js" defer></script>
</body>
</html>
