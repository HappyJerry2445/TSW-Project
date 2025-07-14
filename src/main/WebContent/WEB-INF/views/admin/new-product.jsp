<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <jsp:include page="/WEB-INF/components/common_head.jsp"/>
    <title>Nuovo Prodotto | CardHaven</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/products.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin/new-product.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="/WEB-INF/components/header.jsp"/>

<main class="admin-container">
    <div class="admin-page-header">
        <h1><i class="fas fa-plus-circle"></i> Aggiungi Nuovo Prodotto</h1>
        <p>Compila i campi sottostanti per aggiungere un nuovo articolo al catalogo.</p>
    </div>

    <jsp:include page="/WEB-INF/components/error_message.jsp"/>

    <form action="${pageContext.request.contextPath}/admin/products/new" method="post" enctype="multipart/form-data"
          class="product-form">
        <%--@elvariable id="repopulatedProduct" type="com.cardhaven.cardhaven.model.dto.ProductDTO"--%>
        <div class="form-card">
            <h2 class="form-section-title">Informazioni Principali</h2>
            <div class="form-grid">
                <div class="form-group">
                    <label for="productName">Nome Prodotto <span class="required">*</span></label>
                    <input type="text" id="productName" name="productName" required
                           value="<c:out value='${repopulatedProduct.productName}'/>">
                </div>
                <div class="form-group">
                    <label for="sku">SKU (Codice Univoco) <span class="required">*</span></label>
                    <input type="text" id="sku" name="sku" required value="<c:out value='${repopulatedProduct.sku}'/>">
                </div>
                <div class="form-group">
                    <label for="basePrice">Prezzo di Base (€) <span class="required">*</span></label>
                    <input type="number" id="basePrice" name="basePrice" step="0.01" min="0" required
                           value="${repopulatedProduct.basePrice}">
                </div>
                <div class="form-group">
                    <label for="currentPrice">Prezzo Corrente (€) <span class="required">*</span></label>
                    <input type="number" id="currentPrice" name="currentPrice" step="0.01" min="0" required
                           value="${repopulatedProduct.currentPrice}">
                </div>
                <div class="form-group">
                    <label for="stockQuantity">Quantità in Stock <span class="required">*</span></label>
                    <input type="number" id="stockQuantity" name="stockQuantity" min="0" required
                           value="${repopulatedProduct.stockQuantity}">
                </div>
                <div class="form-group">
                    <label for="productType">Tipo di Prodotto <span class="required">*</span></label>
                    <select id="productType" name="productType" required>
                        <option value="">-- Seleziona un tipo --</option>
                        <c:forEach var="type" items="${productTypes}">
                            <option value="${type.name()}" ${repopulatedProduct.productType == type ? 'selected' : ''}>${type.name()}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group full-width">
                    <label for="description">Descrizione Prodotto</label>
                    <textarea id="description" name="description" rows="4"><c:out
                            value='${repopulatedDescription}'/></textarea>
                </div>
            </div>
        </div>

        <div id="tradingCardFields" class="form-card dynamic-section">
            <h2 class="form-section-title">Dettagli Carta Collezionabile</h2>
            <div class="form-grid">
                <%--@elvariable id="repopulatedCard" type="com.cardhaven.cardhaven.model.dto.TradingCardDTO"--%>
                <div class="form-group"><label for="cardSet">Set/Espansione</label><input type="text" id="cardSet"
                                                                                          name="cardSet"
                                                                                          value="<c:out value='${repopulatedCard.cardSet}'/>">
                </div>
                <div class="form-group"><label for="cardNumber">Numero Carta</label><input type="text" id="cardNumber"
                                                                                           name="cardNumber"
                                                                                           value="<c:out value='${repopulatedCard.cardNumber}'/>">
                </div>
                <div class="form-group"><label for="rarity">Rarità</label><select id="rarity" name="rarity">
                    <option value="Common" ${repopulatedCard.rarity == 'Common' ? 'selected' : ''}>Comune</option>
                    <option value="Uncommon" ${repopulatedCard.rarity == 'Uncommon' ? 'selected' : ''}>Non Comune
                    </option>
                    <option value="Rare" ${repopulatedCard.rarity == 'Rare' ? 'selected' : ''}>Rara</option>
                    <option value="Mythic" ${repopulatedCard.rarity == 'Mythic' ? 'selected' : ''}>Mitica</option>
                    <option value="Secret" ${repopulatedCard.rarity == 'Secret' ? 'selected' : ''}>Segreta</option>
                </select></div>
                <div class="form-group"><label for="cardCondition">Condizione</label><select id="cardCondition"
                                                                                             name="cardCondition">
                    <option value="Mint" ${repopulatedCard.cardCondition == 'Mint' ? 'selected' : ''}>Mint</option>
                    <option value="Near Mint" ${empty repopulatedCard.cardCondition || repopulatedCard.cardCondition == 'Near Mint' ? 'selected' : ''}>
                        Near Mint
                    </option>
                    <option value="Lightly Played" ${repopulatedCard.cardCondition == 'Lightly Played' ? 'selected' : ''}>
                        Lightly Played
                    </option>
                    <option value="Moderately Played" ${repopulatedCard.cardCondition == 'Moderately Played' ? 'selected' : ''}>
                        Moderately Played
                    </option>
                    <option value="Heavily Played" ${repopulatedCard.cardCondition == 'Heavily Played' ? 'selected' : ''}>
                        Heavily Played
                    </option>
                </select></div>
            </div>
        </div>

        <div id="accessoryFields" class="form-card dynamic-section">
            <h2 class="form-section-title">Dettagli Accessorio</h2>
            <div class="form-grid">
                <%--@elvariable id="repopulatedAccessory" type="com.cardhaven.cardhaven.model.dto.AccessoryDTO"--%>
                <div class="form-group"><label for="accessoryType">Tipo Accessorio</label><select id="accessoryType"
                                                                                                  name="accessoryType">
                    <option value="Sleeves" ${repopulatedAccessory.accessoryType == 'Sleeves' ? 'selected' : ''}>Bustine
                        Protettive
                    </option>
                    <option value="Binders" ${repopulatedAccessory.accessoryType == 'Binders' ? 'selected' : ''}>
                        Raccoglitori
                    </option>
                    <option value="Dice" ${repopulatedAccessory.accessoryType == 'Dice' ? 'selected' : ''}>Dadi</option>
                    <option value="Playmats" ${repopulatedAccessory.accessoryType == 'Playmats' ? 'selected' : ''}>
                        Tappetini
                    </option>
                    <option value="Boxes" ${repopulatedAccessory.accessoryType == 'Boxes' ? 'selected' : ''}>
                        Portamazzi
                    </option>
                </select></div>
                <div class="form-group"><label for="material">Materiale</label><input type="text" id="material"
                                                                                      name="material"
                                                                                      value="<c:out value='${repopulatedAccessory.material}'/>">
                </div>
                <div class="form-group"><label for="color">Colore</label><input type="text" id="color" name="color"
                                                                                value="<c:out value='${repopulatedAccessory.color}'/>">
                </div>
            </div>
        </div>

        <div class="form-card">
            <h2 class="form-section-title">Immagini e Organizzazione</h2>
            <div class="form-grid">
                <div class="form-group full-width image-upload-area">
                    <label for="productImages">Carica Immagini</label>
                    <input type="file" id="productImages" name="productImages" multiple
                           accept="image/png, image/jpeg, image/webp" style="display: none;">
                    <label for="productImages" id="image-drop-zone"><i class="fas fa-upload"></i>
                        <p>Trascina le immagini qui, oppure <span>clicca per selezionare</span>.</p></label>
                    <div id="image-preview-container"></div>
                    <input type="hidden" name="imageOrder" id="imageOrder">
                </div>
                <div class="form-group">
                    <label for="categories">Categorie</label>
                    <%--@elvariable id="selectedCategories" type="java.util.List<java.lang.String>"--%>
                    <select id="categories" name="categories" multiple size="5">
                        <c:forEach var="category" items="${categories}">
                            <option value="${category.id}" ${fn:contains(selectedCategories, category.id) ? 'selected' : ''}>${category.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group checkbox-group">
                    <input type="checkbox" id="isActive" name="isActive"
                           value="true" ${empty repopulatedProduct or repopulatedProduct.isActive() ? 'checked' : ''}>
                    <label for="isActive">Prodotto Attivo (visibile sul sito)</label>
                </div>
            </div>
        </div>

        <div class="form-actions">
            <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-outline">Annulla</a>
            <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Salva Prodotto</button>
        </div>
    </form>
</main>

<jsp:include page="/WEB-INF/components/footer.jsp"/>
<script src="${pageContext.request.contextPath}/scripts/admin/new-product.js" defer></script>

</body>
</html>
