package auth

import base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Represents the state of the LoginScreen.
 * Using a single state class makes it easier to manage and observe UI changes.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class LoginViewModel(val authService: AuthService) : BaseViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // This is no longer needed, App.kt handles navigation reactively.
    // private val _navigateToHome = MutableStateFlow(false)
    // val navigateToHome: StateFlow<Boolean> = _navigateToHome.asStateFlow()

    init {
        // This collector now primarily serves to reset the loading state upon successful auth.
        viewModelScope.launch {
            authService.isAuthenticated.collect { isAuthenticated ->
                if (isAuthenticated) {
                    // If auth succeeds (from any method), ensure loading is off and errors are cleared.
                    _uiState.update { it.copy(isLoading = false, error = null) }
                }
            }
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun onSignInClick() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (!isEmailValid(email) || password.isBlank()) {
            _uiState.update { it.copy(error = "Please enter a valid email and password.") }
            return
        }
        performAuthAction { authService.signInWithEmail(email, password) }
    }

    fun onCreateAccountClick() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (!isEmailValid(email) || password.length < 6) {
            _uiState.update { it.copy(error = "Please enter a valid email and a password of at least 6 characters.") }
            return
        }
        performAuthAction { authService.createUser(email, password) }
    }

    fun onGoogleSignInClick() {
        performAuthAction { authService.signInWithGoogle() }
    }

    /**
     * A helper function to encapsulate the common logic for performing an authentication action.
     * This version is robust for both internal (email) and external (Google popup) auth flows.
     */
    private fun performAuthAction(action: suspend () -> Unit) {
        // Prevent multiple auth actions from running at the same time.
        if (_uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                action()
                // On success for all actions, the `isAuthenticated` collector above
                // will handle turning off isLoading.
            } catch (e: Exception) {
                // If ANY error occurs (wrong password, user closes popup, browser blocks popup),
                // we catch it here, update the state, and turn off loading.
                val errorMessage = e.message ?: "An unknown error occurred."
                _uiState.update { it.copy(isLoading = false, error = errorMessage) }
            }
            // CRITICAL FIX: There is NO 'finally' block.
            // The loading state is only ever turned off on a definitive success (in the init collector)
            // or a definitive failure (in the catch block). This prevents the "stuck spinner" issue.
        }
    }

    // onNavigationComplete is no longer needed
    // fun onNavigationComplete() { ... }

    private fun isEmailValid(email: String): Boolean {
        // Basic email validation. For production, consider a more robust regex.
        return email.isNotBlank() && "@" in email && "." in email
    }
}
