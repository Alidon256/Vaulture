package org.vaulture.com.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import vaulture.composeapp.generated.resources.Res
import vaulture.composeapp.generated.resources.bg_two

@Composable
fun ConnectScreen(
    onLogIn: () -> Unit = {},
    onCreateAccount: () -> Unit = {}
) {
    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Top Section: Text
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp, bottom = 20.dp), // Adjusted top and added bottom padding
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Travel Deeper",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Connect with other travellers",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Curved Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp) // Adjusted height, fine-tune this with the curve
            ) {
                Image(
                    painter = painterResource(Res.drawable.bg_two),
                    contentDescription = "Hikers enjoying a scenic view",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(AsymmetricBottomCurveShapeImproved()) // Using improved shape
                        .background(Color(0xFF1B5E20)), // Dark green from mockup
                    contentScale = ContentScale.Crop
                )
            }

            // Spacer to create significant distance before buttons
            Spacer(modifier = Modifier.height(60.dp)) // Increased spacer

            // Bottom Section with Buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    // .weight(1f) // Removed weight here, using spacers for more explicit control for now
                    .padding(bottom = 40.dp) // Ensure enough bottom padding
            ) {
                Button(
                    onClick = onLogIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Log In",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onCreateAccount,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF2E7D32)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Create account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            // Add a flexible spacer at the bottom to push content up if screen is tall
            // and ensure buttons don't stick to the absolute bottom if not needed.
            Spacer(modifier = Modifier.weight(0.5f))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

// Custom Shape for the Asymmetric Bottom Curve - Improved Version
fun AsymmetricBottomCurveShapeImproved(): Shape {
    return GenericShape { size, _ ->
        val width = size.width
        val height = size.height

        // Define Key Vertical Markers
        val topCurveStart = height * 0.10f // Where the top curve begins on the left
        val bottomCurveEnd = height * 0.85f  // Where the bottom curve ends on the right

        // --- Start at the Bottom-Left Corner (To define the shape in a clockwise manner) ---
        moveTo(0f, height * 0.60f) // Start (A) where the previous S-curve ended on the left

        // --- 1. The Bottom S-Curve (Convex/Concave) ---
        // This is the shape you previously defined, slightly adjusted to fit the new overall shape.
        cubicTo(
            // Control Point 1 (B): Convex/Outward Pull (The lower bump)
            x1 = width * 0.20f, y1 = height * 0.95f,
            // Control Point 2 (C): Concave/Inward Pull (The upper dip before the final rise)
            x2 = width * 0.70f, y2 = height * 0.90f,
            // End Point of the S-Curve on the right edge (D)
            x3 = width, y3 = bottomCurveEnd
        )

        // --- 2. Line Up the Right Side ---
        // Line straight up the right side to where the top curve begins
        lineTo(width, topCurveStart) // E: Start point of the Top Curve on the right

        // --- 3. The Top Concave Curve (Single Quadratic Bezier for a smooth dip) ---
        // This curve defines the white space above the image.

        // Control Point (F): Pulls the curve inward and down to create the concave/scoop
        val topControlX = width * 0.50f
        val topControlY = height * 0.15f

        // End Point of the Top Curve on the left edge (G)
        val topEndX = 0f
        val topEndY = topCurveStart

        // Invert the bottom S-curve and use it for the top curve
        cubicTo(
            x1 = width * 0.70f, y1 = height * 0.20f, // Inverted control point 2
            x2 = width * 0.20f, y2 = height * 0.05f, // Inverted control point 1
            x3 = 0f, y3 = topCurveStart               // End point at the top left
        )
        /*quadraticTo(
            x1 = topControlX, y1 = topControlY, // F: Control point for the top scoop
            x2 = topEndX, y2 = topEndY          // G: End point on the left edge
        )*/

        // --- 4. Line Down the Left Side ---
        // Line straight down the left side back to the start point (0, 0.60*height)
        lineTo(0f, height * 0.60f)

        // Close the path
        close()
    }
}
@Preview()
@Composable
fun ConnectScreenPreview() {
    MaterialTheme {
        ConnectScreen()
    }
}
