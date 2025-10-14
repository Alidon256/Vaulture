// Create a new file: TripsScreen.kt
package org.vaulture.com.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.vaulture.com.presentation.theme.AppTheme
import vaulture.composeapp.generated.resources.* // Your resources

data class TripItem(
    val id: String,
    val title: String,
    val imageRes: DrawableResource
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    onNavigateBack: () -> Unit,
    onTripClick: (tripId: String) -> Unit
) {
    val trips = listOf(
        TripItem("Uganda", "Trip to Uganda", Res.drawable.uganda), // Replace
        TripItem("Kenya", "Trip to Kenya", Res.drawable.kenya),
        TripItem("Egypt", "Trip to Egypt", Res.drawable.egypt),
        TripItem("Nigeria", "Trip to Nigeria", Res.drawable.nigeria),
        TripItem("Morocco", "Trip to Morocco", Res.drawable.spain),
        TripItem("Ghana", "Trip to Ghana", Res.drawable.greece)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trips", fontWeight = FontWeight.SemiBold) },
                /*navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },*/
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface, // Or surfaceColorAtElevation
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background // Light gray background as per mockup
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp,end =16.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(trips) { trip ->
                TripCard(tripItem = trip, onClick = { onTripClick(trip.id) })
            }
        }
    }
}

@Composable
fun TripCard(tripItem: TripItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp), // Consistent height for trip cards
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = null,
                placeholder = painterResource(tripItem.imageRes),
                error = painterResource(tripItem.imageRes),
                contentDescription = tripItem.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Gradient overlay for better text visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                            startY = 200f // Adjust gradient start
                        )
                    )
            )
            Text(
                text = tripItem.title,
                style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
@Preview
fun TripsScreenPreview() {
    AppTheme {
        TripsScreen(
            onNavigateBack = {},
            onTripClick = {}
        )
    }

}
