document.addEventListener('DOMContentLoaded', () => {
    // Seleziona tutti i link dentro gli `li` che hanno la classe `has-submenu`
    const submenuToggles = document.querySelectorAll('.main-nav .has-submenu > a');

    submenuToggles.forEach(toggle => {
        toggle.addEventListener('click', (event) => {
            // Applica la logica solo se il menu mobile è visibile
            const mobileMenuToggle = document.querySelector('.mobile-menu-toggle');
            if (window.getComputedStyle(mobileMenuToggle).display !== 'none') {

                // Previene la navigazione immediata per permettere l'apertura/chiusura del sottomenu
                event.preventDefault();

                const parentLi = toggle.parentElement;

                // Aggiunge o rimuove la classe 'open' per mostrare/nascondere il sottomenu
                parentLi.classList.toggle('open');
            }
        });
    });
});
