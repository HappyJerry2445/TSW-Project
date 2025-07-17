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
        <div class="nav-overlay"></div>
        <nav class="main-nav">
            <ul>
                <li class="nav-item dropdown">
                    <a href="#" class="dropdown-toggle" onclick="return false;">
                        Categorie <i class="fas fa-chevron-down dropdown-icon"></i>
                    </a>
                    <ul id="category-header-dropdown-menu" class="dropdown-menu">
                        <c:forEach var="category" items="${applicationScope.allCategories}">
                            <c:if test="${empty category.parentId}">

                                <%-- Check if this category has children to add a specific class --%>
                                <c:set var="hasChildren" value="${false}"/>
                                <c:forEach var="child" items="${applicationScope.allCategories}">
                                    <c:if test="${not hasChildren and child.parentId == category.id}">
                                        <c:set var="hasChildren" value="${true}"/>
                                    </c:if>
                                </c:forEach>

                                <li class="${hasChildren ? 'has-submenu' : ''}">
                                    <a href="${pageContext.request.contextPath}/products/category/${category.id}">
                                        <c:out value="${category.name}"/>
                                        <c:if test="${hasChildren}">
                                            <i class="fas fa-chevron-right submenu-icon"></i>
                                        </c:if>
                                    </a>
                                    <c:if test="${hasChildren}">
                                        <ul class="submenu">
                                            <c:forEach var="child" items="${applicationScope.allCategories}">
                                                <c:if test="${child.parentId == category.id}">
                                                    <li>
                                                        <a href="${pageContext.request.contextPath}/products/category/${child.id}">
                                                            <c:out value="${child.name}"/>
                                                        </a>
                                                    </li>
                                                </c:if>
                                            </c:forEach>
                                        </ul>
                                    </c:if>
                                </li>
                            </c:if>
                        </c:forEach>
                    </ul>
                </li>
                <li class="nav-item"><a
                        href="${pageContext.request.contextPath}/products/search?onSale=true">Offerte</a></li>
            </ul>
        </nav>
        <div class="header-actions">
            <!-- TODO: implementare ricerca -->
            <form action="${pageContext.request.contextPath}/products/search" method="get" class="search-bar"
                  id="search-form" autocomplete="off">
                <input type="search" name="query" id="search-input" placeholder="Cerca prodotti..." aria-label="Cerca">
                <div class="search-spinner" id="search-spinner"><i class="fas fa-spinner"></i></div>
                <button type="submit" aria-label="Invia ricerca"><i class="fas fa-search"></i></button>
                <div class="search-suggestions-container" id="suggestions-container"></div>
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
                <script defer src="${pageContext.request.contextPath}/scripts/mobile-menu.js"></script>
                <script defer src="${pageContext.request.contextPath}/scripts/mobile-accordion.js"></script>
                <script defer src="${pageContext.request.contextPath}/scripts/search-suggestions.js"></script>
            </div>
        </div>
        <!-- TODO: pagina menu -->
        <button class="mobile-menu-toggle" aria-label="Apri menu">
            <i class="fas fa-bars"></i>
        </button>
    </div>
</header>
