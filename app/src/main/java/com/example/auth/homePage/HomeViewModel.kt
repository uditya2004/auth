package com.example.auth.homePage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.auth.domain.repository.LoginRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val loginRepository: LoginRepository,
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadSessionData()
    }

    private fun loadSessionData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                // Get the current session from Supabase
                val session = supabaseClient.auth.currentSessionOrNull()
                val access = session?.accessToken ?: "No Access Token"
                val refresh = session?.refreshToken ?: "No Refresh Token"

                // Retrieve the user details and update the session if needed
                val userResponse = supabaseClient.auth.retrieveUserForCurrentSession(updateSession = true)
                val fullNameValue = userResponse.userMetadata?.get("full_name")
                val userName = fullNameValue?.toString() ?: userResponse.email ?: "Guest"

                _uiState.value = HomeUiState(
                    userName = userName,
                    accessToken = access,
                    refreshToken = refresh,
                    errorMessage = null,
                    isLoading = false
                )
            } catch (e: Exception) {
                val session = supabaseClient.auth.currentSessionOrNull()
                val access = session?.accessToken ?: "No Access Token"
                val refresh = session?.refreshToken ?: "No Refresh Token"
                _uiState.value = HomeUiState(
                    userName = "Guest",
                    accessToken = access,
                    refreshToken = refresh,
                    errorMessage = "Failed to fetch user details: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = loginRepository.logoutUser()
            if (result.isSuccess) {
                onLogoutSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Logout failed. Please try again."
                )
            }
        }
    }
}
