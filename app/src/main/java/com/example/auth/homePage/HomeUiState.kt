package com.example.auth.homePage

// UI state data class for the Home Screen
data class HomeUiState(
    val userName: String = "Guest",
    val accessToken: String = "No Access Token",
    val refreshToken: String = "No Refresh Token",
    val errorMessage: String? = null,
    val isLoading: Boolean = true
)

