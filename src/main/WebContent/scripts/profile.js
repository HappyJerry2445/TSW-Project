function editProfile() {
    let editButton = document.getElementById("edit-button");
    let saveButton = document.getElementById("save-button");
    editButton.classList.add("d-none");
    saveButton.classList.remove("d-none");
    let inputs = document.querySelectorAll(".profile-details input");
    inputs.forEach((el) => {
        el.removeAttribute("disabled");
    });
}

function undoEditProfile() {
    let editButton = document.getElementById("edit-button");
    let saveButton = document.getElementById("save-button");
    editButton.classList.remove("d-none");
    saveButton.classList.add("d-none");
    let inputs = document.querySelectorAll(".profile-details input");
    inputs.forEach((el) => {
        el.setAttribute("disabled", "");
    });
}

document.addEventListener("DOMContentLoaded", function () {
    const editForm = document.getElementById("editProfileForm");
    editForm.addEventListener("submit", function (event) {
        event.preventDefault();
        console.log("submit1234");
        const formData = new FormData(this);
        const button = this.querySelector('button[type="submit"]');
        const originalButtonHTML = button.innerHTML;

        button.disabled = true;
        button.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';

        fetch(this.action, {
            method: "POST",
            body: new URLSearchParams(formData)
        }).then((response) => {
            if (response.ok) {
                button.classList.add("btn-success");
                button.innerHTML = '<i class="fas fa-check"></i> Salvato';
                setTimeout(() => {
                    button.disabled = false;
                    button.innerHTML = originalButtonHTML;
                    button.classList.remove("btn-success");
                    undoEditProfile();
                }, 2000);
            } else {
                window.notify.error("Errore durante il salvataggio");
                button.disabled = false;
                button.innerHTML = originalButtonHTML;
                button.classList.remove("btn-success");
            }
        })

    })
})