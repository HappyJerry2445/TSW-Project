document.addEventListener("DOMContentLoaded", function () {
  const addToCartForms = document.querySelectorAll(".add-to-cart-form");

  addToCartForms.forEach((form) => {
    form.addEventListener("submit", function (event) {
      event.preventDefault(); // Impedisce l'invio sincrono del form

      const formData = new FormData(this);
      const button = this.querySelector('button[type="submit"]');
      const originalButtonHTML = button.innerHTML;

      // Disabilita il pulsante e mostra uno stato di caricamento
      button.disabled = true;
      button.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';

      fetch(this.action, {
        method: "POST",
        body: new URLSearchParams(formData),
      })
        .then((response) => {
          if (response.ok) {
            // Se la risposta è positiva, aggiorna l'interfaccia
            window.updateCartCount(); // Aggiorna il contatore nel header
            if (window.notify && typeof window.notify.success === "function") {
              window.notify.success("Prodotto aggiunto al carrello!");
            }

            // Fornisce un feedback visivo sul pulsante
            button.classList.add("btn-success"); // Aggiungi una classe per lo stile di successo
            button.innerHTML = '<i class="fas fa-check"></i> Aggiunto';

            // Ripristina il pulsante allo stato originale dopo un breve ritardo
            setTimeout(() => {
              button.disabled = false;
              button.innerHTML = originalButtonHTML;
              button.classList.remove("btn-success");
            }, 2000);
          } else {
            // Se il server risponde con un errore, lancia un errore per il blocco .catch
            return response.json().then((errorData) => {
              throw new Error(
                errorData.message || "Errore del server sconosciuto.",
              );
            });
          }
        })
        .catch((error) => {
          // Gestisce errori di rete o errori lanciati dal server
          console.error("Errore durante l'aggiunta al carrello:", error);
          if (window.notify && typeof window.notify.error === "function") {
            window.notify.error(
              error.message || "Impossibile aggiungere il prodotto.",
            );
          }
          // Ripristina immediatamente il pulsante in caso di errore
          button.disabled = false;
          button.innerHTML = originalButtonHTML;
        });
    });
  });

  // Aggiungi uno stile CSS per lo stato di successo del pulsante, se non già presente
  const style = document.createElement("style");
  style.innerHTML = `
        .btn-success {
            background-color: var(--color-status-success) !important;
            border-color: var(--color-status-success) !important;
            cursor: default !important;
        }
    `;
  document.head.appendChild(style);
});
