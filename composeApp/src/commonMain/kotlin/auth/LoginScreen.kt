package auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    // The onLoginSuccess parameter has been removed.
    onGoogleSignInRequest: () -> Unit // For Android to trigger the intent
) {
    // The uiState is the only state we need to collect from the ViewModel here.
    val uiState by viewModel.uiState.collectAsState()

    // The navigation logic (navigateToHome, LaunchedEffect) has been removed
    // because App.kt's Crossfade now handles it automatically.

    LoginScreenContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSignInClick = viewModel::onSignInClick,
        onCreateAccountClick = viewModel::onCreateAccountClick,
        onGoogleSignInClick = {
            // This correctly delegates the Google Sign-In click to the platform-specific handler.
            onGoogleSignInRequest()
        }
    )
}

@Composable
private fun LoginScreenContent(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    BoxWithConstraints(
        modifier = modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val maxWidth = this.maxWidth
        val contentModifier = if (maxWidth > 600.dp) Modifier.width(400.dp) else Modifier.fillMaxWidth()

        Column(
            modifier = contentModifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Welcome!", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                isError = uiState.error != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                isError = uiState.error != null,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    onSignInClick()
                }),
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(uiState.error != null) {
                Text(
                    text = uiState.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(16.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = onSignInClick,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("SIGN IN")
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onCreateAccountClick,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("CREATE ACCOUNT")
                }
                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = onGoogleSignInClick,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    // You can add the Google logo here as an Icon
                    Text("SIGN IN WITH GOOGLE")
                }
            }
        }
    }
}
