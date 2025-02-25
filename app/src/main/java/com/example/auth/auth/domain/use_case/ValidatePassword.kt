package com.example.auth.auth.domain.use_case

class ValidatePassword{

    fun execute(password: String): ValidationResult {

        if (password.length < 8) {
            return ValidationResult(
                successful = false,
                errorMessage = "Password must be at least 8 characters long"
            )
        }

        val containsLetters = password.any { it.isLetter() }          // Atleast one char needs to be letter
        val containsDigits = password.any { it.isDigit() }        // Atleast one char needs to be digit
        if (!containsLetters || !containsDigits) {
            return ValidationResult(
                successful = false,
                errorMessage = "Password must include a letter and a digit."
            )
        }

        return ValidationResult(successful = true)
    }
}
