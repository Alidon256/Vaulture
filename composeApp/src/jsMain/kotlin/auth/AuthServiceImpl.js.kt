package auth

import dev.gitlive.firebase.auth.js
import kotlinx.coroutines.await
import kotlin.js.Promise

// --- Dynamic JS Interop to call our global JavaScript function ---

// Define an external function that matches the signature of our function in auth.js.
// It takes the raw JavaScript auth object and returns a Promise.
private external fun performGoogleSignIn(auth: dynamic): Promise<dynamic>


// --- Actual Implementations ---

/**
 * Web's implementation of performGoogleSignIn.
 * It now delegates directly to our pure JavaScript helper function.
 */
internal actual suspend fun AuthServiceImpl.performGoogleSignIn() {
    // 1. Call the global JavaScript function, passing the raw JS auth object (this.auth.js).
    // 2. Use .await() to wait for the JavaScript Promise to complete.
    performGoogleSignIn(this.auth.js).await()
}

/**
 * This function is not needed for the web flow, so we provide an empty 'actual'
 * implementation to satisfy the 'expect' contract in commonMain.
 */
internal actual suspend fun AuthServiceImpl.signInWithGoogleIdToken(idToken: String) {
    // Intentionally empty. The popup flow on the web handles everything.
}
