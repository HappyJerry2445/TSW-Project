document.addEventListener('DOMContentLoaded', function () {
    const cartCountSpan = document.querySelector('.cart-count');

    async function updateCartCount() {
        try {
            const response = await fetch(`${APP_CONTEXT_PATH}/cart/count`);

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();

            if (data.count !== undefined) {
                cartCountSpan.textContent = data.count;
                cartCountSpan.classList.remove("hidden");
            } else if (data.error) {
                console.error('Errore nel recupero del conteggio del carrello:', data.error);
            }

        } catch (error) {
            console.error('Errore di rete o del server durante l\'aggiornamento del carrello:', error);
        }
    }

    updateCartCount();

});
