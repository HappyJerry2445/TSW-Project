<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="I Miei Indirizzi" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/profile.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/addresses.css" type="text/css">
    <%-- Reusing profile.css for general card styling --%>
    <jsp:include page="/components/common_head.jsp"/>
</head>
<body>
<jsp:include page="/components/header.jsp"/>

<main>
    <div class="container mt-3">
        <div class="card profile-card"> <%-- Using profile-card for consistent styling --%>
            <h2 class="section-title">I Miei Indirizzi</h2>

            <jsp:include page="/components/error_message.jsp"/>

            <c:choose>
                <%--@elvariable id="addresses" type="java.util.List<com.cardhaven.cardhaven.model.dto.AddressDTO>"--%>
                <c:when test="${not empty addresses}">
                    <div class="address-list"> <%-- This class will need minimal, if any, specific styling. Maybe just a flexbox or grid for layout if needed. --%>
                        <c:forEach var="address" items="${addresses}">
                            <div class="address-item rounded-md"> <%-- 'mb-3', 'p-3', 'border', 'rounded' are likely utility classes from style.css or a framework emulated by it. --%>
                                <c:if test="${address.isDefault()}">
                                    <span class="badge bg-success">Predefinito</span> <%-- 'badge' and 'bg-success' are likely utility classes from style.css. --%>
                                </c:if>
                                <div class="address-details">
                                    <p class="address-field">
                                        <span class="address-label">Tipo:</span>
                                        <span class="address-value"><c:out value="${address.addressType}"/></span>
                                    </p>
                                    <p class="address-field">
                                        <!--<span class="address-label">Indirizzo:</span>-->
                                        <span class="address-value"><c:out value="${address.streetAddress}"/></span>
                                    </p>
                                    <p class="address-field">
                                        <!--<span class="address-label">Città/Stato/CAP:</span>-->
                                        <span class="address-value">
                                        <c:out value="${address.city}"/>,
                                        <c:if test="${not empty address.state}"><c:out value="${address.state}"/>,
                                        </c:if>
                                        <c:out value="${address.postalCode}"/>
                                    </span>
                                    </p>
                                    <p class="address-field">
                                        <!--<span class="address-label">Nazione:</span>-->
                                        <span class="address-value"><c:out value="${address.country}"/></span>
                                    </p>
                                </div>
                                <div class="address-actions mt-2"> <%-- 'mt-2' likely a utility class. 'address-actions' could be styled if multiple buttons need specific layout. --%>
                                    <a href="${pageContext.request.contextPath}/common/addresses/edit?addressId=${address.addressID}"
                                       class="btn  btn-sm">Modifica</a> <%-- 'btn', 'btn-secondary', 'btn-sm' are expected in style.css. --%>
                                    <a href="${pageContext.request.contextPath}/common/addresses/delete?addressId=${address.addressID}"
                                       class="btn btn-sm">Elimina</a> <%-- 'btn', 'btn-danger', 'btn-sm' are expected in style.css. --%>
                                    <c:if test="${!address.isDefault()}">
                                        <a href="${pageContext.request.contextPath}/common/addresses/set-default?addressId=${address.addressID}"
                                           class="btn btn-info btn-sm">Imposta come
                                            Predefinito</a> <%-- 'btn', 'btn-info', 'btn-sm' are expected in style.css. --%>
                                    </c:if>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="text-center">Non hai ancora nessun indirizzo
                        salvato.</p> <%-- 'text-center' is likely a utility class. --%>
                </c:otherwise>
            </c:choose>

            <div class="address-buttons text-center mt-3"> <%-- 'text-center', 'mt-3' are utility classes. --%>
                <a href="${pageContext.request.contextPath}/common/addresses/add" class="btn btn-primary ">Aggiungi
                    Nuovo
                    Indirizzo</a> <%-- 'btn', 'btn-primary' are expected in style.css. --%>
                <a href="${pageContext.request.contextPath}/common/profile" class="btn btn-outline">Torna al
                    Profilo</a> <%-- 'btn', 'btn-outline' are expected in style.css. --%>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/components/footer.jsp"/>
</body>
</html>

