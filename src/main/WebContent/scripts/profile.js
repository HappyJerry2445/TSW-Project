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
