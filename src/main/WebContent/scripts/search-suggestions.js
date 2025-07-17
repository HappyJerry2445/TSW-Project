document.addEventListener("DOMContentLoaded", () => {
  const searchForm = document.getElementById("search-form");
  const searchInput = document.getElementById("search-input");
  const suggestionsContainer = document.getElementById("suggestions-container");
  const searchSpinner = document.getElementById("search-spinner");

  if (!searchForm || !searchInput || !suggestionsContainer) {
    console.error("Elementi per la ricerca non trovati.");
    return;
  }

  let highlightedIndex = -1;

  /**
   * Esegue il debounce di una funzione, ritardandone l'esecuzione
   * finché non è trascorso un certo tempo senza che venga richiamata.
   * @param {Function} func La funzione da eseguire con debounce.
   * @param {number} delay Il ritardo in millisecondi.
   * @returns {Function} La nuova funzione con debounce.
   */
  const debounce = (func, delay) => {
    let timeoutId;
    return (...args) => {
      clearTimeout(timeoutId);
      timeoutId = setTimeout(() => {
        func.apply(this, args);
      }, delay);
    };
  };

  /**
   * Recupera i suggerimenti dal server.
   * @param {string} query Il termine di ricerca.
   */
  const fetchSuggestions = async (query) => {
    if (query.length < 3) {
      hideSuggestions();
      return;
    }

    searchSpinner.classList.add("visible");

    try {
      const response = await fetch(
        `${APP_CONTEXT_PATH}/products/suggest?query=${encodeURIComponent(query)}`,
      );
      if (!response.ok) {
        throw new Error(`Errore HTTP: ${response.status}`);
      }
      const suggestions = await response.json();
      renderSuggestions(suggestions, query);
    } catch (error) {
      console.error("Errore nel recupero dei suggerimenti:", error);
      hideSuggestions();
    } finally {
      searchSpinner.classList.remove("visible");
    }
  };

  /**
   * Mostra i suggerimenti nel contenitore.
   * @param {object[]} suggestions La lista di oggetti suggerimento (con id e nome).
   * @param {object[]} suggestions La lista di oggetti suggerimento (con id e nome).
   * @param {string} query La query originale per l'highlighting.
   */
  const renderSuggestions = (suggestions, query) => {
    suggestionsContainer.innerHTML = "";
    if (suggestions.length === 0) {
      hideSuggestions();
      return;
    }

    const fragment = document.createDocumentFragment();
    const escapedQuery = query.replace(/[-\/\\^$*+?.()|[\]{}]/g, "\\$&");
    const regex = new RegExp(`(${escapedQuery})`, "gi");

    suggestions.forEach((suggestionObj, index) => {
      const item = document.createElement("div");
      item.className = "suggestion-item";
      item.dataset.index = index;
      item.dataset.id = suggestionObj.id; // Salva l'ID del prodotto

      // Evidenzia la parte della stringa che corrisponde alla query
      item.innerHTML = suggestionObj.name.replace(regex, "<strong>$1</strong>");

      item.addEventListener("click", () => {
        // Reindirizza direttamente alla pagina del prodotto
        window.location.href = `${APP_CONTEXT_PATH}/products/detail/${suggestionObj.id}`;
      });
      fragment.appendChild(item);
    });

    suggestionsContainer.appendChild(fragment);
    showSuggestions();
  };

  const showSuggestions = () => {
    suggestionsContainer.classList.add("visible");
    highlightedIndex = -1;
  };

  const hideSuggestions = () => {
    suggestionsContainer.classList.remove("visible");
    suggestionsContainer.innerHTML = "";
  };

  /**
   * Gestisce la navigazione da tastiera tra i suggerimenti.
   * @param {KeyboardEvent} e L'evento keydown.
   */
  const handleKeyDown = (e) => {
    const items = suggestionsContainer.querySelectorAll(".suggestion-item");
    if (!items.length) return;

    switch (e.key) {
      case "ArrowDown":
        e.preventDefault(); // Evita che il cursore si sposti
        highlightedIndex = (highlightedIndex + 1) % items.length;
        updateHighlight(items);
        break;
      case "ArrowUp":
        e.preventDefault();
        highlightedIndex = (highlightedIndex - 1 + items.length) % items.length;
        updateHighlight(items);
        break;
      case "Enter":
        if (highlightedIndex > -1) {
          e.preventDefault();
          items[highlightedIndex].click();
        }
        break;
      case "Escape":
        hideSuggestions();
        break;
    }
  };

  const updateHighlight = (items) => {
    items.forEach((item, index) => {
      item.classList.toggle("highlighted", index === highlightedIndex);
    });
  };

  // --- Event Listeners ---
  const debouncedFetchSuggestions = debounce(fetchSuggestions, 500);

  searchInput.addEventListener("input", () => {
    debouncedFetchSuggestions(searchInput.value);
  });

  searchInput.addEventListener("keydown", handleKeyDown);

  // Chiudi i suggerimenti se si clicca fuori
  document.addEventListener("click", (e) => {
    if (!searchForm.contains(e.target)) {
      hideSuggestions();
    }
  });

  // Nascondi i suggerimenti quando il form viene inviato
  searchForm.addEventListener("submit", hideSuggestions);
});
