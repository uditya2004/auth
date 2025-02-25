package com.example.auth.auth.presentation.forgetPassword.reset

sealed class ForgetPasswordFormEvent {
    data class EmailChanged(val email: String) : ForgetPasswordFormEvent()
    object Submit : ForgetPasswordFormEvent()
}