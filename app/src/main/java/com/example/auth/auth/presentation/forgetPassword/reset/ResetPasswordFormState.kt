package com.example.auth.auth.presentation.forgetPassword.reset

data class ForgetPasswordFormState(
    val email: String = "",
    val emailError: String? = null,

    val isLoading: Boolean = false
)