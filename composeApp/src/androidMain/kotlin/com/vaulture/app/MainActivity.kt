package com.vaulture.app

import App
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import auth.AuthServiceImpl
import auth.signInWithGoogleIdToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.vaulture.app.R // Make sure R is imported
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.initialize
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    // CRITICAL FIX: Use 'by lazy' to defer initialization until after onCreate()
    // This ensures Firebase.initialize() has been called before Firebase.auth is accessed.
    private val authService by lazy { AuthServiceImpl(Firebase.auth) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // It's recommended to initialize Firebase in your Application class,
        // but for simplicity, we do it here. This must run before 'authService' is used.
        Firebase.initialize(
            applicationContext,
            options = FirebaseOptions(
                applicationId = "1:703611560855:android:4cc14354d3cfccf9555ad2",
                apiKey = "AIzaSyAc29mp06dTHOsyBFoQ4k7N2b_Un4sSHxE",
                projectId = "vaulture256"
            )
        )

        // Register the activity result launcher for the Google Sign-In intent.
        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account.idToken
                    if (idToken != null) {
                        // If we get an ID token, use it to sign in to Firebase.
                        lifecycleScope.launch {
                            try {
                                authService.signInWithGoogleIdToken(idToken)
                            } catch (e: Exception) {
                                showToast("Firebase sign-in failed: ${e.message}")
                            }
                        }
                    } else {
                        showToast("Google sign-in failed: ID token was null.")
                    }
                } catch (e: ApiException) {
                    showToast("Google sign-in failed with status code: ${e.statusCode}")
                }
            } else {
                // This is a normal outcome if the user cancels, so a toast isn't necessary.
                // showToast("Google sign-in cancelled.")
            }
        }

        setContent {
            // Pass the function to launch the sign-in flow into our App composable.
            App(onGoogleSignInRequest = { launchGoogleSignIn() })
        }
    }

    /**
     * Creates and launches the Google Sign-In intent.
     */
    private fun launchGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // Use your server's client ID from google-services.json
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // It's good practice to sign out first to allow the user to pick an account every time.
        googleSignInClient.signOut().addOnCompleteListener {
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
