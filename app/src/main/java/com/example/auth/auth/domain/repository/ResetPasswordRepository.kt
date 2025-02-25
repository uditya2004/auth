package com.example.auth.auth.domain.repository

import com.example.auth.Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResetPasswordRepository(
    private val supabaseClient: SupabaseClient
) {
    // Function to check if the user's email exists in the public.users table
    suspend fun isEmailInPublicUsersTable(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Selecting all users having provider as "email"
                val result  = supabaseClient
                    .from("users")
                    .select(columns = Columns.list("email")){
                        filter {
                            eq("provider", "email")
                            eq("email", email)
                        }
                    }
                    .decodeList<Map<String, String>>() // Decodes list of email entries
                result.isNotEmpty()   // Returns true if the email exists
            } catch (e: Exception) {
                Logger.e("SignUpRepository", "Error checking email in public.users: ${e.message}")
                false
            }
        }
    }

    // New function to send OTP using Supabase's OTP sign-in method.
    suspend fun sendOTP(email: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                supabaseClient.auth.resetPasswordForEmail(email = email)
                return@withContext Result.success(Unit)

            } catch (e: Exception) {
                Logger.e("ResetPasswordRepository", "Error sending OTP: ${e.message}")
                return@withContext Result.failure(e)
            }
        }
    }
}