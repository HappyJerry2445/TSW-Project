document.addEventListener('DOMContentLoaded', function () {
    const passwordInput = document.getElementById('newPassword');
    const confirmPasswordInput = document.getElementById('confirmNewPassword');
    const messageSpan = document.getElementById('password-match-message');

    function validatePasswords() {
        if (passwordInput.value !== confirmPasswordInput.value && confirmPasswordInput.value.length > 0) {
            messageSpan.textContent = 'Le password non corrispondono.';
            messageSpan.style.display = 'block';
            confirmPasswordInput.setCustomValidity("Le password non corrispondono.");
        } else {
            messageSpan.textContent = '';
            messageSpan.style.display = 'none';
            confirmPasswordInput.setCustomValidity('');
        }
    }

    if (passwordInput && confirmPasswordInput) {
        passwordInput.addEventListener('input', validatePasswords);
        confirmPasswordInput.addEventListener('input', validatePasswords);
    }
});
