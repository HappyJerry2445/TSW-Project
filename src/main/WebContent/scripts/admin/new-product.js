document.addEventListener('DOMContentLoaded', function () {
    const productTypeSelect = document.getElementById('productType');
    const tradingCardFields = document.getElementById('tradingCardFields');
    const accessoryFields = document.getElementById('accessoryFields');
    const allDynamicInputs = document.querySelectorAll('.dynamic-section input, .dynamic-section select');

    function toggleProductFields() {
        const selectedType = productTypeSelect.value;

        // Disabilita tutti gli input dinamici per non inviarli se non pertinenti
        allDynamicInputs.forEach(input => input.disabled = true);

        // Nascondi tutte le sezioni
        tradingCardFields.style.display = 'none';
        accessoryFields.style.display = 'none';

        // Mostra e abilita la sezione corretta
        let sectionToShow;
        if (selectedType === 'TradingCard') {
            sectionToShow = tradingCardFields;
        } else if (selectedType === 'Accessory') {
            sectionToShow = accessoryFields;
        }

        if (sectionToShow) {
            sectionToShow.style.display = 'block';
            sectionToShow.querySelectorAll('input, select').forEach(input => input.disabled = false);
        }
    }

    productTypeSelect.addEventListener('change', toggleProductFields);
    toggleProductFields(); // Esegui al caricamento

    // Logica per l'upload e anteprima delle immagini
    const dropZone = document.getElementById('image-drop-zone');
    const fileInput = document.getElementById('productImages');
    const previewContainer = document.getElementById('image-preview-container');
    const imageOrderInput = document.getElementById('imageOrder');

    let fileStore = new Map();
    let draggedItem = null;

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
        handleFiles(e.dataTransfer.files);
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
        const filesToRender = Array.from(fileStore.values());

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
            <img src="${src}" alt="Anteprima di ${fileName}">
            <button type="button" class="remove-btn" title="Rimuovi immagine">&times;</button>
            ${isCover ? '<span class="cover-badge">Copertina</span>' : ''}
        `;

        item.querySelector('.remove-btn').addEventListener('click', (e) => {
            e.stopPropagation();
            fileStore.delete(fileName);

            // Aggiorna l'input file "invisibile" per riflettere la rimozione
            const dt = new DataTransfer();
            fileStore.forEach(file => dt.items.add(file));
            fileInput.files = dt.files;

            renderPreviews();
        });

        return item;
    }

    function updateImageOrder() {
        const items = previewContainer.querySelectorAll('.image-preview-item');
        const orderedFileNames = Array.from(items).map(item => item.dataset.fileName);
        imageOrderInput.value = orderedFileNames.join(',');

        items.forEach((item, index) => {
            let badge = item.querySelector('.cover-badge');
            if (index === 0 && !badge) {
                item.insertAdjacentHTML('beforeend', '<span class="cover-badge">Copertina</span>');
            } else if (index > 0 && badge) {
                badge.remove();
            }
        });
    }

    // Drag & Drop
    previewContainer.addEventListener('dragstart', (e) => {
        if (e.target.classList.contains('image-preview-item')) {
            draggedItem = e.target;
            setTimeout(() => draggedItem.classList.add('is-dragging'), 0);
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
