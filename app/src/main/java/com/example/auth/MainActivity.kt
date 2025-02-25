package com.example.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.auth.auth.navigation.AuthNavigation
import com.example.auth.auth.presentation.main.MainViewModel
import com.example.auth.ui.theme.AuthTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    // Inject the MainViewModel via Koin.
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthTheme(darkTheme = false, dynamicColor = false) {
                // Collect the UI state exposed by the ViewModel.
                val uiState by mainViewModel.uiState.collectAsState()

                ToastDebug(mainViewModel = mainViewModel)

                if (uiState.isLoading) {
                    LoadingScreen()
                } else {
                    AuthNavigation(startDestination = uiState.startDestination)
                }
            }
        }
    }
}



@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            strokeWidth = 4.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


/**
 * Composable that listens to the ViewModel’s toast events and shows a Toast message.
 */
@Composable
fun ToastDebug(mainViewModel: MainViewModel) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        mainViewModel.toastMessageFlow.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}


