function editProfile() {
    var editButton = document.getElementById("edit-button");
    var saveButton = document.getElementById("save-button");
    editButton.classList.add("d-none");
    saveButton.classList.remove("d-none");
    var inputs = document.querySelectorAll(".profile-details input");
    inputs.forEach(function (el) {
        el.removeAttribute("disabled");
    });
}
