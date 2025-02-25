package com.example.auth.auth.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween

// Reusable enter transition
fun defaultEnterTransition(): EnterTransition {
    return fadeIn(animationSpec = tween(300)) + slideInHorizontally(
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
    ) { it }
}

// Reusable exit transition
fun defaultExitTransition(): ExitTransition {
    return fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
        animationSpec = tween(durationMillis = 300, easing = FastOutLinearInEasing)
    ) { -it }
}

// Reusable pop enter transition
fun defaultPopEnterTransition(): EnterTransition {
    return fadeIn(animationSpec = tween(300)) + slideInHorizontally(
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
    ) { -it }
}

// Reusable pop exit transition
fun defaultPopExitTransition(): ExitTransition {
    return fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
        animationSpec = tween(durationMillis = 300, easing = FastOutLinearInEasing)
    ) { it }
}

// Reusable vertical transitions for Forget Password screen
fun verticalEnterTransition(): EnterTransition {
    return fadeIn() + slideInVertically { it }
}

fun verticalExitTransition(): ExitTransition {
    return fadeOut() + slideOutVertically { -it }
}

fun verticalPopEnterTransition(): EnterTransition {
    return fadeIn() + slideInVertically { -it }
}

fun verticalPopExitTransition(): ExitTransition {
    return fadeOut() + slideOutVertically { it }
}
