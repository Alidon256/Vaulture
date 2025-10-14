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
    onGoogleSignInRequest: (() -> Unit)? = null
) {
    MaterialTheme {
        // Use remember to create a single instance of the ViewModel for the app's lifecycle.
        // In a larger app, you would use a dependency injection framework like Koin.
        val loginViewModel = remember { LoginViewModel(AuthServiceImpl(Firebase.auth)) }
 val isAuthenticated by loginViewModel.authService.isAuthenticated.collectAsState(initial = false)

        Surface {
            Crossfade(targetState = isAuthenticated, label = "AuthScreenCrossfade") { isUserLoggedIn ->
                if (isUserLoggedIn) {
                    HomeScreen(
                        viewModel = loginViewModel,
                        onLogout = {
                            loginViewModel.viewModelScope.launch {
                                loginViewModel.authService.signOut()
                            }
                        }
                    )
                } else {
                    LoginScreen(
                        viewModel = loginViewModel,

                        onGoogleSignInRequest = onGoogleSignInRequest ?: { loginViewModel.onGoogleSignInClick() }
                    )
                }
            }
        }
    }
}
