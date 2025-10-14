package auth

/**
 * Represents our app-specific User model.
 * It's good practice to have our own model instead of using the Firebase one directly in the UI.
 * This class holds the essential, non-sensitive user data needed by the presentation layer.
 *
 * @param uid The unique ID of the user.
 * @param displayName The user's display name, which might be null (e.g., for email/password users).
 * @param email The user's email address.
 */
data class User(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val isAnonymous: Boolean = true
)