<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script defer src="${pageContext.request.contextPath}/scripts/notifications.js"></script>

<%--@elvariable id="notificationType" type="java.lang.String"--%>
<%--@elvariable id="notificationMessage" type="java.lang.String"--%>
<c:if test="${not empty sessionScope.notificationMessage}">
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            const message = '<c:out value="${sessionScope.notificationMessage}"/>';
            const type = '<c:out value="${sessionScope.notificationType}"/>';
            if (window.notify && typeof window.notify[type] === 'function') {
                window.notify[type](message);
            } else {
                window.notify.info(message);
            }
        });
    </script>
    <c:remove var="notificationMessage" scope="session"/>
    <c:remove var="notificationType" scope="session"/>
</c:if>
<div id="notification-container"></div>
