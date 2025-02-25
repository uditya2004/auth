package com.example.auth.auth.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.auth.homePage.HomeScreen
import com.example.auth.auth.presentation.forgetPassword.reset.ForgetPasswordScreen
import com.example.auth.auth.presentation.forgetPassword.setNew.SetPasswordScreen
import com.example.auth.auth.presentation.login.LoginScreen
import com.example.auth.auth.presentation.otpVerification.OtpVerificationScreen
import com.example.auth.auth.presentation.signUp.SignUpScreen

@Composable
fun AuthNavigation(
    startDestination: String
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        // Login Screen
        composable(
            route = Screen.Login.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {
            LoginScreen(
                navigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                navigateToForgetPassword = { navController.navigate(Screen.ResetPassword.route) },
                navigateToHome = {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                },
                navigateToOtp = { email ->
                    navController.navigate("${Screen.OtpVerification.route}/$email/login")
                }
            )
        }

        // Sign-Up Screen
        composable(
            route = Screen.SignUp.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {
            SignUpScreen(
                navigateToLogin = { navController.navigate(Screen.Login.route) },
                navigateToOtp = { email ->
                    navController.navigate("${Screen.OtpVerification.route}/$email/signup")
                }
            )
        }

        // OTP Screen
        composable(
            route = "${Screen.OtpVerification.route}/{email}/{flow}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("flow") { type = NavType.StringType }
            ),
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val flow = backStackEntry.arguments?.getString("flow") ?: "signup"

            OtpVerificationScreen(
                email = email,
                flow = flow,
                navigateAfterOtp = {
                    if (flow == "login") {
                        // If coming from login, after successful OTP verification, go to Home
                        navController.navigate(Screen.HomeScreen.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                    else if(flow == "reset"){
                        // If coming from reset password, after successful OTP verification, go to Home
                        navController.navigate(Screen.SetNewPassword.route) {
                            popUpTo(Screen.ResetPassword.route) { inclusive = true }
                        }
                    }

                    else {
                        // If coming from sign-up, after OTP verification, go to Login
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onBackPressed  = { navController.popBackStack() },
            )
        }


        // Reset Password Screen
        composable(
            route = Screen.ResetPassword.route,
            enterTransition = { verticalEnterTransition() },
            exitTransition = { verticalExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {
            ForgetPasswordScreen(
                navigateToLogin = { navController.navigate(Screen.Login.route) },
                navigateToOtp = {email->
                    navController.navigate("${Screen.OtpVerification.route}/$email/reset")
                }
            )
        }

        // Set New Password Screen
        composable(
            route = Screen.SetNewPassword.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ){
            SetPasswordScreen(
                navigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                },
                navigateToReset = { navController.navigate(Screen.ResetPassword.route) }
            )
        }

        // Home Screen
        composable(
            route = Screen.HomeScreen.route,
            enterTransition = { defaultEnterTransition() },
            exitTransition = { defaultExitTransition() },
            popEnterTransition = { defaultPopEnterTransition() },
            popExitTransition = { defaultPopExitTransition() }
        ) {
            HomeScreen(
                navigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) // Clears the backstack if needed.
                    }
                }
            )
        }
    }
}