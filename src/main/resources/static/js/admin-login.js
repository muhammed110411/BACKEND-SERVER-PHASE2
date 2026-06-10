(function () {
    var form = document.querySelector("[data-admin-login-form]");

    if (!form) {
        return;
    }

    var usernameInput = form.querySelector("[data-username-input]");
    var passwordInput = form.querySelector("[data-password-input]");
    var submitButton = form.querySelector("[data-submit-button]");
    var submitLabel = form.querySelector("[data-submit-label]");
    var formError = document.getElementById("form-error");
    var usernameError = form.querySelector("[data-username-error]");
    var passwordError = form.querySelector("[data-password-error]");

    function setFieldError(input, messageElement, hasError) {
        var fieldGroup = input.closest(".field-group");

        fieldGroup.classList.toggle("has-error", hasError);
        input.setAttribute("aria-invalid", hasError ? "true" : "false");
        messageElement.hidden = !hasError;
    }

    function clearClientError() {
        if (formError) {
            formError.hidden = true;
        }
    }

    function validateForm() {
        var usernameMissing = usernameInput.value.trim().length === 0;
        var passwordMissing = passwordInput.value.length === 0;

        setFieldError(usernameInput, usernameError, usernameMissing);
        setFieldError(passwordInput, passwordError, passwordMissing);

        if (formError) {
            formError.hidden = !(usernameMissing || passwordMissing);
        }

        return !(usernameMissing || passwordMissing);
    }

    usernameInput.addEventListener("input", function () {
        if (usernameInput.value.trim().length > 0) {
            setFieldError(usernameInput, usernameError, false);
        }
        clearClientError();
    });

    passwordInput.addEventListener("input", function () {
        if (passwordInput.value.length > 0) {
            setFieldError(passwordInput, passwordError, false);
        }
        clearClientError();
    });

    form.addEventListener("submit", function (event) {
        if (submitButton.disabled) {
            event.preventDefault();
            return;
        }

        if (!validateForm()) {
            event.preventDefault();
            return;
        }

        submitButton.disabled = true;
        submitButton.classList.add("is-loading");
        submitButton.setAttribute("aria-busy", "true");
        submitLabel.textContent = "Signing In";
    });
}());
