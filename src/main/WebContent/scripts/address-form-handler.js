document.addEventListener('DOMContentLoaded', function () {
    const sameAsShippingCheckbox = document.getElementById('same-as-shipping');
    const shippingSelect = document.getElementById('shipping-address');
    const billingSelect = document.getElementById('billing-address');
    const billingContainer = document.getElementById('billing-address-container');

    // Assicurati che gli elementi esistano prima di aggiungere event listeners
    // per evitare errori in altre pagine.
    if (!sameAsShippingCheckbox || !shippingSelect || !billingSelect || !billingContainer) {
        return;
    }

    function syncBillingAddress() {
        if (sameAsShippingCheckbox.checked) {
            // Imposta l'indirizzo di fatturazione uguale a quello di spedizione
            billingSelect.value = shippingSelect.value;
            // Disabilita il menu a tendina e nascondilo per una UI più pulita
            billingSelect.disabled = true;
            billingContainer.style.display = 'none';
        } else {
            // Riabilita il menu a tendina e mostralo
            billingSelect.disabled = false;
            billingContainer.style.display = 'block';
        }
    }

    // Esegui la funzione al cambio della checkbox
    sameAsShippingCheckbox.addEventListener('change', syncBillingAddress);

    // Esegui la funzione anche al cambio dell'indirizzo di spedizione (se la checkbox è attiva)
    shippingSelect.addEventListener('change', syncBillingAddress);

    // Esegui la funzione al caricamento della pagina per impostare lo stato iniziale
    syncBillingAddress();
});