document.addEventListener('DOMContentLoaded', function () {
    const roleSelects = document.querySelectorAll('.role-select');

    roleSelects.forEach(select => {
        select.addEventListener('change', function () {
            // Trova il form più vicino e invialo
            const form = this.closest('.update-role-form');
            if (form) {
                form.submit();
            }
        });
    });
});
