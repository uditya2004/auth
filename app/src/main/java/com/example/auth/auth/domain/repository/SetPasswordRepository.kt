package com.example.auth.auth.domain.repository

import com.example.auth.Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SetPasswordRepository(
    private val supabaseClient: SupabaseClient
) {
    // Updates the password for the currently authenticated user.
    suspend fun updatePassword(newPassword: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Calls the Supabase updateUser() method to update the password.
                supabaseClient.auth.updateUser {
                    password = newPassword
                }
                return@withContext Result.success(Unit)
            } catch (e: Exception) {
                Logger.e("SetPasswordRepository", "Error updating password: ${e.message}")
                return@withContext Result.failure(e)
            }
        }
    }
}