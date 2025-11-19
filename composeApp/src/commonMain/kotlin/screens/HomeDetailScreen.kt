package screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.AppTheme
import vaulture.composeapp.generated.resources.*

// --- Production-Ready Data Models with Mockup Data ---

data class HotelAmenity(val name: String, val icon: ImageVector)
data class HotelGalleryImage(val id: String, val imageRes: String)
data class HotelReview(val author: String, val rating: Float, val comment: String, val authorImage: DrawableResource)

data class HotelDetails(
    val name: String,
    val location: String,
    val heroImage: String,
    val pricePerNight: String,
    val bookingDates: String,
    val overallRating: Float,
    val reviewCount: Int,
    val description: String,
    val mapImage: DrawableResource,
    val amenities: List<HotelAmenity>,
    val gallery: List<HotelGalleryImage>,
    val reviews: List<HotelReview>
)

// Mockup data for a realistic preview and production-ready structure
val mockHotelDetails = HotelDetails(
    name = "Lake Victoria Resort",
    location = "Kampala, Central Region, Uganda",
    heroImage = "https://images.pexels.com/photos/261102/pexels-photo-261102.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
    pricePerNight = "UGX 450,000",
    bookingDates = "15 Dec - 22 Dec, 2 guests",
    overallRating = 4.9f,
    reviewCount = 54,
    description = "Nestled on the shores of Lake Victoria, in the heart of Uganda's bustling capital. Traditional Ugandan artwork and modern amenities blend seamlessly throughout the property, creating a warm and authentic East African atmosphere with stunning lake views.",
    mapImage = Res.drawable.maps,
    amenities = listOf(
        HotelAmenity("Wifi", Icons.Default.Wifi),
        HotelAmenity("Pool", Icons.Default.Pool),
        HotelAmenity("Parking", Icons.Default.LocalParking),
        HotelAmenity("Pet Friendly", Icons.Default.Pets),
        HotelAmenity("Restaurant", Icons.Default.Restaurant),
        HotelAmenity("Gym", Icons.Default.FitnessCenter)
    ),
    gallery = listOf(
        HotelGalleryImage("g1", "https://images.pexels.com/photos/271624/pexels-photo-271624.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        HotelGalleryImage("g2", "https://images.pexels.com/photos/1457842/pexels-photo-1457842.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        HotelGalleryImage("g3", "https://images.pexels.com/photos/2598638/pexels-photo-2598638.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        HotelGalleryImage("g4", "https://images.pexels.com/photos/1579253/pexels-photo-1579253.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        HotelGalleryImage("g5", "https://images.pexels.com/photos/338504/pexels-photo-338504.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2")
    ),
    reviews = listOf(
        HotelReview("Alex", 4.8f, "Absolutely stunning views and incredible service.", Res.drawable.profile),
        HotelReview("Maria", 5.0f, "A perfect getaway. The pool area is fantastic!", Res.drawable.profile)
    )
)


// --- Main Composable ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailScreen(
    hotel: HotelDetails = mockHotelDetails, // Use the mockup data
    onNavigateBack: () -> Unit,
    onBookNow: () -> Unit
) {
    val lazyListState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AnimatedCollapsingToolbar(
                hotel = hotel,
                lazyListState = lazyListState,
                onNavigateBack = onNavigateBack
            )
        },
        floatingActionButton = {
            // Animate the FAB to appear with the content
            AnimatedVisibility(
                visible = !lazyListState.isScrollingUp(),
                enter = fadeIn(animationSpec = tween(200, 200)),
                exit = fadeOut(animationSpec = tween(200))
            ) {
                ExtendedFloatingActionButton(
                    onClick = onBookNow,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    text = { Text("Book Now", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Check, "Book Now") },
                    modifier = Modifier.padding(16.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp) // Space for content below FAB
        ) {
            // 1. Hero Image with Parallax Effect
            item {
                ParallaxHeroImage(hotel.heroImage, lazyListState)
            }

            // 2. Hotel Info Section with Animated Fade-In
            item {
                AnimatedContentEntry {
                    HotelPrimaryInfo(hotel)
                }
            }

            // 3. Amenities Section
            item {
                AnimatedContentEntry(delay = 100) {
                    DetailSection(title = "Amenities") {
                        AmenitiesGrid(hotel.amenities)
                    }
                }
            }

            // 4. Description Section
            item {
                AnimatedContentEntry(delay = 200) {
                    DetailSection(title = "Description") {
                        Text(
                            text = hotel.description,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 5. Gallery Section
            item {
                AnimatedContentEntry(delay = 300) {
                    DetailSection(title = "Gallery", showDivider = false) {
                        GalleryRow(hotel.gallery)
                    }
                }
            }

            // 6. Map Section
            item {
                AnimatedContentEntry(delay = 400) {
                    DetailSection(title = "Location") {
                        Image(
                            painter = painterResource(hotel.mapImage),
                            contentDescription = "Map Location",
                            modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}


// --- UI Components ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimatedCollapsingToolbar(
    hotel: HotelDetails,
    lazyListState: LazyListState,
    onNavigateBack: () -> Unit
) {
    val scrollOffset = remember { derivedStateOf { lazyListState.firstVisibleItemScrollOffset } }
    val firstItemVisible = remember { derivedStateOf { lazyListState.firstVisibleItemIndex == 0 } }

    val toolbarHeight = 64.dp
    val heroImageHeight = 300.dp
    val heroImageHeightPx = with(LocalDensity.current) { heroImageHeight.toPx() }

    // Calculate how much the hero image has been scrolled past
    val collapsedRatio = if (firstItemVisible.value) (scrollOffset.value / heroImageHeightPx).coerceIn(0f, 1f) else 1f

    CenterAlignedTopAppBar(
        title = {
            // The title fades in as the toolbar collapses
            AnimatedVisibility(
                visible = collapsedRatio > 0.8f,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Text(hotel.name, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.3f * (1 - collapsedRatio)), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate back", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = collapsedRatio),
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = Color.White.copy(alpha = 1 - collapsedRatio)
        ),
        modifier = Modifier.height(toolbarHeight)
    )
}

@Composable
private fun ParallaxHeroImage(imageRes: String, lazyListState: LazyListState) {
    val scrollOffset = remember { derivedStateOf { lazyListState.firstVisibleItemScrollOffset } }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .graphicsLayer {
                // Parallax Effect: Image scrolls slower than the list
                if (lazyListState.firstVisibleItemIndex == 0) {
                    translationY = scrollOffset.value * 0.5f
                }
                // Fade out effect as it scrolls
                alpha = 1f - (scrollOffset.value / 600f).coerceIn(0f, 1f)
            }
    ) {
        AsyncImage(
            model = imageRes,
            placeholder = painterResource(Res.drawable.ic_hotel1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Scrim for text readability
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                    startY = 400f
                )
            )
        )
    }
}


@Composable
private fun HotelPrimaryInfo(hotel: HotelDetails) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            hotel.name,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = "Location", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(4.dp))
            Text(
                hotel.location,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    hotel.pricePerNight,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    hotel.bookingDates,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { /* Navigate to reviews */ }) {
                Icon(Icons.Filled.Star, contentDescription = "Rating", tint = Color(0xFFFFC107))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "${hotel.overallRating}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    " (${hotel.reviewCount} Reviews)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        if (showDivider) {
            Divider(modifier = Modifier.padding(bottom = 16.dp))
        }
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}

@Composable
private fun AmenitiesGrid(amenities: List<HotelAmenity>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        amenities.take(5).forEach { amenity ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(64.dp)
            ) {
                Icon(
                    amenity.icon,
                    contentDescription = amenity.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    amenity.name,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun GalleryRow(gallery: List<HotelGalleryImage>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(end = 16.dp)
    ) {
        items(gallery, key = { it.id }) { item ->
            AsyncImage(
                model = item.imageRes,
                placeholder = painterResource(Res.drawable.ic_hotel1),
                contentDescription = "Gallery Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp, 160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { /* TODO: Open fullscreen gallery view */ }
            )
        }
    }
}


// --- Animation Helpers ---

@Composable
private fun AnimatedContentEntry(delay: Int = 0, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 400, delayMillis = 100))
    ) {
        content()
    }
}

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}


@Preview
@Composable
private fun HotelDetailsScreenPreview() {
    AppTheme {
        HotelDetailScreen(
            onNavigateBack = {},
            onBookNow = {}
        )
    }
}
