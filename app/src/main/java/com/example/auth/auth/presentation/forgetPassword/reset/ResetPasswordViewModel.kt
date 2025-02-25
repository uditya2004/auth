package com.example.auth.auth.presentation.forgetPassword.reset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.auth.domain.repository.ResetPasswordRepository
import com.example.auth.auth.domain.use_case.ValidateEmail
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val validateEmail: ValidateEmail,
    private val repository: ResetPasswordRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ForgetPasswordFormState())
    val stateFlow = _state.asStateFlow()

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun onEvent(event: ForgetPasswordFormEvent) {
        when (event) {
            is ForgetPasswordFormEvent.EmailChanged -> {
                _state.update { it.copy(email = event.email) }
            }
            is ForgetPasswordFormEvent.Submit -> {
                submitData()
            }
        }
    }

    private fun submitData() {
        _state.update { it.copy(isLoading = true) }

        val state = _state.value
        val emailResult = validateEmail.execute(state.email)

        if (!emailResult.successful) {
            _state.update {
                it.copy(
                    emailError = emailResult.errorMessage,
                    isLoading = false
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                val userExists = repository.isEmailInPublicUsersTable(_state.value.email)
                if (!userExists) {
                    // Case 1: Email exists, show error
                    _state.update {
                        it.copy(
                            emailError = "Email do not exists.",
                            isLoading = false
                        )
                    }
                } else {
                    // Case - 2: Sent the OTP and navigate to otp Screen
                    val otpSentResult = repository.sendOTP(_state.value.email)

                    if (otpSentResult.isSuccess) {
                        _state.update { it.copy(isLoading = false) }
                        validationEventChannel.send(ValidationEvent.Success(state.email))
                    } else {
                        _state.update {
                            it.copy(
                                emailError = "Failed to send OTP. Please try again.",
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (e: Exception){
                // Handle any unexpected exceptions.
                _state.update {
                    it.copy(
                        emailError = "An error occurred :- ${e.message} ",
                        isLoading = false
                    )
                }

            }
        }
    }

    sealed class ValidationEvent {
        data class Success(val email: String) : ValidationEvent()
    }
}




