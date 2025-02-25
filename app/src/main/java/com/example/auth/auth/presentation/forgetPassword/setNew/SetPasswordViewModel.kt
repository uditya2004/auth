package com.example.auth.auth.presentation.forgetPassword.setNew

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.auth.domain.repository.SetPasswordRepository
import com.example.auth.auth.domain.use_case.ValidatePassword
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SetPasswordViewModel(
    private val validatePassword: ValidatePassword,
    private val repository: SetPasswordRepository,
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    private val _state = MutableStateFlow(SetPasswordFormState())
    val state = _state.asStateFlow()

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    fun onEvent(event: SetPasswordFormEvent) {
        when (event) {
            is SetPasswordFormEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password) }
            }
            is SetPasswordFormEvent.ConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = event.confirmPassword) }
            }
            is SetPasswordFormEvent.Submit -> {
                submitData()
            }
        }
    }

    private fun submitData() {
        _state.update { it.copy(isLoading = true) }

        val state = _state.value
        val passwordResult = validatePassword.execute(state.password)
        val confirmPasswordResult = (state.password == state.confirmPassword)

        if (!passwordResult.successful) {
            _state.update {
                it.copy(
                    passwordError = passwordResult.errorMessage,
                    isLoading = false
                )
            }
            return
        }

        if (!confirmPasswordResult) {
            _state.update {
                it.copy(
                    confirmPasswordError = "Passwords do not match",
                    isLoading = false
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                val updateSuccess = repository.updatePassword(state.password)

                if (updateSuccess.isSuccess) {
                    // Sign the user out immediately after updating the password.
                    supabaseClient.auth.signOut()
                    // Then navigate to the Login screen.
                    validationEventChannel.send(ValidationEvent.Success)
                } else {
                    val error = updateSuccess.exceptionOrNull()?.message ?: "Unknown error"
                    _state.update {
                        it.copy(
                            isLoading = false,
                            passwordError = error,
                            confirmPasswordError = null,
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        passwordError = "An error occurred:- ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    sealed class ValidationEvent {
        object Success : ValidationEvent()
    }

}