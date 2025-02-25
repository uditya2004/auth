package com.example.auth.auth.presentation.otpVerification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.Logger
import com.example.auth.auth.domain.repository.SignUpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class OtpViewModel(
    private val repository: SignUpRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OtpState())
    val state = _state.asStateFlow()

    fun verifyOtp(email: String) {
        viewModelScope.launch {
            if (email.isBlank()) {
                _state.update { it.copy(isValid = false) }
                return@launch
            }

            try {
                val otpCode = state.value.code.joinToString("") { it?.toString() ?: "" }

                val result = repository.verifyEmail(email, otpCode)
                Logger.d("OtpViewModel", "Backend Result: $result")

                if (result.isSuccess) {
                    _state.update { it.copy(isValid = true) }   // Mark as valid on success
                } else {
                    _state.update { it.copy(isValid = false) }  // Mark as invalid on failure
                }
            } catch (e: Exception) {
                Logger.d("OtpViewModel", "Backend Result: ${e.message}")
                _state.update { it.copy(isValid = false) }      // Handle exception as invalid
            }
        }
    }

    fun onAction(action: OtpAction) {    // To receive the action from ui
        when(action) {
            is OtpAction.OnChangeFieldFocused -> {
                _state.update { it.copy(
                    focusedIndex = action.index
                ) }
            }
            is OtpAction.OnEnterNumber -> {
                enterNumber(action.number, action.index)
            }
            OtpAction.OnKeyboardBack -> {
                val previousIndex = getPreviousFocusedIndex(state.value.focusedIndex)
                _state.update { it.copy(
                    code = it.code.mapIndexed { index, number ->
                        if(index == previousIndex) {
                            null
                        } else {
                            number
                        }
                    },
                    focusedIndex = previousIndex
                ) }
            }
        }
    }

    private fun enterNumber(number: Int?, index: Int) {
        val newCode = state.value.code.mapIndexed { currentIndex, currentNumber ->
            if(currentIndex == index) {
                number
            } else {
                currentNumber
            }
        }
        val wasNumberRemoved = number == null
        _state.update { it.copy(
            code = newCode,
            focusedIndex = if(wasNumberRemoved || it.code.getOrNull(index) != null) {
                it.focusedIndex
            } else {
                getNextFocusedTextFieldIndex(
                    currentCode = it.code,
                    currentFocusedIndex = it.focusedIndex
                )
            },
            isValid = null // Reset validity to null until verified by the API
        ) }
    }

    private fun getPreviousFocusedIndex(currentIndex: Int?): Int? {
        return currentIndex?.minus(1)?.coerceAtLeast(0)
    }

    private fun getNextFocusedTextFieldIndex(
        currentCode: List<Int?>,
        currentFocusedIndex: Int?
    ): Int? {
        if(currentFocusedIndex == null) {
            return null
        }

        if (currentFocusedIndex >= currentCode.size - 1) {
            return null
        }

        return getFirstEmptyFieldIndexAfterFocusedIndex(
            code = currentCode,
            currentFocusedIndex = currentFocusedIndex
        )
    }

    private fun getFirstEmptyFieldIndexAfterFocusedIndex(
        code: List<Int?>,
        currentFocusedIndex: Int
    ): Int {
        code.forEachIndexed { index, number ->
            if(index <= currentFocusedIndex) {
                return@forEachIndexed
            }
            if(number == null) {
                return index
            }
        }
        return currentFocusedIndex
    }
}