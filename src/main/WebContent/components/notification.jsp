<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script defer src="${pageContext.request.contextPath}/scripts/notifications.js"></script>

<%--@elvariable id="notificationType" type="java.lang.String"--%>
<%--@elvariable id="notificationMessage" type="java.lang.String"--%>
<c:if test="${not empty notificationMessage}">
    <script>
        document.addEventListener('DOMContentLoaded', function () {

            <c:if test="${not empty notificationType}">
            notify.<c:out value="${notificationType}"/>('<c:out value="${notificationMessage}"/>');
            </c:if>

            <c:if test="${empty notificationType}">
            notify.info('<c:out value="${notificationMessage}"/>');
            </c:if>
        });
    </script>
    <c:remove var="notificationMessage" scope="session"/>
    <c:remove var="notificationType" scope="session"/>
</c:if>
<div id="notification-container"></div>
