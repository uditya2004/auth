package com.example.auth.auth.domain.repository

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID


private const val WEB_CLIENT_ID = ""  /*ENTER THE WEB_CLIENT_ID HERE COPIED FROM GOOGLE CONSOLE*/

class LoginRepository(
    private val context: Context,
    private val supabaseClient: SupabaseClient
) {

    suspend fun loginUser(email: String, password: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Sign in using Supabase Auth with the Email provider.
                supabaseClient.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                // Retrieve the current session and user.
                val session = supabaseClient.auth.currentSessionOrNull()
                val user = supabaseClient.auth.retrieveUserForCurrentSession()

                if (user != null && session != null) {
                    // No need to manually persist the tokens; Supabase now handles storage.
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Invalid credentials. Please try again."))
                }
            } catch (e: Exception) {
                if (e.message?.contains("Email not confirmed") == true) {
                    // If the email isn’t confirmed, trigger resend.
                    supabaseClient.auth.resendEmail(OtpType.Email.SIGNUP, email)
                    Result.failure(Exception("Email not verified. Verification email sent."))
                } else {
                    Result.failure(e)
                }
            }
        }
    }

    suspend fun googleSignIn(activity: Activity): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Create a CredentialManager instance using the Activity context.
                val credentialManager = CredentialManager.create(activity)

                // 2. Generate a raw nonce and hash it using SHA-256.
                val rawNonce = UUID.randomUUID().toString()
                val md = MessageDigest.getInstance("SHA-256")
                val hashedNonce = md.digest(rawNonce.toByteArray())
                    .joinToString(separator = "") { "%02x".format(it) }

                // 3. Build the Google sign-in option.
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(WEB_CLIENT_ID) // Replace with your actual Web Client ID.
                    .setNonce(hashedNonce)
                    .build()

                // 4. Build the credential request.
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                // 5. Request credentials from the system.
                val result = credentialManager.getCredential(request = request, context = activity)

                // 6. Extract the Google ID token.
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                val googleIdToken = googleIdTokenCredential.idToken

                // 7. Use Supabase Auth to sign in with the ID token.
                supabaseClient.auth.signInWith(IDToken) {
                    idToken = googleIdToken        // The ID token obtained from Google.
                    provider = Google              // Specify the provider.
                    nonce = rawNonce               // Pass the raw nonce for validation.
                }

                // 8. Retrieve the session.
                val session = supabaseClient.auth.currentSessionOrNull()
                if (session != null) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Google sign-in failed: No session returned."))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun logoutUser(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                supabaseClient.auth.signOut()
                Result.success(Unit)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
