package com.example.auth.auth.presentation.login

import android.app.Activity

sealed class LoginFormEvent {
    data class EmailChanged(val email: String) : LoginFormEvent()
    data class PasswordChanged(val password: String) : LoginFormEvent()
    object Submit : LoginFormEvent()
    data class GoogleSignIn(val activity: Activity) : LoginFormEvent()
}