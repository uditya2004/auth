package com.example.auth.auth.presentation.signUp

data class SignUpFormState(
    val name: String = "",
    val nameError: String? = null,

    val email: String = "",
    val emailError: String? = null,

    val password: String = "",
    val passwordError: String? = null,

    val acceptedTerms: Boolean = false,
    val termsError: String? = null,

    val isLoading: Boolean = false
)