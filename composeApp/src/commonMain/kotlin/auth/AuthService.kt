package auth

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
}
