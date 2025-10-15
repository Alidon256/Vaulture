package auth

import base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.stateIn
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
