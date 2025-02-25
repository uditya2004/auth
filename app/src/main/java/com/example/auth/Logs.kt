package com.example.auth

import android.util.Log

object Logger {
    private const val BASE_TAG = "MyLog" // Replace with your app-specific base tag

    fun e(tag: String, message: String) {
        Log.e("$BASE_TAG-$tag", message)
    }

    // Add other levels if needed
    fun d(tag: String, message: String) {
        Log.d("$BASE_TAG-$tag", message)
    }

    fun i(tag: String, message: String) {
        Log.i("$BASE_TAG-$tag", message)
    }
}
