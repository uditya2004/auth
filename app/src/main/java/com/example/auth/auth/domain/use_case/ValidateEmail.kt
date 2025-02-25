package com.example.auth.auth.domain.use_case

import android.util.Patterns

class ValidateEmail{
    fun execute(email: String): ValidationResult {
        // Ensure email is trimmed to avoid issues with leading/trailing spaces
        val trimmedEmail = email.trim()

        // Check if email is blank
        if (trimmedEmail.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "The email can't be blank"
            )
        }

        // Check for maximum length (254 characters as per standards)
        if (email.trim().length > 254) {
            return ValidationResult(
                successful = false,
                errorMessage = "The email must not exceed 254 characters"
            )
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter a valid email"
            )
        }

        return ValidationResult(successful = true)
    }
}
