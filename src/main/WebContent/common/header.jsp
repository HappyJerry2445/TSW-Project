<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<header>
    <div class="container">
        <a href="/" class="logo">CardHaven</a>
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
            <form class="search-bar">
                <input type="search" placeholder="Cerca prodotti..." aria-label="Cerca">
                <button type="submit" aria-label="Invia ricerca"><i class="fas fa-search"></i></button>
            </form>
            <div class="header-icons">
                <!-- TODO: pagina account -->
                <a href="/common/login.jsp" aria-label="Il mio account" class="action-icon account-icon"><i class="fas fa-user"></i></a>
                <!-- TODO: pagina carrello -->
                <a href="#cart" aria-label="Carrello" class="action-icon cart-icon">
                    <i class="fas fa-shopping-cart"></i>
                    <span class="cart-count">0</span>
                </a>
            </div>
        </div>
        <!-- TODO: pagina menu -->
        <button class="mobile-menu-toggle" aria-label="Apri menu">
            <i class="fas fa-bars"></i>
        </button>
    </div>
</header>
