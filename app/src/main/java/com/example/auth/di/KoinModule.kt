package com.example.auth.di

import com.example.auth.auth.domain.repository.LoginRepository
import com.example.auth.auth.domain.repository.ResetPasswordRepository
import com.example.auth.auth.domain.repository.SetPasswordRepository
import com.example.auth.auth.domain.repository.SignUpRepository
import com.example.auth.auth.domain.supabase.SupabaseClientProvider
import com.example.auth.auth.domain.use_case.ValidateEmail
import com.example.auth.auth.domain.use_case.ValidateName
import com.example.auth.auth.domain.use_case.ValidatePassword
import com.example.auth.auth.domain.use_case.ValidateTerms
import com.example.auth.auth.presentation.forgetPassword.reset.ResetPasswordViewModel
import com.example.auth.auth.presentation.forgetPassword.setNew.SetPasswordViewModel
import com.example.auth.auth.presentation.login.LoginViewModel
import com.example.auth.auth.presentation.main.MainViewModel
import com.example.auth.auth.presentation.otpVerification.OtpViewModel
import com.example.auth.auth.presentation.signUp.SignUpViewModel
import com.example.auth.homePage.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


/**
 * - This module is used to define dependencies that are instances of normal classes
 * - Register all dependencies your ViewModels need.
 */
val appModule = module {
    // Register SupabaseClientProvider as a Singleton
    single { SupabaseClientProvider.client }

    // Define a singleton for SignUpRepository
    single { SignUpRepository(get()) }

    single { ResetPasswordRepository(get()) }

    single { SetPasswordRepository(get()) }

    // Provide LoginRepository (Now with SupabaseClientProvider)
    single { LoginRepository(androidContext(), get() ) }


    // Define use-case dependencies
    factoryOf(::ValidateEmail)
    factoryOf(::ValidatePassword)
    factoryOf(::ValidateTerms)
    factoryOf(::ValidateName)
}


/**
 * - This module provides all ViewModels used in the app.
 * - Every ViewModel must be declared here.
 */
val viewModelModule = module {
    // Use factoryOf instead of viewModel()
    viewModelOf(::SignUpViewModel)
    viewModelOf(::OtpViewModel)
    viewModelOf(::LoginViewModel) // Added LoginViewModel
    viewModelOf(::HomeViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::ResetPasswordViewModel)
    viewModelOf(::SetPasswordViewModel)
}