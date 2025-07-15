document.addEventListener('DOMContentLoaded', function () {
    const statusSelects = document.querySelectorAll('.status-select');

    statusSelects.forEach(select => {
        select.addEventListener('change', function () {
            // Find the closest form and submit it
            const form = this.closest('.update-status-form');
            if (form) {
                form.submit();
            }
        });
    });
});

