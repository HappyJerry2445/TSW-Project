<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Modifica Indirizzo" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user-auth.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/addresses.css" type="text/css">
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main>
    <div class="container mt-3">
        <div class="card user-auth-card">
            <h2 class="section-title">Modifica Indirizzo</h2>

            <jsp:include page="/WEB-INF/components/error_message.jsp"/>

            <%--@elvariable id="address" type="com.cardhaven.cardhaven.model.dto.AddressDTO"--%>
            <form action="${pageContext.request.contextPath}/common/addresses/edit" method="post">
                <input type="hidden" name="id" value="<c:out value="${address.addressID}"/>">

                <div class="mb-1">
                    <label for="street">Via/Piazza:</label>
                    <input type="text" id="street" name="street" class="form-input"
                           value="<c:out value="${address.streetAddress}"/>" required>
                </div>
                <div class="mb-1">
                    <label for="city">Città:</label>
                    <input type="text" id="city" name="city" class="form-input"
                           value="<c:out value="${address.city}"/>" required>
                </div>
                <div class="mb-1">
                    <label for="state">Provincia/Stato:</label>
                    <input type="text" id="state" name="state" class="form-input"
                           value="<c:out value="${address.state}"/>">
                </div>
                <div class="mb-1">
                    <label for="postalCode">CAP:</label>
                    <input type="text" id="postalCode" name="postalCode" class="form-input"
                           value="<c:out value="${address.postalCode}"/>" required>
                </div>
                <div class="mb-1">
                    <label for="country">Nazione:</label>
                    <input type="text" id="country" name="country" class="form-input"
                           value="<c:out value="${address.country}"/>" required>
                </div>

                <div class="profile-actions">
                    <button type="submit" class="btn btn-primary">Salva Modifiche</button>
                    <a href="${pageContext.request.contextPath}/common/addresses" class="btn btn-outline">Annulla</a>
                </div>
            </form>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
</body>
</html>