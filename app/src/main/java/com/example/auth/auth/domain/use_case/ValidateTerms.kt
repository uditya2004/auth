package com.example.auth.auth.domain.use_case

class ValidateTerms{

    fun execute(acceptedTerms: Boolean): ValidationResult {
        if(!acceptedTerms) {             // If not checked the box return error
            return ValidationResult(
                successful = false,
                errorMessage = "Please accept the terms"
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}