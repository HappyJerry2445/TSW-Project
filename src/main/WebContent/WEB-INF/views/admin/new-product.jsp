<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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

    <form action="${pageContext.request.contextPath}/admin/products/new" method="post" enctype="multipart/form-data"
          id="product-form"
          class="product-form">
        <div class="form-card">
            <h2 class="form-section-title">Informazioni Principali</h2>
            <div class="form-grid">
                <div class="form-group">
                    <label for="productName">Nome Prodotto <span class="required">*</span></label>
                    <input type="text" id="productName" name="productName" required>
                </div>
                <div class="form-group">
                    <label for="sku">SKU (Codice Univoco) <span class="required">*</span></label>
                    <input type="text" id="sku" name="sku" required>
                </div>
                <div class="form-group">
                    <label for="basePrice">Prezzo di Base (€) <span class="required">*</span></label>
                    <input type="number" id="basePrice" name="basePrice" step="0.01" min="0" required>
                </div>
                <div class="form-group">
                    <label for="currentPrice">Prezzo Corrente (€) <span class="required">*</span></label>
                    <input type="number" id="currentPrice" name="currentPrice" step="0.01" min="0" required>
                </div>
                <div class="form-group">
                    <label for="stockQuantity">Quantità in Stock <span class="required">*</span></label>
                    <input type="number" id="stockQuantity" name="stockQuantity" min="0" required>
                </div>
                <div class="form-group">
                    <label for="productType">Tipo di Prodotto <span class="required">*</span></label>
                    <%--@elvariable id="productTypes" type="com.cardhaven.cardhaven.model.dto.ProductDTO.ProductType[]"--%>
                    <select id="productType" name="productType" required>
                        <option value="">-- Seleziona un tipo --</option>
                        <c:forEach var="type" items="${productTypes}">
                            <option value="${type.name()}">${type.name()}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group full-width">
                    <label for="description">Descrizione Prodotto</label>
                    <textarea id="description" name="description" rows="4"></textarea>
                </div>
            </div>
        </div>

        <!-- Sezione Dinamica per Carte Collezionabili -->
        <div id="tradingCardFields" class="form-card dynamic-section">
            <h2 class="form-section-title">Dettagli Carta Collezionabile</h2>
            <div class="form-grid">
                <div class="form-group">
                    <label for="cardSet">Set/Espansione <span class="required">*</span></label>
                    <input type="text" id="cardSet" name="cardSet" required>
                </div>
                <div class="form-group">
                    <label for="cardNumber">Numero Carta <span class="required">*</span></label>
                    <input type="text" id="cardNumber" name="cardNumber" required>
                </div>
                <div class="form-group">
                    <label for="rarity">Rarità <span class="required">*</span></label>
                    <select id="rarity" name="rarity" required>
                        <option value="Common">Comune</option>
                        <option value="Uncommon">Non Comune</option>
                        <option value="Rare">Rara</option>
                        <option value="Mythic">Mitica</option>
                        <option value="Secret">Segreta</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="cardCondition">Condizione <span class="required">*</span></label>
                    <select id="cardCondition" name="cardCondition" required>
                        <option value="Mint">Mint</option>
                        <option value="Near Mint" selected>Near Mint</option>
                        <option value="Lightly Played">Lightly Played</option>
                        <option value="Moderately Played">Moderately Played</option>
                        <option value="Heavily Played">Heavily Played</option>
                    </select>
                </div>
            </div>
        </div>

        <!-- Sezione Dinamica per Accessori -->
        <div id="accessoryFields" class="form-card dynamic-section">
            <h2 class="form-section-title">Dettagli Accessorio</h2>
            <div class="form-grid">
                <div class="form-group">
                    <label for="accessoryType">Tipo Accessorio <span class="required">*</span></label>
                    <select id="accessoryType" name="accessoryType" required>
                        <option value="Sleeves">Bustine Protettive</option>
                        <option value="Binders">Raccoglitori</option>
                        <option value="Dice">Dadi</option>
                        <option value="Playmats">Tappetini</option>
                        <option value="Boxes">Portamazzi</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="material">Materiale</label>
                    <input type="text" id="material" name="material">
                </div>
                <div class="form-group">
                    <label for="color">Colore</label>
                    <input type="text" id="color" name="color">
                </div>
            </div>
        </div>
        <!-- Gestione Immagini (Nuova Sezione) -->
        <div class="form-card">
            <h2 class="form-section-title">Immagini Prodotto</h2>
            <div class="form-group full-width image-upload-area">
                <label for="productImages">Carica Immagini</label>
                <input type="file" id="productImages" name="productImages" multiple
                       accept="image/png, image/jpeg, image/webp" style="display: none;">
                <label for="productImages" id="image-drop-zone">
                    <i class="fas fa-upload"></i>
                    <p>Trascina le immagini qui, oppure <span>clicca per selezionare</span>.</p>
                </label>
                <div id="image-preview-container"></div>
                <input type="hidden" name="imageOrder" id="imageOrder">
            </div>
        </div>

        <div class="form-card">
            <h2 class="form-section-title">Organizzazione</h2>
            <div class="form-grid">
                <div class="form-group">
                    <label for="categories">Categorie</label>
                    <%--@elvariable id="categories" type="java.util.Collection<com.cardhaven.cardhaven.model.dto.CategoryDTO>"--%>
                    <select id="categories" name="categories" multiple size="5">
                        <c:forEach var="category" items="${categories}">
                            <option value="${category.id}">${category.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group checkbox-group">
                    <input type="checkbox" id="isActive" name="isActive" value="true" checked>
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

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const productTypeSelect = document.getElementById('productType');
        const tradingCardFields = document.getElementById('tradingCardFields');
        const accessoryFields = document.getElementById('accessoryFields');

        function toggleProductFields() {
            const selectedType = productTypeSelect.value;
            // Nascondi tutte le sezioni dinamiche
            tradingCardFields.style.display = 'none';
            accessoryFields.style.display = 'none';

            // Mostra la sezione corretta
            if (selectedType === 'TradingCard') {
                tradingCardFields.style.display = 'block';
            } else if (selectedType === 'Accessory') {
                accessoryFields.style.display = 'block';
            }
        }

        // Esegui al cambio del select
        productTypeSelect.addEventListener('change', toggleProductFields);

        // Esegui al caricamento della pagina per impostare lo stato iniziale
        toggleProductFields();

        const productForm = document.getElementById('product-form');
        const dropZone = document.getElementById('image-drop-zone');
        const fileInput = document.getElementById('productImages');
        const previewContainer = document.getElementById('image-preview-container');
        const imageOrderInput = document.getElementById('imageOrder');

        let fileStore = new Map();
        let draggedItem = null;

        // Gestione Drop Zone
        dropZone.addEventListener('click', () => fileInput.click());
        fileInput.addEventListener('change', () => handleFiles(fileInput.files));
        dropZone.addEventListener('dragover', (e) => {
            e.preventDefault();
            dropZone.classList.add('dragover');
        });
        dropZone.addEventListener('dragleave', () => dropZone.classList.remove('dragover'));
        dropZone.addEventListener('drop', (e) => {
            e.preventDefault();
            dropZone.classList.remove('dragover');
            fileInput.files = e.dataTransfer.files;
            handleFiles(fileInput.files);
        });

        function handleFiles(files) {
            for (const file of files) {
                if (!file.type.startsWith('image/')) continue;
                if (!fileStore.has(file.name)) {
                    fileStore.set(file.name, file);
                }
            }
            renderPreviews();
        }

        async function renderPreviews() {
            previewContainer.innerHTML = '';
            const filesToRender = Array.from(fileStore.values()); // Lavora sempre con l'ordine attuale del map

            const previewPromises = filesToRender.map(file => {
                return new Promise((resolve, reject) => {
                    const reader = new FileReader();
                    reader.onload = e => resolve({src: e.target.result, name: file.name});
                    reader.onerror = e => reject(e);
                    reader.readAsDataURL(file);
                });
            });

            try {
                const loadedImages = await Promise.all(previewPromises);

                loadedImages.forEach((img, index) => {
                    const previewItem = createPreviewItem(img.src, img.name, index === 0);
                    previewContainer.appendChild(previewItem);
                });

                updateImageOrder();
            } catch (error) {
                console.error("Errore durante la lettura di un file:", error);
            }
        }


        function createPreviewItem(src, fileName, isCover) {
            const item = document.createElement('div');
            item.className = 'image-preview-item';
            item.draggable = true;
            item.dataset.fileName = fileName;

            item.innerHTML = `
                <img src="\${src}" alt="Anteprima di \${fileName}">
                <button type="button" class="remove-btn" title="Rimuovi immagine">&times;</button>
                ${isCover ? '<span class="cover-badge">Copertina</span>' : ''}
            `;

            item.querySelector('.remove-btn').addEventListener('click', (e) => {
                e.stopPropagation();
                fileStore.delete(fileName);
                renderPreviews();
            });

            return item;
        }

        function getOrderedFiles() {
            const orderedFileNames = (imageOrderInput.value || '').split(',').filter(name => name);
            if (orderedFileNames.length === 0) {
                return Array.from(fileStore.values());
            }
            return orderedFileNames.map(name => fileStore.get(name)).filter(file => file);
        }

        function updateImageOrder() {
            const items = previewContainer.querySelectorAll('.image-preview-item');
            const orderedFileNames = Array.from(items).map(item => item.dataset.fileName);
            imageOrderInput.value = orderedFileNames.join(',');

            // Aggiorna la copertina
            items.forEach((item, index) => {
                let badge = item.querySelector('.cover-badge');
                if (index === 0 && !badge) {
                    item.insertAdjacentHTML('beforeend', '<span class="cover-badge">Copertina</span>');
                } else if (index > 0 && badge) {
                    badge.remove();
                }
            });
        }

        // Drag & Drop per riordinare
        previewContainer.addEventListener('dragstart', (e) => {
            draggedItem = e.target.closest('.image-preview-item');
            if (draggedItem) {
                setTimeout(() => {
                    draggedItem.classList.add('is-dragging');
                }, 0);
            }
        });

        previewContainer.addEventListener('dragend', () => {
            if (draggedItem) {
                draggedItem.classList.remove('is-dragging');
                draggedItem = null;
                updateImageOrder();
            }
        });

        previewContainer.addEventListener('dragover', (e) => {
            e.preventDefault();
            const afterElement = getDragAfterElement(previewContainer, e.clientY);
            const currentDraggable = document.querySelector('.is-dragging');
            if (currentDraggable) {
                if (afterElement == null) {
                    previewContainer.appendChild(currentDraggable);
                } else {
                    previewContainer.insertBefore(currentDraggable, afterElement);
                }
            }
        });

        productForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const formData = new FormData(productForm);
            formData.delete('productImages');

            const orderedFiles = getOrderedFiles();
            orderedFiles.forEach((file, index) => {
                formData.append('productImages', file, file.name);
            });
            console.log(formData);

            fetch('${pageContext.request.contextPath}/admin/products/new', {
                method: "POST",
                body: formData
            }).then(response => {
                window.location.href = response.url;
            }).catch(error => {
                console.log("Errore nell'invio del form:",
                    error
                );
            });
        });

        function getDragAfterElement(container, y) {
            const draggableElements = [...container.querySelectorAll('.image-preview-item:not(.is-dragging)')];
            return draggableElements.reduce((closest, child) => {
                const box = child.getBoundingClientRect();
                const offset = y - box.top - box.height / 2;
                if (offset < 0 && offset > closest.offset) {
                    return {offset: offset, element: child};
                } else {
                    return closest;
                }
            }, {offset: Number.NEGATIVE_INFINITY}).element;
        }
    });
</script>

</body>
</html>

