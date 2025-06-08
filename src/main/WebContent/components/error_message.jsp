<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="errors" type="java.util.List<java.lang.String>"--%>
<c:if test="${not empty errors}">
    <div class="alert alert-danger">
        <c:forEach var="error" items="${errors}">
            <p><c:out value="${error}"/></p>
        </c:forEach>
    </div>
</c:if>
