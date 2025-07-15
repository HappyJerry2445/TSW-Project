document.addEventListener('DOMContentLoaded', function () {
    const statusSelects = document.querySelectorAll('.status-select');
    const viewReviewButtons = document.querySelectorAll('.view-review-btn');
    const reviewDetailModal = document.getElementById('reviewDetailModal');
    const modalReviewText = document.getElementById('modalReviewText');
    const closeButton = document.querySelector('#reviewDetailModal .close-button');

    statusSelects.forEach(select => {
        select.addEventListener('change', function () {
            // Trova il form più vicino e invialo
            const form = this.closest('.update-status-form');
            if (form) {
                form.submit();
            }
        });
    });

    viewReviewButtons.forEach(button => {
        button.addEventListener('click', function () {
            const reviewText = this.dataset.reviewText;
            modalReviewText.textContent = reviewText;
            reviewDetailModal.style.display = 'flex'; // Use flex to center
        });
    });

    closeButton.addEventListener('click', function () {
        reviewDetailModal.style.display = 'none';
    });

    // Close the modal if clicking outside the modal content
    window.addEventListener('click', function (event) {
        if (event.target === reviewDetailModal) {
            reviewDetailModal.style.display = 'none';
        }
    });
});
