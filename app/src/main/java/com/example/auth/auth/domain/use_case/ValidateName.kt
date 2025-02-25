package com.example.auth.auth.domain.use_case

class ValidateName{
    fun execute(name: String): ValidationResult {

        // Trim the name to handle any leading or trailing spaces
        val trimmedName = name.trim()

        // Check if name is blank (after trimming)
        if (trimmedName.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Name can't be blank or just spaces."
            )
        }


        // Check for length (minimum 2 characters and maximum 50 characters)
        if (trimmedName.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = "The name must be at least 2 characters long"
            )
        }

        if (trimmedName.length > 50) {
            return ValidationResult(
                successful = false,
                errorMessage = "The name must not exceed 50 characters"
            )
        }

        // Check for invalid symbols. Allow only letters, spaces and digits.
        val invalidSymbolRegex = Regex("[^a-zA-Z\\s0-9]") // Matches any character that is NOT a letter, whitespace or digit
        if (invalidSymbolRegex.containsMatchIn(trimmedName)) {
            return ValidationResult(
                successful = false,
                errorMessage = "The name must not contain invalid symbols"
            )
        }

        // Check for leading or trailing spaces (already handled by trimming)
        if (name != trimmedName) {
            return ValidationResult(
                successful = false,
                errorMessage = "The name must not have leading or trailing spaces"
            )
        }

        // Check for forbidden names
        val forbiddenNames = listOf("admin", "root", "null")
        if (trimmedName.lowercase() in forbiddenNames) {
            return ValidationResult(
                successful = false,
                errorMessage = "The name is not allowed"
            )
        }

        // Check if name contains numbers (if not allowed)
        if (trimmedName.any { it.isDigit() }) {
            return ValidationResult(
                successful = false,
                errorMessage = "The name must not contain numbers"
            )
        }

        return ValidationResult(   // return no errors if not matches the above cases
            successful = true
        )

    }
}