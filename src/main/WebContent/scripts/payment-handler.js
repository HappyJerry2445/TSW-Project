document.addEventListener('DOMContentLoaded', function() {
    PaymentHandler.init();
});

const PaymentHandler = {
    init: function() {
        this.bindEvents();
        this.setupCardNumberFormatting();
        this.setupExpiryDateFormatting();
        this.setupCVVValidation();
        this.setupCardholderNameValidation();
        this.addFieldErrorStyles();
    },

    bindEvents: function() {
        document.querySelectorAll('input[name="payment-method"]').forEach(radio => {
            radio.addEventListener('change', this.handlePaymentMethodChange.bind(this));
        });

        document.getElementById('checkout-form').addEventListener('submit', this.handleFormSubmit.bind(this));

        document.getElementById('card-number').addEventListener('input', this.validateCardNumber.bind(this));
        document.getElementById('expiry-date').addEventListener('input', this.validateExpiryDate.bind(this));
        document.getElementById('cvv').addEventListener('input', this.validateCVV.bind(this));
        document.getElementById('cardholder-name').addEventListener('input', this.validateCardholderName.bind(this));
    },

    setupCardholderNameValidation: function() {
        const cardholderNameField = document.getElementById('cardholder-name');
        cardholderNameField.addEventListener('keypress', function(e) {
            const charCode = e.charCode;
            // Permette solo lettere (maiuscole e minuscole), spazi, apostrofi e trattini
            if (!(charCode >= 65 && charCode <= 90) && // A-Z
                !(charCode >= 97 && charCode <= 122) && // a-z
                charCode !== 32 && // spazio
                charCode !== 39 && // apostrofo
                charCode !== 45 && // trattino
                !(charCode >= 192 && charCode <= 255)) { // caratteri accentati
                e.preventDefault();
            }
        });

        cardholderNameField.addEventListener('paste', function(e) {
            e.preventDefault();
            const pasteData = e.clipboardData.getData('text')
                .replace(/[^a-zA-ZàèéìòùÀÈÉÌÒÙ\s'-]/g, '');
            document.execCommand('insertText', false, pasteData);
        });
    },

    setupCardNumberFormatting: function() {
        const cardNumberField = document.getElementById('card-number');
        cardNumberField.addEventListener('keypress', function(e) {
            const charCode = e.charCode;
            if (charCode < 48 || charCode > 57) { // Permette solo numeri (0-9)
                e.preventDefault();
            }
        });

        cardNumberField.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\s/g, '');
            if (value.length > 16) {
                value = value.substring(0, 16);
            }
            let formattedValue = value.match(/.{1,4}/g)?.join(' ') || value;
            e.target.value = formattedValue;
        });

        cardNumberField.addEventListener('paste', function(e) {
            e.preventDefault();
            const pasteData = e.clipboardData.getData('text').replace(/\D/g, '').substring(0, 16);
            document.execCommand('insertText', false, pasteData);
        });
    },

    setupExpiryDateFormatting: function() {
        const expiryDateField = document.getElementById('expiry-date');
        expiryDateField.addEventListener('keypress', function(e) {
            const charCode = e.charCode;
            if ((charCode < 48 || charCode > 57) && charCode !== 47) { // Permette solo numeri e /
                e.preventDefault();
            }
        });

        expiryDateField.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length > 2) {
                value = value.substring(0, 2) + '/' + value.substring(2, 4);
            }
            if (value.length > 5) {
                value = value.substring(0, 5);
            }
            e.target.value = value;
        });

        expiryDateField.addEventListener('paste', function(e) {
            e.preventDefault();
            const pasteData = e.clipboardData.getData('text').replace(/\D/g, '').substring(0, 4);
            if (pasteData.length > 2) {
                document.execCommand('insertText', false, pasteData.substring(0, 2) + '/' + pasteData.substring(2, 4));
            } else {
                document.execCommand('insertText', false, pasteData);
            }
        });
    },

    setupCVVValidation: function() {
        const cvvField = document.getElementById('cvv');
        cvvField.setAttribute('maxlength', '3');

        cvvField.addEventListener('keypress', function(e) {
            const charCode = e.charCode;
            if (charCode < 48 || charCode > 57) { // Permette solo numeri
                e.preventDefault();
            }
        });

        cvvField.addEventListener('input', function(e) {
            e.target.value = e.target.value.replace(/\D/g, '').substring(0, 3);
        });

        cvvField.addEventListener('paste', function(e) {
            e.preventDefault();
            const pasteData = e.clipboardData.getData('text').replace(/\D/g, '').substring(0, 3);
            document.execCommand('insertText', false, pasteData);
        });
    },

    addFieldErrorStyles: function() {
        const style = document.createElement('style');
        style.textContent = `
            .is-invalid {
                border-color: var(--color-status-error) !important;
            }
            .field-error {
                color: var(--color-status-error);
                font-size: 0.75rem;
                margin-top: 0.25rem;
                display: block;
            }
        `;
        document.head.appendChild(style);
    },

    handlePaymentMethodChange: function(event) {
        const selectedMethod = event.target.value;

        document.querySelectorAll('.payment-form').forEach(form => {
            form.classList.remove('active');
        });

        document.getElementById(selectedMethod + '-form').classList.add('active');
        document.getElementById('selected-payment-method').value = selectedMethod;
        this.clearFieldErrors();
    },

    handleFormSubmit: function(event) {
        event.preventDefault();

        const selectedMethod = document.querySelector('input[name="payment-method"]:checked').value;

        if (!this.validatePaymentMethod(selectedMethod)) {
            window.notify.error('Correggi gli errori nel modulo di pagamento');
            return false;
        }

        const paymentData = this.collectPaymentData(selectedMethod);
        document.getElementById('payment-data').value = JSON.stringify(paymentData);

        // Simulazione di invio pagamento
        window.notify.success('Pagamento in elaborazione...');
        setTimeout(() => {
            event.target.submit();
        }, 1500);
    },

    validatePaymentMethod: function(method) {
        switch(method) {
            case 'credit-card':
                return this.validateCreditCard();
            case 'cash-on-delivery':
                return this.validateCashOnDelivery();
            default:
                return false;
        }
    },

    collectPaymentData: function(method) {
        const data = {
            method: method,
            timestamp: new Date().toISOString()
        };

        switch(method) {
            case 'credit-card':
                data.cardNumber = this.maskCardNumber(document.getElementById('card-number').value);
                data.expiryDate = document.getElementById('expiry-date').value;
                data.cardholderName = document.getElementById('cardholder-name').value;
                break;
            case 'cash-on-delivery':
                data.notes = 'Pagamento in contrassegno alla consegna';
                break;
        }

        return data;
    },

    validateCreditCard: function() {
        const cardNumber = document.getElementById('card-number').value;
        const expiryDate = document.getElementById('expiry-date').value;
        const cvv = document.getElementById('cvv').value;
        const cardholderName = document.getElementById('cardholder-name').value;

        let isValid = true;

        if (!this.isValidCardNumber(cardNumber)) {
            this.showFieldError('card-number', 'Inserisci un numero di carta valido (13-16 cifre)');
            isValid = false;
        }

        if (!this.isValidExpiryDate(expiryDate)) {
            this.showFieldError('expiry-date', 'Inserisci una data di scadenza valida (MM/AA)');
            isValid = false;
        }

        if (!this.isValidCVV(cvv)) {
            this.showFieldError('cvv', 'Il CVV deve contenere esattamente 3 cifre');
            isValid = false;
        }

        if (!cardholderName.trim() || !this.isValidCardholderName(cardholderName)) {
            this.showFieldError('cardholder-name', 'Inserisci il nome del titolare come sulla carta');
            isValid = false;
        }

        return isValid;
    },

    isValidCardholderName: function(name) {
        return /^[a-zA-ZàèéìòùÀÈÉÌÒÙ\s'-]+$/.test(name);
    },

    validateCashOnDelivery: function() {
        return true;
    },

    isValidCardNumber: function(cardNumber) {
        const cleaned = cardNumber.replace(/\s/g, '');
        return cleaned.length >= 13 && cleaned.length <= 16 && /^\d+$/.test(cleaned);
    },

    isValidExpiryDate: function(expiryDate) {
        const match = expiryDate.match(/^(\d{2})\/(\d{2})$/);
        if (!match) return false;

        const month = parseInt(match[1]);
        const year = parseInt(match[2]) + 2000;
        const now = new Date();
        const currentYear = now.getFullYear();
        const currentMonth = now.getMonth() + 1;

        return month >= 1 && month <= 12 &&
            (year > currentYear || (year === currentYear && month >= currentMonth));
    },

    isValidCVV: function(cvv) {
        return /^\d{3}$/.test(cvv);
    },

    validateCardNumber: function(e) {
        const isValid = this.isValidCardNumber(e.target.value);
        this.toggleFieldValidation(e.target, isValid);
        if (isValid) {
            this.clearFieldError(e.target.id);
        }
    },

    validateExpiryDate: function(e) {
        const isValid = this.isValidExpiryDate(e.target.value);
        this.toggleFieldValidation(e.target, isValid);
        if (isValid) {
            this.clearFieldError(e.target.id);
        }
    },

    validateCVV: function(e) {
        const isValid = this.isValidCVV(e.target.value);
        this.toggleFieldValidation(e.target, isValid);
        if (isValid) {
            this.clearFieldError(e.target.id);
        }
    },

    validateCardholderName: function(e) {
        const isValid = e.target.value.trim().length > 0 && this.isValidCardholderName(e.target.value);
        this.toggleFieldValidation(e.target, isValid);
        if (isValid) {
            this.clearFieldError(e.target.id);
        }
    },

    toggleFieldValidation: function(field, isValid) {
        field.classList.toggle('is-valid', isValid);
        field.classList.toggle('is-invalid', !isValid);
    },

    showFieldError: function(fieldId, message) {
        const field = document.getElementById(fieldId);
        const errorElement = document.getElementById(fieldId + '-error');

        if (!errorElement) {
            const newErrorElement = document.createElement('span');
            newErrorElement.className = 'field-error';
            newErrorElement.id = fieldId + '-error';
            newErrorElement.textContent = message;
            field.insertAdjacentElement('afterend', newErrorElement);
        } else {
            errorElement.textContent = message;
        }

        field.classList.add('is-invalid');
    },

    clearFieldError: function(fieldId) {
        const field = document.getElementById(fieldId);
        const errorElement = document.getElementById(fieldId + '-error');

        if (errorElement) {
            errorElement.remove();
        }

        field.classList.remove('is-invalid');
    },

    clearFieldErrors: function() {
        document.querySelectorAll('.field-error').forEach(error => error.remove());
        document.querySelectorAll('.is-invalid').forEach(field => field.classList.remove('is-invalid'));
    },

    maskCardNumber: function(cardNumber) {
        const cleaned = cardNumber.replace(/\s/g, '');
        return '**** **** **** ' + cleaned.slice(-4);
    }
};