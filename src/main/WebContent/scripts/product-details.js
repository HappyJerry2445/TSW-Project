document.addEventListener('DOMContentLoaded', function () {
    // --- Image Gallery Logic ---
    const mainImage = document.getElementById('main-product-image');
    const thumbnails = document.querySelectorAll('.thumbnail-item');

    if (mainImage && thumbnails.length > 0) {
        // Set the first thumbnail as active by default
        thumbnails[0].classList.add('active');

        thumbnails.forEach(thumb => {
            thumb.addEventListener('click', function (event) {
                // Prevent default behavior if thumbnails are wrapped in links
                event.preventDefault();

                // Get the URL of the full-size image from the data attribute
                const fullImageSrc = this.dataset.fullImage;
                if (fullImageSrc && mainImage.src !== fullImageSrc) {
                    // Fade out the old image
                    mainImage.style.opacity = '0';

                    setTimeout(() => {
                        // Change the source and fade it back in
                        mainImage.src = fullImageSrc;
                        mainImage.style.opacity = '1';
                    }, 200); // Match this duration with CSS transition
                }

                // Update the active state for thumbnails
                thumbnails.forEach(t => t.classList.remove('active'));
                this.classList.add('active');
            });
        });

        // Add a simple transition to the main image for the fade effect
        mainImage.style.transition = 'opacity 0.2s ease-in-out';
    }

    // --- Tab Navigation Logic ---
    const tabLinks = document.querySelectorAll('.tab-link');
    const tabPanes = document.querySelectorAll('.tab-pane');

    if (tabLinks.length > 0 && tabPanes.length > 0) {
        tabLinks.forEach(link => {
            link.addEventListener('click', function (event) {
                event.preventDefault();

                // Get the target tab pane's ID from the href (e.g., "#tab-description")
                const targetId = this.getAttribute('href');
                const targetPane = document.querySelector(targetId);

                if (targetPane) {
                    // Deactivate all tab links and panes first
                    tabLinks.forEach(l => l.classList.remove('active'));
                    tabPanes.forEach(p => p.classList.remove('active'));

                    // Activate the clicked link and its corresponding content pane
                    this.classList.add('active');
                    targetPane.classList.add('active');
                }
            });
        });
    }
});
