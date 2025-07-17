function validateFormElement(formElem) {
    let span = formElem.nextElementSibling;
    if (formElem.checkValidity()) {
        formElem.classList.remove("error");
        span.innerHTML = "";
        return true;
    }
    formElem.classList.add("error");
    span.style.color = "var(--color-status-error)";
    if (formElem.validity.valueMissing) {
        span.innerHTML = "Campo obbligatorio";
    } else {
        span.innerHTML = formElem.title;
    }
}