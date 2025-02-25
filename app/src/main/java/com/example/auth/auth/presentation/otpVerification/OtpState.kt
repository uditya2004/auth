package com.example.auth.auth.presentation.otpVerification


data class OtpState(
    val code: List<Int?> = (1..6).map { null },    // list of 4 entries, all initialized to null
    val focusedIndex: Int? = null,     // index of currently focused cell
    val isValid: Boolean? = null       // checking if otp is valid or not
)