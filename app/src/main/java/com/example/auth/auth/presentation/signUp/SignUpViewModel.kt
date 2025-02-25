package com.example.auth.auth.presentation.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.auth.domain.repository.SignUpRepository
import com.example.auth.auth.domain.use_case.ValidateEmail
import com.example.auth.auth.domain.use_case.ValidateName
import com.example.auth.auth.domain.use_case.ValidatePassword
import com.example.auth.auth.domain.use_case.ValidateTerms
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword,
    private val validateTerms: ValidateTerms,
    private val validateName: ValidateName,
    private val repository: SignUpRepository
) : ViewModel() {


    private val _state = MutableStateFlow(SignUpFormState())
    val stateFlow = _state.asStateFlow()

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()


    fun onEvent(event: SignUpFormEvent) {
        when (event) {
            is SignUpFormEvent.NameChanged -> {
                _state.update { it.copy(name = event.name) }
            }
            is SignUpFormEvent.EmailChanged -> {
                _state.update { it.copy(email = event.email) }
            }
            is SignUpFormEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password) }
            }
            is SignUpFormEvent.AcceptTerms -> {
                _state.update { it.copy(acceptedTerms = event.isAccepted) }
            }
            is SignUpFormEvent.Submit -> {
                submitData()
            }
        }
    }

    private fun submitData() {
        _state.update { it.copy(isLoading = true) }

        val state = _state.value
        val nameResult = validateName.execute(state.name)
        val emailResult = validateEmail.execute(state.email)
        val passwordResult = validatePassword.execute(state.password)
        val termsResult = validateTerms.execute(state.acceptedTerms)

        val hasError = listOf(
            emailResult,
            passwordResult,
            termsResult,
            nameResult
        ).any { !it.successful }

        if (hasError) {
            _state.update {
                it.copy(
                    nameError = nameResult.errorMessage,
                    emailError = emailResult.errorMessage,
                    passwordError = passwordResult.errorMessage,
                    termsError = termsResult.errorMessage,
                    isLoading = false
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                // Check if the email exists in the public.users table
                val userExists = repository.isEmailInPublicUsersTable(state.email)

                if (userExists) {
                    // Case 1: Email exists, show error
                    _state.update {
                        it.copy(
                            nameError = nameResult.errorMessage,
                            emailError = "Email already exists.",
                            passwordError = passwordResult.errorMessage,
                            termsError = termsResult.errorMessage,
                            isLoading = false
                        )
                    }
                } else {
                    // Case 2: New user, proceed with sign-up and OTP
                    val signUpResult = repository.signUp(
                        email = state.email,
                        password = state.password,
                        fullName = state.name
                    )
                    if (signUpResult.isSuccess) {
                        _state.update { it.copy(isLoading = false) }
                        // Navigate to otp verification step
                        validationEventChannel.send(ValidationEvent.Success(state.email))
                    } else {
                        val error = signUpResult.exceptionOrNull()?.message ?: "Unknown error"
                        _state.update { it.copy(isLoading = false, emailError = error) }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(emailError = "An error occurred: ${e.message}") }
            }
        }
    }

    sealed class ValidationEvent {
        data class Success(val email: String) : ValidationEvent()
    }
}