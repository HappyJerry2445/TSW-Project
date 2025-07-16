document.addEventListener("DOMContentLoaded", function () {
  // Gestisce l'animazione di fade-in per le sezioni della homepage quando entrano nel viewport.
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          // Applica l'animazione a ogni card all'interno della sezione visibile
          entry.target
            .querySelectorAll(".product-card")
            .forEach((card, index) => {
              // Applica un ritardo crescente per un effetto "stagger"
              card.style.animationDelay = `${index * 100}ms`;
              card.classList.add("fade-in-up");
            });
          observer.unobserve(entry.target); // Ferma l'osservazione dopo la prima animazione
        }
      });
    },
    {
      threshold: 0.1, // L'animazione si attiva quando il 10% dell'elemento è visibile
    },
  );

  // Applica l'observer a tutte le sezioni di vetrina dei prodotti.
  document.querySelectorAll(".product-showcase").forEach((section) => {
    observer.observe(section);
  });
});
