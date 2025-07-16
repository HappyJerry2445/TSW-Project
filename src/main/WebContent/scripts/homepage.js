document.addEventListener("DOMContentLoaded", function () {
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add("visible");
          observer.unobserve(entry.target);
        }
      });
    },
    {
      threshold: 0.1,
    },
  );

  document.querySelectorAll(".product-showcase").forEach((section) => {
    observer.observe(section);
  });

  document.querySelectorAll(".add-to-cart-form").forEach((form) => {
    form.addEventListener("submit", function (event) {
      event.preventDefault();

      const formData = new FormData(this);
      const button = this.querySelector('button[type="submit"]');
      const originalButtonHTML = button.innerHTML;

      button.disabled = true;
      button.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';

      fetch(this.action, {
        method: "POST",
        body: new URLSearchParams(formData),
      })
        .then((response) => {
          if (response.ok) {
            if (typeof window.updateCartCount === "function") {
              window.updateCartCount();
            }
            if (window.notify) {
              window.notify.success("Prodotto aggiunto al carrello!");
            }
            button.innerHTML = '<i class="fas fa-check"></i> Aggiunto!';
            setTimeout(() => {
              button.disabled = false;
              button.innerHTML = originalButtonHTML;
            }, 2000);
          } else {
            return response.json().then((errorData) => {
              throw new Error(errorData.message || "Errore server");
            });
          }
        })
        .catch((error) => {
          console.error("Errore:", error);
          if (window.notify) {
            window.notify.error(error.message || "Errore di connessione.");
          }
          button.disabled = false;
          button.innerHTML = originalButtonHTML;
        });
    });
  });
});
