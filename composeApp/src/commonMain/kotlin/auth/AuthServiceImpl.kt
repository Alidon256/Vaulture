package auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
// Assume your custom upload function is in 'utils'
// You will need to create this expect/actual function
// import utils.upload


// --- EXPECT/ACTUAL DEFINITIONS (These remain the same) ---
internal expect suspend fun AuthServiceImpl.performGoogleSignIn()
internal expect suspend fun AuthServiceImpl.signInWithGoogleIdToken(idToken: String)


// --- IMPLEMENTATION ---
class AuthServiceImpl(
    internal val auth: FirebaseAuth
) : AuthService {

    private val firestore = Firebase.firestore
    private val storage = Firebase.storage

    override val isAuthenticated: Flow<Boolean> = auth.authStateChanged
        .map { user -> user != null && !user.isAnonymous }

    override val currentUser: Flow<User?> = auth.authStateChanged
        .map { firebaseUser -> firebaseUser?.toUser() }


        override suspend fun createUser(
            email: String,
            password: String,
            username: String,
            profilePicture: ByteArray?
        ) {
            println("AuthService: Starting user creation for $email")

            // 1. Create the user in Firebase Auth.
            val authResult = auth.createUserWithEmailAndPassword(email, password)
            val firebaseUser = authResult.user
                ?: throw Exception("User creation failed: Firebase user object is null after creation.")
            println("AuthService: Firebase Auth user created successfully with UID: ${firebaseUser.uid}")

            var photoDownloadUrl: String? = null

            // 2. Upload profile picture if it exists.
            if (profilePicture != null) {
                println("AuthService: Profile picture provided, attempting upload...")
                val storageRef = storage.reference("profile_pictures/${firebaseUser.uid}.jpg")

                // --- DEFINITIVE FIX: Use runCatching to safely execute the suspend function ---
                runCatching {
                    // This is your custom expect/actual suspend function.
                    storageRef.upload(profilePicture)

                    // This code will only run if the upload above SUCCEEDS.
                    println("AuthService: Profile picture uploaded successfully.")
                    photoDownloadUrl = storageRef.getDownloadUrl()
                    println("AuthService: Got photo download URL: $photoDownloadUrl")

                }.onFailure { exception ->
                    // This block will reliably execute if ANY exception occurs during upload.
                    println("AuthService: CRITICAL FAILURE during upload: ${exception.message}")
                    // Optional: Decide if you want to stop the user creation process here.
                    // For now, we will log it and continue without a profile picture.
                }
            }

            // 3. Create the user document in Firestore.
            // This part of the code will now be reached even if the upload fails.
            println("AuthService: Preparing to create user document in Firestore.")
            val userDocument = firestore.collection("users").document(firebaseUser.uid)
            val userData = mutableMapOf<String, Any>(
                "uid" to firebaseUser.uid,
                "username" to username,
                "email" to email,
                "createdAt" to dev.gitlive.firebase.firestore.FieldValue.serverTimestamp
            )
            photoDownloadUrl?.let {
                userData["photoUrl"] = it
            }

            userDocument.set(userData)
            println("AuthService: User document created successfully in Firestore for UID: ${firebaseUser.uid}")

            // 4. Update the central Firebase Auth profile.
            println("AuthService: Initiating update of Firebase Auth profile.")
            firebaseUser.updateProfile(
                displayName = username,
                photoUrl = photoDownloadUrl)
            println("AuthService: User creation process complete for $email.")
        }




    override suspend fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
    }

    override suspend fun signInWithGoogle() {
        performGoogleSignIn()
    }

    override suspend fun signOut() {
        auth.currentUser?.let { user ->
            if (user.isAnonymous) {
                user.delete()
            }
        }
        auth.signOut()
    }
}

private fun FirebaseUser.toUser() = User(
    uid = this.uid,
    displayName = this.displayName,
    email = this.email,
    isAnonymous = this.isAnonymous,
    photoUrl = this.photoURL
)
