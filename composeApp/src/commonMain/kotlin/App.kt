import androidx.compose.animation.Crossfade
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import auth.AuthServiceImpl
import auth.LoginScreen
import auth.LoginViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    // This will be provided by MainActivity on Android to launch the native flow
    onGoogleSignInRequest: (() -> Unit)? = null
) {
    MaterialTheme {
        // Use remember to create a single instance of the ViewModel for the app's lifecycle.
        // In a larger app, you would use a dependency injection framework like Koin.
        val loginViewModel = remember { LoginViewModel(AuthServiceImpl(Firebase.auth)) }

        // This state automatically observes the auth flow and determines which screen to show.
        val isAuthenticated by loginViewModel.authService.isAuthenticated.collectAsState(initial = false)

        Surface {
            // Crossfade provides a smooth transition between the Login and Home screens.
            Crossfade(targetState = isAuthenticated, label = "AuthScreenCrossfade") { isUserLoggedIn ->
                if (isUserLoggedIn) {
                    HomeScreen(
                        viewModel = loginViewModel,
                        onLogout = {
                            // Use the ViewModel's scope to launch the sign-out coroutine
                            loginViewModel.viewModelScope.launch {
                                loginViewModel.authService.signOut()
                            }
                        }
                    )
                } else {
                    LoginScreen(
                        viewModel = loginViewModel,
                        // onLoginSuccess is no longer needed as the Crossfade handles navigation.
                        onGoogleSignInRequest = onGoogleSignInRequest ?: { loginViewModel.onGoogleSignInClick() }
                    )
                }
            }
        }
    }
}
