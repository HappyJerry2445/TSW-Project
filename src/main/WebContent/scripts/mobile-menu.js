document.addEventListener('DOMContentLoaded', () => {
    const menuToggleBtn = document.querySelector('.mobile-menu-toggle');
    const mainNav = document.querySelector('.main-nav');
    const navOverlay = document.querySelector('.nav-overlay');
    const body = document.body;

    if (!menuToggleBtn || !mainNav || !navOverlay) {
        console.error('Mobile menu elements not found. Please check your HTML structure.');
        return;
    }

    const toggleIcon = menuToggleBtn.querySelector('i');

    const toggleMenu = () => {
        const isOpen = mainNav.classList.toggle('is-open');

        // Toggle overlay visibility
        navOverlay.classList.toggle('is-visible', isOpen);

        // Toggle body scrolling
        body.classList.toggle('no-scroll', isOpen);

        // Change icon
        if (isOpen) {
            toggleIcon.classList.remove('fa-bars');
            toggleIcon.classList.add('fa-times');
            menuToggleBtn.setAttribute('aria-label', 'Chiudi menu');
        } else {
            toggleIcon.classList.remove('fa-times');
            toggleIcon.classList.add('fa-bars');
            menuToggleBtn.setAttribute('aria-label', 'Apri menu');
        }
    };

    // Event listener for the toggle button
    menuToggleBtn.addEventListener('click', toggleMenu);

    // Event listener for the overlay (to close the menu when clicking outside)
    navOverlay.addEventListener('click', toggleMenu);
});
