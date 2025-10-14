package auth

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1. DEFINE THE 'EXPECT' CONTRACT FOR PLATFORM-SPECIFIC IMPLEMENTATIONS
// These functions are internal and must be implemented by each platform (androidMain, jsMain).

/**
 * Triggers the platform-specific Google Sign-In flow.
 * On JS, this will open a popup. On Android, this will throw an exception
 * because the flow must be started from an Activity.
 */
internal expect suspend fun AuthServiceImpl.performGoogleSignIn()

/**
 * Completes the sign-in process on Android using the ID token from the Google Sign-In intent.
 */
internal expect suspend fun AuthServiceImpl.signInWithGoogleIdToken(idToken: String)

// 2. IMPLEMENT THE COMMON SERVICE LOGIC
class AuthServiceImpl(
    // 'internal' makes it accessible to the 'actual' functions in the same module
    internal val auth: FirebaseAuth
) : AuthService {

    override val isAuthenticated: Flow<Boolean> = auth.authStateChanged
        .map { user -> user != null && !user.isAnonymous }

    override val currentUser: Flow<User?> = auth.authStateChanged
        .map { firebaseUser -> firebaseUser?.toUser() }

    override suspend fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
    }

    override suspend fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
    }

    override suspend fun signInWithGoogle() {
        // This single public method delegates to the platform-specific 'expect' function.
        performGoogleSignIn()
    }

    override suspend fun signOut() {
        // Ensure currentUser is not null before attempting to delete anonymous user data.
        auth.currentUser?.let { user ->
            if (user.isAnonymous) {
                user.delete()
            }
        }
        auth.signOut()
    }
}

/**
 * A private helper function to map the FirebaseUser object to our own domain [User] model.
 * This prevents leaking Firebase-specific models into the rest of the app.
 */
private fun FirebaseUser.toUser() = User(
    uid = this.uid,
    displayName = this.displayName,
    email = this.email,
    isAnonymous = this.isAnonymous,

)
