package com.example.auth.auth.presentation.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.Logger
import com.example.auth.auth.navigation.Screen
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// UI state data class to hold loading status and the chosen start destination.
data class MainUiState(
    val isLoading: Boolean = true,
    val startDestination: String = Screen.Login.route
)

class MainViewModel(
    application: Application,
    private val supabaseClient: SupabaseClient
) : AndroidViewModel(application) {

    // Internal mutable state which the UI observes.
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState

    // SharedFlow to emit debug/toast messages.
    private val _toastMessageFlow = MutableSharedFlow<String>()
    val toastMessageFlow = _toastMessageFlow.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Wait until the Supabase auth system is initialized (autoLoadFromStorage loads the stored session).
            awaitSessionInitialization()

            // Retrieve the SharedPreferences using the Application context.
            val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val pendingReset = prefs.getBoolean("pending_reset_password", false)

            if (pendingReset) {
                // If a pending reset flag is still set, sign out the user
                supabaseClient.auth.signOut()  // Force sign out if a reset is pending
                // Remove the flag so it does not affect future launches.
                prefs.edit().remove("pending_reset_password").apply()
                Logger.d("MainViewModel", "MainViewModel: if (pendingReset) part : $pendingReset")
            } else {
                // For normal login flows, ensure any stale pending flag is cleared.
                prefs.edit().remove("pending_reset_password").apply()
                Logger.d("MainViewModel", "MainViewModel: else {} part: $pendingReset")
                // End NEW CODE
            }

            // Check if a valid session exists.
            val session = supabaseClient.auth.currentSessionOrNull()
            val destination = if (session != null) Screen.HomeScreen.route else Screen.Login.route

            // Update the UI state on the main thread.
            withContext(Dispatchers.Main) {
                _uiState.value = MainUiState(isLoading = false, startDestination = destination)
            }
        }
        // Start listening to authentication events.
        listenToAuthEvents()
    }

    /**
     * Suspend until the Supabase auth system emits a state that is not initializing.
     */
    private suspend fun awaitSessionInitialization() {
        supabaseClient.auth.sessionStatus.first { status ->
            status !is SessionStatus.Initializing
        }
    }

    /**
     * Listen to Supabase authentication events.
     */
    private fun listenToAuthEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            supabaseClient.auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        val token = supabaseClient.auth.currentAccessTokenOrNull()
                        Logger.d("Auth", "Authenticated: Access Token: $token")
                        _toastMessageFlow.emit("Authenticated")
                    }
                    is SessionStatus.NotAuthenticated -> {
                        Logger.d("Auth", "User is not authenticated")
                        _toastMessageFlow.emit("User is not authenticated")
                    }
                    SessionStatus.Initializing -> {
                        Logger.d("Auth", "Auth is initializing")
                        _toastMessageFlow.emit("Auth is initializing")
                    }
                    is SessionStatus.RefreshFailure -> {
                        Logger.d("Auth", "Session refresh failed: ${status.cause}")
                        _toastMessageFlow.emit("Session refresh failed: ${status.cause}")
                    }
                    else -> {
                        Logger.d("Auth", "Other auth state: $status")
                    }
                }
            }
        }
    }
}
