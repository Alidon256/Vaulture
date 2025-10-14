import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import auth.LoginViewModel
import auth.User

@Composable
fun HomeScreen(
    viewModel: LoginViewModel,
    onLogout: () -> Unit
) {
    // Collect the currentUser state from the auth service via the ViewModel.
    // The `initial` value of the flow is now properly handled by the auth service.
    val user: User? by viewModel.authService.currentUser.collectAsState(initial = null)

    // A calculated name to display. It intelligently chooses the best available option.
    val greetingName = remember(user) {
        // Prioritize a non-blank display name, fall back to email, then to a generic "User".
        user?.displayName?.takeIf { it.isNotBlank() } ?: user?.email ?: "User"
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome, $greetingName!",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "You are signed in successfully.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(32.dp))

        Button(onClick = onLogout) {
            Text("LOG OUT")
        }
    }
}
