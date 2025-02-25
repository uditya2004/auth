package com.example.auth.auth.presentation.otpVerification

sealed interface OtpAction {
    data class OnEnterNumber(val number: Int?, val index: Int): OtpAction      // storing what number user typed and at what index in the list of 4 elements
    data class OnChangeFieldFocused(val index: Int): OtpAction
    data object OnKeyboardBack: OtpAction        // controlling what to do on keyboard back button press
}