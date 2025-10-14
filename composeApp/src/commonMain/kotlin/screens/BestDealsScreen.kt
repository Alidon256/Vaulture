package org.vaulture.com.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.ui.tooling.preview.Preview

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.vaulture.com.presentation.theme.AppTheme
// Assuming your generated resources are here
import vaulture.composeapp.generated.resources.*

// Data class for a destination item
data class DealDestination(
    val id: String,
    val cityName: String,
    val imageRes: DrawableResource, // Using DrawableResource for Compose Multiplatform
    val details: String, // Could be price like "$50" or likes like "3265"
    val isLarge: Boolean = false, // To differentiate the "Rome" card style if needed for text
    val span: Int = 1 // Default span for medium cards
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BestDealsScreen() { // Removed parameter as we'll define data inside

    val deals = listOf(
        DealDestination("1", "Uganda", Res.drawable.uganda, "3265", isLarge = true, span = 2), // Rome takes 2 spans
        DealDestination("2", "South Africa", Res.drawable.kenya, "50$", span = 1),
        DealDestination("3", "Kenya", Res.drawable.egypt, "110$", span = 1),
        DealDestination("4", "Egypt", Res.drawable.nigeria, "4385", span = 2) // London takes 2 spans
    )

    Scaffold(
        containerColor = Color(0xFFE4E4E4), // Light gray background from design
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Find best deals",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp, // Prominent title
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent // Make TopAppBar transparent
                )
            )
        },
        bottomBar = {
            Button(
                onClick = { /* TODO: Handle Get Started click */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp, start = 20.dp, bottom = 24.dp) // Generous padding
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)) // Green button
            ) {
                Text("Get started", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 2 columns for the grid
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 16.dp, end = 16.dp), // Horizontal padding for the grid
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(
                deals,
                key = { _, item -> item.id },
                span = { _, item -> GridItemSpan(item.span) }) { index, destination ->
                DestinationCard(destination)
            }
        }
    }
}

@Composable
fun DestinationCard(destination: DealDestination) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // Adjust height based on whether it's the large "Rome" type card or others
            .height(if (destination.cityName == "Uganda" || destination.cityName == "Egypt") 240.dp else 200.dp)
            .clip(RoundedCornerShape(16.dp)), // Rounded corners for the card
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(destination.imageRes),
                contentDescription = destination.cityName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // Crop to fill bounds
            )
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 400f // Start gradient lower to make top part clearer
                        )
                    )
            )
            // Text content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp), // Padding inside the card
                verticalArrangement = Arrangement.Bottom // Align text to bottom
            ) {
                Text(
                    text = destination.cityName,
                    color = Color.White,
                    fontSize = if (destination.isLarge || destination.cityName == "London") 26.sp else 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = destination.details,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = if (destination.isLarge || destination.cityName == "London") 18.sp else 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Dummy Preview Resources (replace with your actual image resources)
// Ensure you have these drawable resources in your commonMain/resources/drawable folder
// For example: img_rome.jpg, img_paris.jpg, etc.
// If you don't have them yet, the preview might show errors for painterResource.
// You can temporarily use placeholder colors for background until images are added.

@Preview()
@Composable
fun BestDealsScreenPreview() {
    AppTheme{ // Wrap in MaterialTheme for consistent preview
        BestDealsScreen()
    }
}
