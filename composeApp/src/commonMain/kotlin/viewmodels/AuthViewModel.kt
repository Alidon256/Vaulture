package viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.vaulture.com.data.models.DeviceCodeInfo
import org.vaulture.com.domain.User
import org.vaulture.com.service.FirebaseAuthService

/**
 * The single source of truth for all authentication-related UI state and business logic.
 * This ViewModel is platform-agnostic.
 */
class AuthViewModel(
    private val firebaseAuthService: FirebaseAuthService
) : ViewModel() {

    // --- Authentication State ---
    val authState: StateFlow<User?> = firebaseAuthService.getAuthState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = firebaseAuthService.getCurrentUser()
        )

    val isAuthenticated: StateFlow<Boolean> = authState
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = firebaseAuthService.getCurrentUser() != null
        )

    // --- UI State for Forms ---
    var email by mutableStateOf("")

    var password by mutableStateOf("")

    var confirmPassword by mutableStateOf("")
        private set

    // --- Platform-Specific UI State ---

    // Holds the device code info for the Desktop UI to display
    var deviceCodeInfo by mutableStateOf<DeviceCodeInfo?>(null)
        private set

    // General state for loading indicators and error messages
    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // --- UI Events ---

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        confirmPassword = newConfirmPassword
    }

    fun signUpUser(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Email and password cannot be empty."
            return
        }
        if (password != confirmPassword) {
            errorMessage = "Passwords do not match."
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            val result = firebaseAuthService.createUserWithEmailPassword(email, password)
            result.fold(
                onSuccess = {
                    println("Sign up successful: ${it.email}")
                    onSuccess() // Trigger navigation
                },
                onFailure = {
                    errorMessage = it.message ?: "Sign up failed. Please try again."
                }
            )
            isLoading = false
        }
    }

    fun loginUser(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Email and password cannot be empty."
            return
        }
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            val result = firebaseAuthService.signInWithEmailPassword(email, password)
            result.fold(
                onSuccess = {
                    println("Login successful: ${it.email}")
                    onSuccess()
                },
                onFailure = {
                    errorMessage = it.message ?: "Login failed. Please try again."
                }
            )
            isLoading = false
        }
    }

    /**
     * Initiates the Google Sign-In process. This single function will trigger
     * the correct native flow on Android and the Device Code flow on Desktop.
     */
    fun initiateGoogleSignIn() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            // For Android, this returns null and the UI handles the intent.
            // For Desktop, it returns the device code info for the UI to display.
            val result = firebaseAuthService.signInWithGoogle()
            result.getOrNull()?.let { info ->
                // This block only executes on Desktop when device code info is returned
                deviceCodeInfo = info
            }
            result.onFailure {
                errorMessage = it.message ?: "Google Sign-In failed to start."
                isLoading = false
            }
            // On desktop, isLoading remains true while the user authenticates in the browser.
            // It will be set to false when the authState changes or an error occurs.
        }
    }

    /**
     * Completes the Google Sign-In process on Android after the user has
     * selected their account and the result is received by the UI.
     */
    fun completeGoogleSignInWithToken(idToken: String) {
        viewModelScope.launch {
            val result = firebaseAuthService.signInWithGoogle(idToken)
            result.onFailure { exception ->
                errorMessage = exception.message ?: "Google Sign-In failed."
            }
            isLoading = false
        }
    }

    fun signOutUser() {
        viewModelScope.launch {
            firebaseAuthService.signOut()
            email = ""
            password = ""
            confirmPassword = ""
            errorMessage = null
            deviceCodeInfo = null
        }
    }

    fun clearErrorMessage() {
        errorMessage = null
    }

    fun dismissDeviceCodeDialog() {
        deviceCodeInfo = null
        isLoading = false
    }
}
