<%@ page import="com.cardhaven.cardhaven.model.dao.UserDAO" %>
<%@ page import="javax.sql.DataSource" %>
<%@ page import="com.cardhaven.cardhaven.model.dto.UserDTO" %>
<%@ page import="java.sql.SQLException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/components/notification.jsp"/>
<header>
    <div class="container">
        <a href="${pageContext.request.contextPath}/" class="logo">CardHaven</a>
        <nav class="main-nav">
            <ul>
                <!-- TODO: Sezione placeholder -->
                <li><a href="#carte-collezionabili">Carte Collezionabili</a></li>
                <li><a href="#accessori">Accessori</a></li>
                <li><a href="#novita">Novità</a></li>
                <li><a href="#offerte">Offerte</a></li>
            </ul>
        </nav>
        <div class="header-actions">
            <!-- TODO: implementare ricerca -->
            <form action="${pageContext.request.contextPath}/products/search" method="get" class="search-bar">
                <input type="search" name="query" placeholder="Cerca prodotti..." aria-label="Cerca">
                <button type="submit" aria-label="Invia ricerca"><i class="fas fa-search"></i></button>
            </form>
            <div class="header-icons">
                <%--@elvariable id="userRole" type="com.cardhaven.cardhaven.model.dto.UserDTO.Role"--%>
                <%
                    UserDTO.Role userRole = null;
                    var ds = (DataSource) request.getServletContext().getAttribute("ds");
                    UserDAO userDAO = new UserDAO(ds);
                    try {
                        UserDTO userDTO = userDAO.getById((Integer) session.getAttribute("userId"));
                        userRole = userDTO.getRole();
                        request.setAttribute("userRole", userRole);
                    } catch (Exception e) {
                    }
                %>
                <c:if test="${userRole == 'Admin'}">
                    <a id="admin-dashboard-icon" href="${pageContext.request.contextPath}/admin/dashboard"
                       aria-label="Dashboard Amministrazione"
                       class="action-icon">
                        <i class="fa-solid fa-solar-panel"></i>
                    </a>
                </c:if>
                <a id="account-icon" href="${pageContext.request.contextPath}/common/profile"
                   aria-label="Il mio account"
                   class="action-icon">
                    <i class="fas fa-user"></i></a>
                <%--@elvariable id="userId" type="java.lang.Integer"--%>
                <c:if test="${not empty userId}">
                    <a id="logout-icon" href="${pageContext.request.getContextPath()}/logout" aria-label="Logout"
                       class="action-icon">
                        <i class="fa-solid fa-right-from-bracket"></i>
                    </a>
                </c:if>
                <a id="cart-icon" href="${pageContext.request.contextPath}/cart" aria-label="Carrello"
                   class="action-icon">
                    <i class="fas fa-shopping-cart"></i>
                    <span class="cart-count hidden">0</span>
                </a>
                <script>
                    const APP_CONTEXT_PATH = "${pageContext.request.contextPath}";
                </script>
                <script defer src="${pageContext.request.contextPath}/scripts/cart-count-update.js"></script>
            </div>
        </div>
        <!-- TODO: pagina menu -->
        <button class="mobile-menu-toggle" aria-label="Apri menu">
            <i class="fas fa-bars"></i>
        </button>
    </div>
</header>
