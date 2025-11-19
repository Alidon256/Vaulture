/*package auth

import kotlinx.coroutines.flow.Flow

/**
 * Defines the public contract for authentication services.
 * This is the single source of truth for UI/ViewModels to interact with authentication.
 * It abstracts away the implementation details of Firebase, Google Sign-In, etc.
 */
interface AuthService {

    /**
     * A flow that emits the current authentication state.
     * Emits `true` if a user is signed in, `false` otherwise.
     */
    val isAuthenticated: Flow<Boolean>

    /**
     * A flow that emits the current user details.
     * Emits a [User] object if signed in, or `null` if not.
     */
    val currentUser: Flow<User?>

    /**
     * Creates a new user with the given email and password.
     * @throws Exception if the user creation fails (e.g., email already in use).
     */
    suspend fun createUser(
        email: String,
        password: String,
        username: String,
        profilePicture: ByteArray?
    )

    /**
     * Signs in an existing user with the given email and password.
     * @throws Exception if sign-in fails (e.g., wrong password).
     */
    suspend fun signInWithEmail(email: String, password: String)

    /**
     * Initiates the Google Sign-In flow.
     * The implementation is platform-specific (popup on web, intent on Android).
     * @throws Exception if the sign-in process is cancelled or fails.
     */
    suspend fun signInWithGoogle()

    /**
     * Signs out the current user.
     */
    suspend fun signOut()
}*/
package auth

import kotlinx.coroutines.flow.Flow

/**
 * Defines the public contract for authentication and user profile services.
 * This is the single source of truth for UI/ViewModels to interact with authentication.
 * Production-ready: Decouples Auth creation from Profile creation.
 */
interface AuthService {

    /**
     * A flow that emits `true` if a user is signed in, `false` otherwise.
     */
    val isAuthenticated: Flow<Boolean>

    /**
     * A flow that emits the current user details. Emits a [User] object or `null`.
     */
    val currentUser: Flow<User?>

    /**
     * Creates a new user in Firebase Authentication only. This is a fast operation.
     * @return The UID of the newly created user.
     * @throws Exception if auth creation fails (e.g., email already in use).
     */
    suspend fun createAuthUser(email: String, password: String): String

    /**
     * Creates the user's profile document in Firestore, including uploading a profile picture.
     * This is the second, slower step in the sign-up process.
     * @throws Exception if Firestore write or image upload fails.
     */
    suspend fun createUserProfile(
        uid: String,
        email: String,
        username: String,
        profilePicture: ByteArray?
    )

    /**
     * A crucial function called after ANY successful sign-in (Email, Google, etc.).
     * It checks if a Firestore document exists for the current user. If not, it creates one.
     * This guarantees every user, including those from Google Sign-In, has a profile document.
     * @return `true` if a new profile was created, `false` otherwise.
     */
    suspend fun onSignInSuccess(): Boolean

    /**
     * Signs in an existing user with email and password.
     * @throws Exception if sign-in fails.
     */
    suspend fun signInWithEmail(email: String, password: String)

    /**
     * Initiates the Google Sign-In flow.
     * @throws Exception if the process is cancelled or fails.
     */
    suspend fun signInWithGoogle()

    /**
     * Signs out the current user.
     */
    suspend fun signOut()
}

