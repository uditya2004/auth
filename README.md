# Jetpack Compose Authentication with Supabase

A modern Android authentication app built with **Jetpack Compose** and **Supabase**. This project demonstrates a complete authentication flow, including email/password login, Google Sign-In, sign-up, OTP verification, and password reset functionality. It leverages the power of **Kotlin**, **Supabase**, and **Koin** for dependency injection, providing a clean and scalable architecture.

## Screenshots

*(Adding screenshots)*

## Features

- ✅ **Email/Password Auth** with Supabase  
- ✅ **Google Sign-In** via Android Credential Manager  
- ✅ **OTP Verification** for account confirmation and password reset  
- ✅ **Password Reset** with OTP-based flow  
- ✅ **Jetpack Compose UI** using Material 3  
- ✅ **Smooth Navigation** with animations  
- ✅ **Koin Dependency Injection** for modular code  
- ✅ **Robust Validation** for user inputs  
- ✅ **Supabase Integration** for backend auth  
- ✅ **Responsive Design** following Material guidelines  


## Tech Stack

- **Kotlin**: Primary programming language.
- **Jetpack Compose**: Modern Android UI toolkit.
- **Supabase**: Backend-as-a-Service for authentication and database.
- **Koin**: Lightweight dependency injection framework.
- **Ktor**: HTTP client for network requests.
- **Coroutines**: Asynchronous programming with Kotlin Coroutines.
- **Android Credential Manager**: For Google Sign-In integration.
- **Material 3**: Latest Material Design components.
- **Gradle**: Build system with Kotlin DSL.

## Getting Started

### Prerequisites

- Android Studio (latest stable version recommended)
- Kotlin 2.0.0 or higher
- Supabase account and project setup
- Google Cloud Console project with OAuth 2.0 credentials

### Setup Instructions

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/uditya2004/auth.git
   cd auth
   ```

2. **Configure Supabase**:
   - Create a Supabase project at [supabase.com](https://supabase.com).
   - Copy your Supabase URL and Anon Key.
   - Open `SupabaseClientProvider.kt` and update the following:
     ```kotlin
     private const val SUPABASE_URL = "your-supabase-url"
     private const val SUPABASE_ANON_KEY = "your-supabase-anon-key"
     ```

3. **Set Up Google Sign-In**:
   - Create a project in the [Google Cloud Console](https://console.cloud.google.com).
   - Enable the "Identity Toolkit API" and configure OAuth 2.0 credentials.
   - Copy your Web Client ID and update `LoginRepository.kt`:
     ```kotlin
     private const val WEB_CLIENT_ID = "your-web-client-id"
     ```

4. **Build and Run**:
   - Open the project in Android Studio.
   - Sync the project with Gradle files.
   - Run the app on an emulator or physical device (min SDK 26).

## Project Structure
```
auth/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/auth/
│   │   │   │   ├── auth/        # Authentication logic
│   │   │   │   ├── di/          # Dependency Injection with Koin
│   │   │   │   ├── homePage/    # Home screen after login
│   │   │   │   └── ui/          # Theme and UI components
│   │   ├── res/                 # Resources (drawables, values)
│   └── build.gradle.kts         # App module build configuration
├── gradle/                      # Gradle wrapper files
└── README.md                    # This file
```
## Usage

- **Login**: Enter email and password or use Google Sign-In to authenticate.
- **Sign-Up**: Register with a name, email, and password, then verify via OTP.
- **Forgot Password**: Reset password using an OTP sent to your email.
- **Home**: View user details and logout after successful authentication.

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -m "Add your feature"`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a Pull Request.

## License

This project is licensed under the MIT License.

## Keywords

Android, Jetpack Compose, Supabase, Authentication, Kotlin, Koin, Google Sign-In, OTP Verification, Material 3, Coroutines, Navigation, Dependency Injection, Password Reset, Mobile Development, Open Source

Built with ❤️ by [Uditya Kumar](https://github.com/uditya2004)
