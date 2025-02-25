package com.example.auth.auth.presentation.otpVerification

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import kotlin.collections.none


//============
// OTP Complete SCREEN UI
//============
@Composable
fun OtpVerificationScreen(
    email: String,
    flow: String,
    navigateAfterOtp: () -> Unit,
    onBackPressed: () -> Unit
){
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column{
                Spacer(modifier = Modifier.height(12.dp))
                IconButton(
                    onClick = {onBackPressed()}
                ) {

                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back Arrow",
                        tint = Color.Black
                    )
                }
            }
        }
    ) { innerPadding ->

        val viewModel: OtpViewModel = koinViewModel()

        val state by viewModel.state.collectAsStateWithLifecycle()
        val focusRequesters = remember {
            List(6) { FocusRequester() }
        }
        val focusManager = LocalFocusManager.current
        val keyboardManager = LocalSoftwareKeyboardController.current

        LaunchedEffect(state.focusedIndex) {
            state.focusedIndex?.let { index ->
                focusRequesters.getOrNull(index)?.requestFocus()
            }
        }

        LaunchedEffect(state.code, keyboardManager) {
            val allNumbersEntered = state.code.none { it == null }
            if(allNumbersEntered) {
                focusRequesters.forEach {
                    it.freeFocus()
                }
                focusManager.clearFocus()
                keyboardManager?.hide()
            }
        }

        val context = LocalContext.current
        // Only for the password reset flow, set a flag indicating a reset is pending.
        LaunchedEffect(key1 = flow) {
            if (flow == "reset") {
                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                prefs.edit().putBoolean("pending_reset_password", true).apply() // Mark reset as pending
            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = innerPadding.calculateTopPadding()),
        ) {
            // Space between the Top bar and Header Section
            Spacer(modifier = Modifier.height(90.dp))

            // Header Section
            Text(
                text = "OTP Code Verification",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please check your email $email to see the verification code.",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(48.dp))

            // OTP Input Section
            OtpScreen(
                state = state,
                focusRequesters = focusRequesters,
                onAction = { action ->
                    when (action) {
                        is OtpAction.OnEnterNumber -> {
                            if (action.number != null) {
                                focusRequesters[action.index].freeFocus()  // on entering the number, move the focus to the next text field and remove the focus from current text field.
                            }
                        }

                        else -> Unit
                    }
                    viewModel.onAction(action)
                }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Verify Button
            Button(
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                onClick = {
                    // Handle verification action
                    viewModel.verifyOtp(email)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),   // inner height of button
                shape = CircleShape
            ) {
                Text(text = "Verify", fontSize = 16.sp, color = Color.White)
            }

            when(state.isValid) {
                true -> {
                    LaunchedEffect(Unit) {
                        navigateAfterOtp()
                    }
                }
                false -> {
                    Text("Invalid OTP. Please try again.", color = Color.Red)
                }
                null -> Unit // Do nothing while waiting
            }


        }
    }
}