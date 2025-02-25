package com.example.auth.auth.presentation.signUp

import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.runtime.collectAsState
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
import com.example.auth.R

@Composable
fun SignUpScreen(
    navigateToLogin: () -> Unit,
    navigateToOtp: (String) -> Unit,  // Pass email to OTP screen
) {
    val viewModel: SignUpViewModel = koinViewModel()

    var passwordVisible by remember { mutableStateOf(false) }

    val state = viewModel.stateFlow.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(key1 = context) {
        viewModel.validationEvents.collect { event ->
            when (event) {
                is SignUpViewModel.ValidationEvent.Success -> {
                    navigateToOtp(event.email)    // On successful signup, navigate to OTP screen
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        // Welcome Text
        Text(
            text = "Register your account!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Text:- Copy
        Text(
            text = "Sign up to start your journey with Campus Ride",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Name Field
        OutlinedTextField(
            value = state.name,
            onValueChange = {viewModel.onEvent(SignUpFormEvent.NameChanged(it))},
            isError = state.nameError != null,
            shape = CircleShape,
            label = { Text("Full Name") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Email Icon"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        if (state.nameError != null) {
            Text(
                text = state.nameError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 22.dp),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Email Field
        OutlinedTextField(
            value = state.email,
            onValueChange = {viewModel.onEvent(SignUpFormEvent.EmailChanged(it))},
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
            onValueChange = {viewModel.onEvent(SignUpFormEvent.PasswordChanged(it))},
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

        Spacer(modifier = Modifier.height(5.dp))
        // Terms and condition Checkbox
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.acceptedTerms,
                    onCheckedChange = { viewModel.onEvent(SignUpFormEvent.AcceptTerms(it))},
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Gray, // Purple color for checked state
                        uncheckedColor = Color(0xFF9C9EA1) // Gray color for unchecked state
                    )
                )
                Text(text = "I agree to the ", color = Color(0xFF9C9EA1), fontSize = 15.sp)
                Text(
                    text = "Campus Ride Terms",
                    fontSize = 15.sp,
                    color = Color(0xFF3A82F7),
                    modifier = Modifier
                        .clickable { /* Handle Sign up click */ }
                )
            }
            if (state.termsError != null) {
                Text(
                    text = state.termsError,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 15.dp, end = 15.dp, bottom = 14.dp),
                    fontSize = 13.sp
                )
            }

        }
        Spacer(modifier = Modifier.height(5.dp))

        // Sign Up Button
        Button(
            onClick = {
                viewModel.onEvent(SignUpFormEvent.Submit)     /* Handle sign up */
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0e5ef5)), // Dark blue color
        ) {

            if (state.isLoading) {
                Text(text = "Sign up", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(10.dp))
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(text = "Sign up", color = Color.White, fontWeight = FontWeight.Bold)
            }

        }

        // Or Sign Up With Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "Or sign up with",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        // Google Sign In Button
        OutlinedButton(
            onClick = { /* Handle Google Sign in */ },
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
        }

        Spacer(modifier = Modifier.height(90.dp))

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
                Text(text = "Already have an account?")
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Sign in",
                    color = Color(0xFF3A82F7),
                    modifier = Modifier.clickable { navigateToLogin()/* Handle Sign up click */ }
                )
            }
        }
    }
}