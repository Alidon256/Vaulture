// Create a new file: HotelDetailScreen.kt
package screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.LocalParking // Placeholder for Digital TV
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets // Placeholder for Pet
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.AppTheme
import vaulture.composeapp.generated.resources.* // Your resources

data class Amenity(val name: String, val icon: ImageVector)
data class GalleryImage(val id: String, val imageRes: DrawableResource)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailScreen(
    onNavigateBack: () -> Unit,
    onBookNow: () -> Unit
) {
    val amenities = listOf(
        Amenity("Wifi", Icons.Filled.Wifi),
        Amenity("Coffee", Icons.Filled.Coffee),
        Amenity("Pool", Icons.Filled.Pool),
        Amenity("Pet", Icons.Filled.Pets), // Replace with actual Pet icon
        Amenity("Digital TV", Icons.Filled.LocalParking) // Replace
    )
    val galleryImages = listOf(
        GalleryImage("g1", Res.drawable.ic_hotels), // Replace with actual gallery images
        GalleryImage("g2", Res.drawable.ic_hotel1),
        GalleryImage("g3", Res.drawable.ic_hotel2)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onBookNow,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                text = { Text("Book Now", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Outlined.Check, "Book") }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                // Do not apply top padding from scaffold if TopAppBar is transparent and overlays content
                // .padding(top = paddingValues.calculateTopPadding())
                .padding(paddingValues) // Space for FAB
        ) {
            // 1. Hero Image Section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp) // Adjust height as needed
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_hotel1), // Replace
                        contentDescription = "Lake Victoria Resort Hotel",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Optional: Gradient overlay for text readability at the bottom of hero
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                                    startY = 400f // Start gradient lower
                                )
                            )
                    )
                    // Hotel Name and Location overlaid (optional position)
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            "Lake Victoria Resort",
                            style = MaterialTheme.typography.headlineMedium.copy(color = Color.White, fontWeight = FontWeight.Bold),
                        )
                        Text(
                            "Kampala, Central Region, Uganda",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.9f))
                        )
                    }
                }
            }

            // 2. Info Section (Price, Dates, Reviews)
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "UGX 450,000",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                "15 Dec - 22 Dec, 2 guests",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = "Rating", tint = Color(0xFFFFC107))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "4.9",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                " (54 Reviews)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                }
            }

            // 3. Amenities Section
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("Amenities", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(amenities.take(5)) { amenity -> // Show first 5
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
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    amenity.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        if (amenities.size > 5) {
                            item {
                                TextButton(onClick = { /* TODO: Show all amenities */ }) {
                                    Text("See more")
                                }
                            }
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                }
            }

            // 4. Description Section
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Nestled on the shores of Lake Victoria, in the heart of Uganda's bustling capital. Traditional Ugandan artwork and modern amenities blend seamlessly throughout the property, creating a warm and authentic East African atmosphere with stunning lake views.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                }
            }

            // 5. Map Section
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("Location", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    // Placeholder for MapView - In a real app, use Google Maps SDK or similar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        // Simple map placeholder
                        Image(
                            painter = painterResource(Res.drawable.maps),
                            contentDescription = "Map Location",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Text("Map View Placeholder", style = MaterialTheme.typography.bodySmall)
                    }
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                }
            }

            // 6. Gallery Section
            item {
                Column(modifier = Modifier.padding(start = 16.dp)) { // Start padding for title
                    Text("Gallery", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        contentPadding = PaddingValues(end = 16.dp), // End padding for last item
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(galleryImages) { galleryImage ->
                          Box{
                              AsyncImage(
                                  model = null,
                                  placeholder = painterResource(galleryImage.imageRes),
                                  contentDescription = "Gallery image ${galleryImage.id}",
                                  contentScale = ContentScale.Crop,
                                  modifier = Modifier
                                      .size(120.dp, 160.dp) // Adjust size as needed
                                      .clip(RoundedCornerShape(12.dp))
                              )
                          }
                        }
                    }
                }
            }
            // Spacer for bottom content padding / FAB
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
@Preview()
fun HotelDetailsScreenPreview() {
    AppTheme {
        HotelDetailScreen(
            onNavigateBack = {},
            onBookNow = {}
        )
    }

}
