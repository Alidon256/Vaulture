package screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import data.Trip
import data.TripRepository
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    onTripClick: (tripId: String) -> Unit
) {
    val trips = remember { TripRepository.getTrips() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Trips", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Determine column count based on screen width for responsiveness
            val columns = if (maxWidth > 600.dp) 3 else 2

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp
            ) {
                itemsIndexed(trips) { index, trip ->
                    // Animate each item's entry
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(500, delayMillis = index * 100)) +
                                slideInVertically(
                                    initialOffsetY = { it / 2 },
                                    animationSpec = tween(500, delayMillis = index * 100)
                                )
                    ) {
                        TripCard(trip = trip, onClick = { onTripClick(trip.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun TripCard(trip: Trip, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            AsyncImage(
                model = trip.imageUrl,
                contentDescription = trip.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio((0.6f + kotlin.random.Random.nextFloat() * (1.2f - 0.6f)))
                    //.aspectRatio(0.8f)
            )
            // Gradient overlay for better text visibility
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 200f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = trip.title,
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold),
                )
                Text(
                    text = trip.country,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f)),
                )
            }
        }
    }
}



@Preview
@Composable
fun TripsScreenPreview() {
    AppTheme {
        TripsScreen(onTripClick = {})
    }
}
