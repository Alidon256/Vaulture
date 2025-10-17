package auth

import base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * Represents the state of the LoginScreen.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val username: String = "",
       val profilePicture: ByteArray? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    // ... inside the LoginUiState data class
    override fun equals(other: Any?): Boolean {
        if (this === other) return true// CORRECTED: Use this::class instead of javaClass for multiplatform compatibility
        if (this::class != other!!::class) return false

        other as LoginUiState
        // ... the rest of the equals function is correct
        if (email != other.email) return false
        if (password != other.password) return false
        if (username != other.username) return false
        if (isLoading != other.isLoading) return false
        if (error != other.error) return false
        if (profilePicture != null) {
            if (other.profilePicture == null) return false
            if (!profilePicture.contentEquals(other.profilePicture)) return false
        } else if (other.profilePicture != null) return false
        return true
    }
// The rest of the file remains the same.


    override fun hashCode(): Int {
        var result = email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + isLoading.hashCode()
        result = 31 * result + (error?.hashCode() ?: 0)
        result = 31 * result + (profilePicture?.contentHashCode() ?: 0)
        return result
    }
}


class LoginViewModel(val authService: AuthService) : BaseViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    val isAuthenticated: StateFlow<Boolean> = authService.isAuthenticated
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        viewModelScope.launch {
            authService.isAuthenticated.collect { isAuthenticated ->
                if (isAuthenticated) {
                    _uiState.update { it.copy(isLoading = false, error = null) }
                }
            }
        }
    }


    fun onProfilePictureChange(bytes: ByteArray) {
        _uiState.update { it.copy(profilePicture = bytes, error = null) }
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
        val state = _uiState.value

        if (state.username.isBlank()) {
            _uiState.update { it.copy(error = "Please enter a username.") }
            return
        }
        if (!isEmailValid(state.email) || state.password.length < 6) {
            _uiState.update { it.copy(error = "Please enter a valid email and a password of at least 6 characters.") }
            return
        }

        // This now passes the ByteArray? correctly
        performAuthAction {
            authService.createUser(
                email = state.email,
                password = state.password,
                username = state.username,
                profilePicture = state.profilePicture
            )
        }
    }
    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, error = null) }
    }

    fun onGoogleSignInClick() {
        performAuthAction { authService.signInWithGoogle() }
    }

    private fun performAuthAction(action: suspend () -> Unit) {
        if (_uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                action()
            } catch (e: Exception) {
                val errorMessage = e.message ?: "An unknown error occurred."
                _uiState.update { it.copy(isLoading = false, error = errorMessage) }
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.isNotBlank() && "@" in email && "." in email
    }
}
