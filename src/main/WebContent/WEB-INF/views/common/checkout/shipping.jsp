<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="Checkout - Indirizzo di Spedizione" scope="request"/>

<!DOCTYPE html>
<html lang="it">
<head>
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css">
    <%-- Reusing profile.css for general card styling --%>
    <jsp:include page="/components/common_head.jsp"/>
</head>
<body>
<jsp:include page="/components/header.jsp"/>

<main>
    <div>
        <h2>Indirizzi di Spedizione e Fatturazione</h2>
        <jsp:include page="/components/error_message.jsp"/>
        <p>Seleziona gli indirizzi da utilizzare per il tuo ordine.</p>

        <form action="${pageContext.request.contextPath}/common/checkout/shipping" method="post">
            <%--TODO continue--%>



        </form>
    </div>

</main>


<jsp:include page="/components/footer.jsp"/>
</body>
</html>
