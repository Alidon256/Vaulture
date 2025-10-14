package auth

import dev.gitlive.firebase.auth.js // Required for the 'FirebaseAuth.js' property
import kotlinx.coroutines.await
import kotlin.js.Promise

// --- Native JavaScript Interop Definition ---

// Defines a Kotlin interface that matches the native Firebase JS Auth object's structure.
private external interface FirebaseJsAuth {
    fun signInWithPopup(provider: dynamic): Promise<dynamic>
}

// This extension property provides a safe, typed way to access the native JS 'signInWithPopup' function.
private val dev.gitlive.firebase.auth.FirebaseAuth.jsAuth: FirebaseJsAuth
    get() = this.js.unsafeCast<FirebaseJsAuth>()

// --- Dynamic JS Interop for Provider ---

// CRITICAL AND FINAL FIX:
// 1. We import the 'GoogleAuthProvider' class directly by its JavaScript name.
@JsModule("firebase/auth")
@JsNonModule
@JsName("GoogleAuthProvider")
private external class GoogleAuthProvider {
    // We don't need to define anything inside, we just need a reference to the class constructor.
}

// --- Actual Implementations ---

/**
 * Web's implementation of performGoogleSignIn.
 * It calls the native Firebase JS signInWithPopup method via our interop helper.
 */
internal actual suspend fun AuthServiceImpl.performGoogleSignIn() {
    // 2. We now create a new instance of our "external class" directly in Kotlin.
    // The Kotlin/JS compiler knows how to translate this into `new GoogleAuthProvider()` in JavaScript.
    val jsProvider = GoogleAuthProvider()

    // 3. Call the native 'signInWithPopup' function and await the result.
    auth.jsAuth.signInWithPopup(jsProvider).await()
}

/**
 * This function is not needed for the web flow, so we provide an empty 'actual'
 * implementation to satisfy the 'expect' contract in commonMain.
 */
internal actual suspend fun AuthServiceImpl.signInWithGoogleIdToken(idToken: String) {
    // Intentionally empty. The popup flow on the web handles everything.
}
