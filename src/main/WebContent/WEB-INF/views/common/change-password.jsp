<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Cambia Password" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user-auth.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/profile.css" type="text/css">
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main>
    <div class="center-child mt-3">
        <div class="card user-auth-card change-password-card">
            <h2 class="section-title">Cambia Password</h2>

            <jsp:include page="/WEB-INF/components/error_message.jsp"/>

            <!-- TODO: Add Regex -->
            <form action="${pageContext.request.contextPath}/common/change-password" method="post"
                  id="changePasswordForm">
                <div class="mb-1">
                    <label for="currentPassword">Password Attuale:</label>
                    <input type="password" id="currentPassword" name="currentPassword" class="form-input" required>
                </div>
                <div class="mb-1">
                    <label for="newPassword">Nuova Password:</label>
                    <input type="password" id="newPassword" name="newPassword" class="form-input" required>
                </div>
                <div class="mb-1">
                    <label for="confirmNewPassword">Conferma Nuova Password:</label>
                    <input type="password" id="confirmNewPassword" name="confirmNewPassword" class="form-input"
                           required>
                </div>

                <div class="profile-actions">
                    <button type="submit" class="btn btn-primary">Cambia Password</button>
                    <a href="${pageContext.request.contextPath}/common/profile" class="btn btn-outline">Annulla</a>
                </div>
            </form>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
<script src="${pageContext.request.contextPath}/scripts/change-password.js"></script>
</body>
</html>