// Rende la funzione di aggiornamento del carrello globalmente accessibile
window.updateCartCount = async function () {
  const cartCountSpan = document.querySelector(".cart-count");
  if (!cartCountSpan) return; // Esce se l'elemento non esiste

  try {
    // La variabile APP_CONTEXT_PATH è definita in header.jsp
    const response = await fetch(`${APP_CONTEXT_PATH}/cart/count`);
    if (!response.ok) {
      throw new Error(`Errore HTTP! Stato: ${response.status}`);
    }

    const data = await response.json();

    if (data.count !== undefined) {
      cartCountSpan.textContent = data.count;
      // Mostra o nasconde il badge in base al conteggio
      if (data.count > 0) {
        cartCountSpan.classList.remove("hidden");
      } else {
        cartCountSpan.classList.add("hidden");
      }
    } else if (data.error) {
      console.error(
        "Errore nel recupero del conteggio del carrello:",
        data.error,
      );
    }
  } catch (error) {
    console.error("Impossibile aggiornare il contatore del carrello:", error);
  }
};

// Esegue l'aggiornamento iniziale al caricamento della pagina
document.addEventListener("DOMContentLoaded", () => {
  window.updateCartCount();
});
