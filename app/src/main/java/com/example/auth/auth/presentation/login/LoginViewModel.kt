package com.example.auth.auth.presentation.login

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.auth.domain.repository.LoginRepository
import com.example.auth.auth.domain.repository.ResetPasswordRepository
import com.example.auth.auth.domain.use_case.ValidateEmail
import com.example.auth.auth.domain.use_case.ValidatePassword
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Suppress("StaticFieldLeak")
class LoginViewModel(
    private val context: Context,
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val loginRepository: LoginRepository,
    private val resetPasswordRepository: ResetPasswordRepository
) : ViewModel() {

    var state by mutableStateOf(LoginFormState())

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun onEvent(event: LoginFormEvent) {
        when (event) {
            is LoginFormEvent.EmailChanged -> {
                state = state.copy(email = event.email)
            }
            is LoginFormEvent.PasswordChanged -> {
                state = state.copy(password = event.password)
            }
            is LoginFormEvent.Submit -> {
                submitData()
            }
            is LoginFormEvent.GoogleSignIn -> {
                googleSignIn(event.activity)
            }
        }
    }

    private fun submitData() {
        state = state.copy(isLoading = true)

        // Validation
        val emailResult = validateEmail.execute(state.email)
        val passwordResult = validatePassword.execute(state.password)

        // checking for errors
        val hasError = listOf(emailResult, passwordResult).any { !it.successful }

        //if having error then update the error message
        if (hasError) {
            state = state.copy(
                emailError = emailResult.errorMessage,
                passwordError = passwordResult.errorMessage,
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            // Check if the email exists in public.users table.
            val emailExists = resetPasswordRepository.isEmailInPublicUsersTable(state.email)
            if (!emailExists) {
                state = state.copy(
                    emailError = "Email do not exists.",
                    isLoading = false,
                    passwordError = passwordResult.errorMessage
                )
                return@launch
            }

            // Proceed with login if the email exists.
            val loginResult = loginRepository.loginUser(state.email, state.password)
            loginResult.fold(
                onSuccess = {
                    // Stop loading after success.
                    state = state.copy(isLoading = false)
                    validationEventChannel.send(ValidationEvent.Success(state.email))
                },
                onFailure = { error ->
                    if (error.message?.contains("Email not verified. Verification email sent.") == true) {
                        validationEventChannel.send(ValidationEvent.EmailNotVerified(state.email))
                    }
                    state = state.copy(
                        isLoading = false,
                        emailError = emailResult.errorMessage,
                        passwordError = error.message
                    )
                }
            )
        }
    }

    private fun googleSignIn(activity: Activity) {
        viewModelScope.launch {
            // Set the loading state for Google sign-in to true.
            state = state.copy(isGoogleLoading = true)

            val result = loginRepository.googleSignIn(activity)

            // Reset the Google loading state when the process completes.
            state = state.copy(isGoogleLoading = false)

            if (result.isSuccess) {
                validationEventChannel.send(ValidationEvent.Success("Google user"))
            } else {
                // Get the error message.
                val error = result.exceptionOrNull()?.localizedMessage ?: "Google sign-in failed"
                // Check if error message indicates cancellation/dismissal.
                if (
                    !error.contains("cancelled", ignoreCase = true)
                ) {
                    validationEventChannel.send(ValidationEvent.Failure(error))
                }
                // Else: ignore error if it appears to be user cancellation.
            }
        }
    }

    sealed class ValidationEvent {
        data class Success(val email: String) : ValidationEvent()
        data class EmailNotVerified(val email: String) : ValidationEvent()
        data class Failure(val error: String) : ValidationEvent()
    }
}


