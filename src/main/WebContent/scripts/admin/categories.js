document.addEventListener('DOMContentLoaded', function () {
    const categoryForm = document.getElementById('category-form');
    const formTitle = document.getElementById('form-title');
    const formActionInput = document.getElementById('form-action');
    const categoryIdInput = document.getElementById('categoryId');
    const categoryNameInput = document.getElementById('categoryName');
    const parentIdSelect = document.getElementById('parentId');
    const categoryTypeSelect = document.getElementById('categoryType');
    const descriptionTextarea = document.getElementById('description');
    const submitButton = document.getElementById('submit-button');
    const cancelButton = document.getElementById('cancel-button');
    const editButtons = document.querySelectorAll('.edit-btn');

    const resetForm = () => {
        formTitle.textContent = 'Aggiungi Nuova Categoria';
        formActionInput.value = 'create';
        submitButton.textContent = 'Crea Categoria';

        categoryForm.reset(); // Metodo più semplice per resettare il form
        categoryIdInput.value = '';

        cancelButton.style.display = 'none';
    };

    editButtons.forEach(button => {
        button.addEventListener('click', function (e) {
            e.preventDefault();

            const item = this.closest('.category-item');
            console.log(item.dataset.category);
            const data = JSON.parse(item.dataset.category.replace(/'/g, '"')); // Sostituisce apici singoli con doppi per JSON valido

            formTitle.textContent = `Modifica Categoria: ${data.name}`;
            formActionInput.value = 'update';
            submitButton.textContent = 'Salva Modifiche';

            categoryIdInput.value = data.id;
            categoryNameInput.value = data.name;
            parentIdSelect.value = data.parentId || '';
            categoryTypeSelect.value = data.type;
            descriptionTextarea.value = data.description || '';

            cancelButton.style.display = 'inline-block';

            categoryForm.scrollIntoView({behavior: 'smooth', block: 'start'});
        });
    });

    if (cancelButton) {
        cancelButton.addEventListener('click', function () {
            resetForm();
        });
    }
});
