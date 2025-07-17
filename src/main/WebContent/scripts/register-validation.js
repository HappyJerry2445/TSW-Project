document.addEventListener('DOMContentLoaded', function () {
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirm_password');
    const messageSpan = document.getElementById('password-match-message');

    // Ensure all elements exist before adding listeners
    if (!passwordInput || !confirmPasswordInput || !messageSpan) {
        return;
    }

    function validatePasswords() {
        if (confirmPasswordInput.value === '') {
            // If the confirm field is empty, don't show an error.
            // HTML5's `required` attribute will handle this.
            messageSpan.style.display = 'none';
            confirmPasswordInput.setCustomValidity('');
            return;
        }

        if (passwordInput.value !== confirmPasswordInput.value) {
            messageSpan.textContent = 'Le password non corrispondono.';
            messageSpan.style.display = 'block';
            // This is crucial for stopping form submission if passwords don't match
            confirmPasswordInput.setCustomValidity('Le password non corrispondono.');
        } else {
            messageSpan.textContent = '';
            messageSpan.style.display = 'none';
            confirmPasswordInput.setCustomValidity('');
        }
    }

    // Add event listeners to both password fields
    passwordInput.addEventListener('input', validatePasswords);
    confirmPasswordInput.addEventListener('input', validatePasswords);
    passwordInput.addEventListener('blur', validatePasswords);
    confirmPasswordInput.addEventListener('blur', validatePasswords);
});
