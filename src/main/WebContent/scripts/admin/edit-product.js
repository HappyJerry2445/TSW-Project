document.addEventListener('DOMContentLoaded', function () {
    const productForm = document.getElementById('product-form');
    const dropZone = document.getElementById('image-drop-zone');
    const fileInput = document.getElementById('newProductImages');
    const previewContainer = document.getElementById('image-preview-container');
    const imageOrderInput = document.getElementById('imageOrder');
    const imagesToDeleteInput = document.getElementById('imagesToDelete');

    let newFileStore = new Map();
    let imagesToDelete = new Set();
    let draggedItem = null;

    function initialize() {
        // Aggiunge event listener agli elementi esistenti
        previewContainer.querySelectorAll('.image-preview-item').forEach(item => {
            addEventListenersToItem(item);
        });
        updateImageOrder();
    }

    function addEventListenersToItem(item) {
        // Gestione Drag & Drop
        item.addEventListener('dragstart', (e) => {
            draggedItem = item;
            setTimeout(() => item.classList.add('is-dragging'), 0);
        });
        item.addEventListener('dragend', () => {
            item.classList.remove('is-dragging');
            draggedItem = null;
            updateImageOrder();
        });

        // Gestione Rimozione
        item.querySelector('.remove-btn').addEventListener('click', (e) => {
            e.stopPropagation();
            const imageId = item.dataset.imageId;
            const fileName = item.dataset.fileName;

            if (imageId) { // È un'immagine esistente
                imagesToDelete.add(imageId);
                imagesToDeleteInput.value = Array.from(imagesToDelete).join(',');
            }
            if (fileName) { // È un'immagine nuova
                newFileStore.delete(fileName);
            }
            item.remove();
            updateImageOrder();
        });
    }

    // Gestione Drop Zone e Selezione File
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
            if (!file.type.startsWith('image/') || newFileStore.has(file.name)) continue;
            newFileStore.set(file.name, file);

            const reader = new FileReader();
            reader.onload = (e) => {
                const previewItem = createPreviewItem(e.target.result, file.name);
                previewContainer.appendChild(previewItem);
                addEventListenersToItem(previewItem);
                updateImageOrder();
            };
            reader.readAsDataURL(file);
        }
    }

    function createPreviewItem(src, fileName) {
        const item = document.createElement('div');
        item.className = 'image-preview-item';
        item.draggable = true;
        item.dataset.fileName = fileName; // Usato come identificatore per i nuovi file

        item.innerHTML = `
            <img src="${src}" alt="Anteprima di ${fileName}">
            <button type="button" class="remove-btn" title="Rimuovi immagine">&times;</button>
        `;
        return item;
    }

    function updateImageOrder() {
        const items = previewContainer.querySelectorAll('.image-preview-item');
        const orderedIdentifiers = Array.from(items).map(item => {
            return item.dataset.imageId ? `id:${item.dataset.imageId}` : `new:${item.dataset.fileName}`;
        });
        imageOrderInput.value = orderedIdentifiers.join(',');

        items.forEach((item, index) => {
            let badge = item.querySelector('.cover-badge');
            if (index === 0 && !badge) {
                item.insertAdjacentHTML('beforeend', '<span class="cover-badge">Copertina</span>');
            } else if (index > 0 && badge) {
                badge.remove();
            }
        });
    }

    // Gestione Drag & Drop sul contenitore
    previewContainer.addEventListener('dragover', (e) => {
        e.preventDefault();
        const afterElement = getDragAfterElement(previewContainer, e.clientY);
        if (draggedItem) {
            if (afterElement == null) {
                previewContainer.appendChild(draggedItem);
            } else {
                previewContainer.insertBefore(draggedItem, afterElement);
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

    initialize();
});
