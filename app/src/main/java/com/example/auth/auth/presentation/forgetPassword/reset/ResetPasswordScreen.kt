package com.example.auth.auth.presentation.forgetPassword.reset

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel


@Composable
fun ForgetPasswordScreen(
    navigateToLogin: () -> Unit,
    navigateToOtp: (String) -> Unit
) {

    val viewModel: ResetPasswordViewModel = koinViewModel()

    val state = viewModel.stateFlow.collectAsState().value

    val context = LocalContext.current

    // Handle validation success event to trigger navigation
    LaunchedEffect(key1 = context) {
        viewModel.validationEvents.collect { event ->
            when (event) {
                is ResetPasswordViewModel.ValidationEvent.Success -> {
                    Toast.makeText(context, "OTP Sent successfully", Toast.LENGTH_SHORT).show()
                    navigateToOtp(event.email)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Column{
                Spacer(modifier = Modifier.height(12.dp))
                IconButton(
                    onClick = {navigateToLogin()}
                ) {

                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back Arrow",
                        tint = Color.Black
                    )
                }
            }

        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(90.dp))
            // Welcome Text
            Text(
                text = "Reset your password",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Subtext
            Text(
                text = "Please enter your email and we will send an OTP code in the next step to reset your password",
                fontSize = 14.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Email Field
            OutlinedTextField(
                value = state.email,
                onValueChange = {viewModel.onEvent(ForgetPasswordFormEvent.EmailChanged(it))},
                isError = state.emailError != null,
                shape = CircleShape,
                label = { Text("Email Address") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "Email Icon"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            if (state.emailError != null) {
                Text(
                    text = state.emailError,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 22.dp),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Continue Button
            Button(
                onClick = { viewModel.onEvent(ForgetPasswordFormEvent.Submit)/* Handle sign in */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0e5ef5)) // Dark blue color
            ) {
                Text(text = "Sent OTP", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(10.dp))
                if (state.isLoading == true){
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
