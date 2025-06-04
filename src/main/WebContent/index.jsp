<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Homepage" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
    <jsp:include page="components/common_head.jsp"/>
</head>
<body>
<jsp:include page="./components/header.jsp"/>

<jsp:include page="./components/footer.jsp"/>
</body>
</html>
