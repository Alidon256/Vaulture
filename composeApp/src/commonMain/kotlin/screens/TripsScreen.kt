package screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
    val filters = remember { TripRepository.getTripFilterCategories() }
    var selectedFilter by remember { mutableStateOf("All") }
    val trips by remember(selectedFilter) {
        mutableStateOf(TripRepository.getTripsByFilter(selectedFilter))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vaulture Trips", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            FilterChipGroup(
                filters = filters,
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val columns = if (maxWidth > 600.dp) 4 else 2
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(columns),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    itemsIndexed(trips, key = { _, trip -> trip.id }) { index, trip ->
                        androidx.compose.animation.AnimatedVisibility(
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
}

@Composable
fun FilterChipGroup(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal =16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter) },
                leadingIcon = if (filter == selectedFilter) {
                    { Icon(Icons.Default.Done, contentDescription = "Selected") }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
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
