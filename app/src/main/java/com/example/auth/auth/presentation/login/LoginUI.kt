package com.example.auth.auth.presentation.login

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.Logger
import com.example.auth.R
import com.example.auth.auth.presentation.login.LoginFormEvent.GoogleSignIn
import org.koin.androidx.compose.koinViewModel


@Composable
fun LoginScreen(
    navigateToSignUp: () -> Unit,
    navigateToForgetPassword: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToOtp: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val viewModel: LoginViewModel = koinViewModel()

    val state = viewModel.state
    val context = LocalContext.current

    // Cast the context to Activity
    val activity = LocalActivity.current
        ?: throw IllegalStateException("LoginScreen must be hosted in an Activity")

    // Handle validation success event to trigger navigation
    LaunchedEffect(key1 = context) {
        viewModel.validationEvents.collect { event ->
            when (event) {
                is LoginViewModel.ValidationEvent.Success -> {
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                    navigateToHome() // Navigate to home screen
                }
                is LoginViewModel.ValidationEvent.EmailNotVerified -> {
                    Toast.makeText(context, "Email not verified. OTP sent.", Toast.LENGTH_LONG).show()
                    navigateToOtp(event.email) // Navigate to OTP verification screen
                }
                is LoginViewModel.ValidationEvent.Failure -> {
                    Logger.e("LoginUI.kt", "Error: ${event.error}")
                    Toast.makeText(context, "Error: ${event.error}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        // Welcome Text
        Text(
            text = "Welcome back!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Subtext
        Text(
            text = "Sign in to start your journey with Campus Ride",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email Field
        OutlinedTextField(
            value = state.email,
            onValueChange = {viewModel.onEvent(LoginFormEvent.EmailChanged(it))},
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
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = state.password,
            onValueChange = {viewModel.onEvent(LoginFormEvent.PasswordChanged(it))},
            isError = state.passwordError != null,
            shape = CircleShape,
            label = { Text("Password") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = "Password Icon"
                )
            },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle Password Visibility")
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        if (state.passwordError != null) {
            Text(
                text = state.passwordError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 22.dp),
                fontSize = 13.sp
            )
        }

        //Forgot Password
        Text(
            text = "Forgot Password?",
            color = Color(0xFF3A82F7), // Purple color
            fontSize = 14.sp,
            modifier = Modifier
                .padding(12.dp)
                .clickable { navigateToForgetPassword() }
        )

        // Login Button
        Button(
            onClick = { viewModel.onEvent(LoginFormEvent.Submit) /* Handle sign in */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0e5ef5)) // Dark blue color
        ) {
            Text(text = "Login", color = Color.White, fontWeight = FontWeight.Bold)
            if (state.isLoading) {
                Spacer(modifier = Modifier.width(10.dp))
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Or Sign In With Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 25.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "Or sign in with",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        // Google Sign In Button
        OutlinedButton(
            onClick = { viewModel.onEvent(GoogleSignIn(activity)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.google_icon),
                contentDescription = "Google Icon",
                tint = Color.Unspecified,
                modifier = Modifier.size(23.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Continue with Google", color = Color.Black)

            if (state.isGoogleLoading) {
                Spacer(modifier = Modifier.width(10.dp))
                CircularProgressIndicator(
                    color = Color.Black, // or choose an appropriate color
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            }

        }

        Spacer(modifier = Modifier.height(8.dp))

        Spacer(modifier = Modifier.height(24.dp))

        // Footer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Don’t have an account?")
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Sign up",
                    color = Color(0xFF3A82F7),
                    modifier = Modifier.clickable { navigateToSignUp() }
                )
            }
        }

    }
}

