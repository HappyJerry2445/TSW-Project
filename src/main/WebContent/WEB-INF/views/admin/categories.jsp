<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
    <title>Gestione Categorie | CardHaven</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/products.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/new-product.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/categories.css">
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main class="admin-container">
    <div class="admin-page-header">
        <div class="admin-header-content">
            <h1><i class="fas fa-sitemap"></i> Gestione Categorie</h1>
            <p>Organizza il tuo catalogo creando, modificando ed eliminando le categorie.</p>
        </div>
        <div class="admin-header-actions">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline">
                <i class="fas fa-arrow-left"></i> Torna alla Dashboard
            </a>
        </div>
    </div>
    <jsp:include page="/WEB-INF/components/error_message.jsp"/>

    <div class="categories-layout">
        <div class="category-list-card">
            <h2>Categorie Esistenti</h2>
            <ul class="category-tree">
                <c:if test="${empty rootCategories}">
                    <li>Nessuna categoria trovata.</li>
                </c:if>
                <c:forEach var="category" items="${rootCategories}">
                    <c:set var="category_to_render" value="${category}" scope="request"/>
                    <jsp:include page="_category_item.jsp"/>
                </c:forEach>
            </ul>
        </div>

        <div class="category-form-card">
            <h2 id="form-title">Aggiungi Nuova Categoria</h2>
            <form id="category-form" action="${pageContext.request.contextPath}/admin/categories" method="post">
                <input type="hidden" id="form-action" name="action" value="create">
                <input type="hidden" id="categoryId" name="categoryId">

                <div class="form-group">
                    <label for="categoryName">Nome Categoria <span class="required">*</span></label>
                    <input
                            type="text" id="categoryName" name="categoryName" required
                            onblur="validateFormElement(this)"
                            onsubmit="validateFormElement(this)"
                            pattern="^[a-zA-Z0-9\s'._-]{3,100}$"
                            title="Il nome può contenere lettere, numeri, e caratteri speciali comuni (3-100 caratteri).">
                    <div class="errorFormElem"></div>
                </div>
                <div class="form-group"><label for="parentId">Categoria Genitore</label><select id="parentId"
                                                                                                name="parentId">
                    <option value="">-- Nessuna (Livello Principale) --</option>
                    <c:forEach var="cat" items="${allCategories}">
                        <option value="${cat.id}">${cat.name}</option>
                    </c:forEach></select></div>
                <div class="form-group"><label for="categoryType">Tipo <span class="required">*</span></label><select
                        id="categoryType" name="categoryType" required onblur="validateFormElement(this)"
                        onsubmit="validateFormElement(this)">
                    <option value="Generic">Generica</option>
                    <option value="Card">Carte</option>
                    <option value="Accessory">Accessori</option>
                </select>
                    <div class="errorFormElem"></div>
                </div>
                <div class="form-group"><label for="description">Descrizione</label><textarea id="description"
                                                                                              name="description"
                                                                                              rows="4"
                                                                                              maxlength="500"></textarea>
                </div>

                <div class="form-actions">
                    <button type="button" id="cancel-button" class="btn btn-outline" style="display: none;">Annulla
                    </button>
                    <button type="submit" id="submit-button" class="btn btn-primary">Crea Categoria</button>
                </div>
            </form>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
<script src="${pageContext.request.contextPath}/scripts/admin/categories.js" defer></script>
<script src="${pageContext.request.contextPath}/scripts/validation.js" defer></script>
</body>
</html>
