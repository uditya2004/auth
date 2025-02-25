package com.example.auth.auth.presentation.signUp


sealed class SignUpFormEvent {
    data class NameChanged(val name: String) : SignUpFormEvent()
    data class EmailChanged(val email: String) : SignUpFormEvent()
    data class PasswordChanged(val password: String) : SignUpFormEvent()
    data class AcceptTerms(val isAccepted: Boolean) : SignUpFormEvent()

    object Submit : SignUpFormEvent()
}