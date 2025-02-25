package com.example.auth.auth.presentation.forgetPassword.setNew

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel

@Composable
fun SetPasswordScreen(
    navigateToLogin: () -> Unit,
    navigateToReset: () -> Unit
){

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val viewModel: SetPasswordViewModel = koinViewModel()
    val state = viewModel.state.collectAsState().value
    val context = LocalContext.current

    // Listen for validation events to trigger navigation.
    LaunchedEffect(key1 = context) {
        viewModel.validationEvents.collect { event ->
            when (event) {
                is SetPasswordViewModel.ValidationEvent.Success -> {

                    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    prefs.edit().remove("pending_reset_password").apply()

                    Toast.makeText(context, "Password Reset Successful", Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                }
            }
        }
    }


    Scaffold(
        topBar = {
            Column{
                Spacer(modifier = Modifier.height(12.dp))
                IconButton(
                    onClick = {navigateToReset()}
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
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            //Heading
            Text(
                text = "Set new password",
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF02040b)
            )

            Spacer(modifier = Modifier.height(12.dp))

            //Sub-Heading
            Text(
                text = "Must be at least 8 characters",
                fontSize = 15.sp,
                color = Color(0xFF767a7b)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Password Field
            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.onEvent(SetPasswordFormEvent.PasswordChanged(it)) },
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
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.onEvent(SetPasswordFormEvent.ConfirmPasswordChanged(it)) },
                isError = state.confirmPasswordError != null,
                shape = CircleShape,
                label = { Text("Confirm Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Password Icon"
                    )
                },
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle Password Visibility")
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            if (state.confirmPasswordError != null) {
                Text(
                    text = state.confirmPasswordError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(27.dp))

            Button(
                onClick = { viewModel.onEvent(SetPasswordFormEvent.Submit) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0e5ef5))
            ){
                Text("Reset Password")
            }

        }
    }

}

//@Preview (showSystemUi = true, showBackground = true, device = Devices.PIXEL_7_PRO)
//@Composable
//fun SetPasswordScreenPreview(){
//    SetPasswordScreen()
//}