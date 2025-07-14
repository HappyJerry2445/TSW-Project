<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
    <title>Modifica Prodotto | CardHaven</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/products.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/new-product.css">
</head>
<body>
<jsp:include page="/WEB-INF/components/header.jsp"/>

<main class="admin-container">
    <div class="admin-page-header">
        <h1><i class="fas fa-edit"></i> Modifica Prodotto: <c:out value="${product.productName}"/></h1>
        <p>Aggiorna i dettagli del prodotto e clicca su Salva.</p>
    </div>

    <jsp:include page="/WEB-INF/components/error_message.jsp"/>

    <form id="product-form" action="${pageContext.request.contextPath}/admin/products/edit" method="post"
          enctype="multipart/form-data"
          class="product-form">
        <input type="hidden" name="productId" value="${product.productId}">

        <!-- Informazioni Principali -->
        <div class="form-card">
            <h2 class="form-section-title">Informazioni Principali</h2>
            <div class="form-grid">
                <div class="form-group"><label for="productName">Nome Prodotto <span
                        class="required">*</span></label><input type="text" id="productName" name="productName" required
                                                                value="<c:out value='${product.productName}'/>"></div>
                <div class="form-group"><label for="sku">SKU <span class="required">*</span></label><input type="text"
                                                                                                           id="sku"
                                                                                                           name="sku"
                                                                                                           required
                                                                                                           value="<c:out value='${product.sku}'/>">
                </div>
                <div class="form-group"><label for="basePrice">Prezzo Base (€) <span
                        class="required">*</span></label><input type="number" id="basePrice" name="basePrice"
                                                                step="0.01" min="0" required
                                                                value="${product.basePrice}"></div>
                <div class="form-group"><label for="currentPrice">Prezzo Corrente (€) <span
                        class="required">*</span></label><input type="number" id="currentPrice" name="currentPrice"
                                                                step="0.01" min="0" required
                                                                value="${product.currentPrice}"></div>
                <div class="form-group"><label for="stockQuantity">Quantità <span
                        class="required">*</span></label><input type="number" id="stockQuantity" name="stockQuantity"
                                                                min="0" required value="${product.stockQuantity}"></div>
                <div class="form-group">
                    <label for="productType">Tipo Prodotto</label>
                    <input type="text" id="productType" name="productType" value="${product.productType.name()}"
                           readonly disabled>
                </div>
            </div>
        </div>

        <!-- Dettagli Carta Collezionabile -->
        <c:if test="${product.productType == 'TradingCard'}">
            <div id="tradingCardFields" class="form-card">
                <h2 class="form-section-title">Dettagli Carta</h2>
                <div class="form-grid">
                    <div class="form-group"><label for="cardSet">Set</label><input type="text" id="cardSet"
                                                                                   name="cardSet"
                                                                                   value="<c:out value='${cardDetails.cardSet}'/>">
                    </div>
                    <div class="form-group"><label for="cardNumber">Numero</label><input type="text" id="cardNumber"
                                                                                         name="cardNumber"
                                                                                         value="<c:out value='${cardDetails.cardNumber}'/>">
                    </div>
                    <div class="form-group"><label for="rarity">Rarità</label><select id="rarity" name="rarity">
                        <option value="Common" ${cardDetails.rarity == 'Common' ? 'selected' : ''}>Comune</option>
                        ...</select></div>
                    <div class="form-group"><label for="cardCondition">Condizione</label><select id="cardCondition"
                                                                                                 name="cardCondition">
                        <option value="Mint" ${cardDetails.cardCondition == 'Mint' ? 'selected' : ''}>Mint</option>
                        ...</select></div>
                </div>
            </div>
        </c:if>

        <c:if test="${product.productType == 'Accessory'}">
            <div class="form-card"><h2 class="form-section-title">Dettagli Accessorio</h2>
                <div class="form-grid">
                    <div class="form-group"><label for="accessoryType">Tipo</label><select id="accessoryType"
                                                                                           name="accessoryType">
                        <option value="Sleeves" ${accessoryDetails.accessoryType eq 'Sleeves' ? 'selected' : ''}>
                            Bustine
                        </option>
                        ...</select></div>
                    <div class="form-group"><label for="material">Materiale</label><input type="text" id="material"
                                                                                          name="material"
                                                                                          value="<c:out value='${accessoryDetails.material}'/>">
                    </div>
                    <div class="form-group"><label for="color">Colore</label><input type="text" id="color" name="color"
                                                                                    value="<c:out value='${accessoryDetails.color}'/>">
                    </div>
                </div>
            </div>
        </c:if>

        <!-- Immagini e Organizzazione -->
        <div class="form-card">
            <h2 class="form-section-title">Immagini e Organizzazione</h2>
            <div class="image-upload-area form-group">
                <label>Immagini Prodotto</label>
                <input type="file" id="newProductImages" name="newProductImages" multiple
                       accept="image/png, image/jpeg, image/webp" style="display: none;">
                <label for="newProductImages" id="image-drop-zone"><i class="fas fa-upload"></i>
                    <p>Trascina nuove immagini qui, o <span>clicca per aggiungere</span>.</p></label>
                <div id="image-preview-container">
                    <c:forEach var="img" items="${productImages}" varStatus="loop">
                        <div class="image-preview-item" draggable="true" data-image-id="${img.productImageId}"
                             data-identifier="id:${img.imageId}">
                            <img src="${pageContext.request.contextPath}/image/${img.imageId}" alt="Immagine prodotto">
                            <button type="button" class="remove-btn" title="Rimuovi immagine">&times;</button>
                            <c:if test="${loop.first}"><span class="cover-badge">Copertina</span></c:if>
                        </div>
                    </c:forEach>
                </div>
                <input type="hidden" name="imageOrder" id="imageOrder">
                <input type="hidden" name="imagesToDelete" id="imagesToDelete">
            </div>
            <div class="form-group">
                <label for="categories">Categorie</label>
                <select id="categories" name="categories" multiple size="5">
                    <c:forEach var="category" items="${categories}">
                        <option value="${category.id}" ${fn:contains(selectedCategoryIds, category.id) ? 'selected' : ''}>${category.name}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group checkbox-group">
                <input type="checkbox" id="isActive" name="isActive"
                       value="true" ${product.isActive() ? 'checked' : ''}>
                <label for="isActive">Prodotto Attivo</label>
            </div>
        </div>

        <div class="form-actions">
            <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-outline">Annulla</a>
            <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Salva Modifiche</button>
        </div>
    </form>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
<script src="${pageContext.request.contextPath}/scripts/admin/edit-product.js" defer></script>
</body>
</html>
