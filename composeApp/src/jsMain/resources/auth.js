// This function will be accessible globally on the `window` object.
function performGoogleSignIn(auth) {
    // We dynamically import the necessary Firebase auth functions.
    // This is the standard way in modern JavaScript.
    return import('/node_modules/firebase/auth.js')
        .then(authModule => {
            const provider = new authModule.GoogleAuthProvider();
            // Call the signInWithPopup function and return the resulting Promise.
            return authModule.signInWithPopup(auth, provider);
        })
        .catch(error => {
            console.error("Google Sign-In failed:", error);
            // Propagate the error so Kotlin's .catch can handle it if needed.
            return Promise.reject(error);
        });
}
