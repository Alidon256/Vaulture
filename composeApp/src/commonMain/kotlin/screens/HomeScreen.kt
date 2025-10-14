/*package org.vaulture.com.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.vaulture.com.presentation.viewmodels.TravelViewModel
import org.vaulture.com.presentation.components.DestinationCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TravelViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vaulture Travel") },
                actions = {
                    IconToggleButton(
                        checked = uiState.showFavoritesOnly,
                        onCheckedChange = { viewModel.showFavoritesOnly(it) }
                    ) {
                        if (uiState.showFavoritesOnly) {
                            Text("All")
                        } else {
                            Text("Favorites")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.destinations.isEmpty()) {
                Text(
                    text = if (uiState.showFavoritesOnly) "No favorites yet" else "No destinations available",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.destinations) { destination ->
                        DestinationCard(
                            destination = destination,
                            onCardClick = { onNavigateToDetail(destination.id) },
                            onFavoriteClick = { viewModel.toggleFavorite(destination.id) }
                        )
                    }
                }
            }
        }
    }
}*/
// Create a new file: HomeScreen.kt in
// composeApp/src/commonMain/kotlin/org/vaulture/com/presentation/screens/

package org.vaulture.com.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Hotel
// For extended icons if needed, ensure you have the dependency
// import androidx.compose.material.icons.extended.* // For example, if specific icons are not in core
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.vaulture.com.presentation.theme.AppTheme
import org.vaulture.com.presentation.viewmodels.TravelViewModel // Assuming you have this
import vaulture.composeapp.generated.resources.* // For your image resources

data class HeroItem(
    val id: String,
    val title: String,
    val description: String,
    val imageRes: DrawableResource
)

data class PopularDestinationItem(
    val id: String,
    val name: String,
    val imageRes: DrawableResource
)

data class SideNavItem(
    val title: String,
    val icon: ImageVector,
    val contentDescription: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TravelViewModel, // Pass your ViewModel
    onNavigateToDetail: (destinationId: String) -> Unit,
    onMenuClick: () -> Unit = {} // For drawer or other menu actions
) {
    val heroItems = listOf(
        HeroItem("japan", "Uganda", "The city setting is stunning with a rich architectural and historical heritage", Res.drawable.kenya), // Replace with actual image
        HeroItem("rome_hero", "Kenya", "Explore ancient ruins and vibrant culture in the eternal city.", Res.drawable.egypt), // Replace
        HeroItem("paris_hero", "Egypt", "Discover the romance and art of the City of Lights.", Res.drawable.uganda) // Replace
    )
    val popularDestinations = listOf(
        PopularDestinationItem("thailand", "Nigeria", Res.drawable.nigeria), // Replace
        PopularDestinationItem("spain", "Rwanda", Res.drawable.spain), // Replace
        PopularDestinationItem("greece", "Algeria", Res.drawable.greece) // Replace
    )
    val recommendedDestinations = listOf(
        PopularDestinationItem("thailand", "Morocco", Res.drawable.greece), // Replace
        PopularDestinationItem("spain", "Ghana", Res.drawable.kenya), // Replace
        PopularDestinationItem("greece", "Algeria", Res.drawable.egypt) // Replace
    )

    val sideNavItems = listOf(
        SideNavItem("Hotels", Icons.Outlined.Hotel, "Hotels"), // Replace with actual icons
        SideNavItem("Flights", Icons.Outlined.Flight, "Flights"), // Replace
        SideNavItem("To do", Icons.Outlined.CalendarToday, "To do") // Replace
    )
    var selectedSideNavItem by remember { mutableStateOf(sideNavItems.firstOrNull()) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(Color.Transparent)
                    .padding(16.dp)
            ) {
                sideNavItems.forEach { item ->
                    SideNavigationItem(
                        item = item,
                        isSelected = item == selectedSideNavItem,
                       onClick = {
                           selectedSideNavItem = item
                           scope.launch {
                               drawerState.close()
                           }
                       }
                    )
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                HomeTopAppBar(onMenuClick = {
                    scope.launch {
                    drawerState.close()
                } })
            },
            // Bottom Navigation is handled by AppNavigation.kt's Scaffold
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
               item {
                   // Hero Section with Pager
                   val pagerState = rememberPagerState { heroItems.size }
                   Box(
                       modifier = Modifier
                           .height(300.dp)
                           .fillMaxWidth()
                           .padding(top = 8.dp)
                   ) {
                       HorizontalPager(
                           state = pagerState,
                           modifier = Modifier.fillMaxSize()
                       ) { pageIndex ->
                           HeroCard(heroItems[pageIndex])
                       }
                       // Page Indicators
                       Row(
                           Modifier
                               .align(Alignment.BottomCenter)
                               .padding(bottom = 16.dp),
                           horizontalArrangement = Arrangement.spacedBy(8.dp)
                       ) {
                           repeat(heroItems.size) { iteration ->
                               val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.LightGray
                               Box(
                                   modifier = Modifier
                                       .clip(CircleShape)
                                       .background(color)
                                       .size(8.dp)
                               )
                           }
                       }
                   }

                   Spacer(modifier = Modifier.height(24.dp))
               }

               item {
                   // Popular Destinations
                   Text(
                       "Popular destination",
                       fontSize = 18.sp,
                       fontWeight = FontWeight.Bold,
                       color = MaterialTheme.colorScheme.onPrimary,
                       modifier = Modifier
                           .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                           .background(Color(0xFF2E7D32), shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                           .padding(horizontal = 12.dp, vertical = 8.dp)
                   )

                   LazyRow(
                       contentPadding = PaddingValues(horizontal = 16.dp),
                       horizontalArrangement = Arrangement.spacedBy(12.dp)
                   ) {
                       items(popularDestinations) { destination ->
                           PopularDestinationCard(destination, onClick = {
                               onNavigateToDetail(destination.id)
                           })
                       }
                   }
                   Spacer(modifier = Modifier.height(16.dp))
               }
                item {
                    // Popular Destinations
                    Text(
                        "Recommended destinations",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                            .background(Color(0xFF2E7D32), shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recommendedDestinations) { destination ->
                            PopularDestinationCard(destination, onClick = {
                                onNavigateToDetail(destination.id)
                            })
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    onMenuClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    TopAppBar(
        title = {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Where are you going?", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp) // Control height of search bar
                    .clip(RoundedCornerShape(25.dp)) // Fully rounded
                    .background(Color.White),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                singleLine = true,
                colors = TextFieldDefaults.colors( // Use new M3 colors
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0A2E36) // Dark teal/blue background
        )
    )
}

@Composable
fun SideNavigationItem(item: SideNavItem, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .width(70.dp) // Fixed width for these items
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = item.icon, // Assuming these are DrawableResources now
            contentDescription = item.contentDescription,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            item.title,
            fontSize = 10.sp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(40.dp)
                    .height(3.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(2.dp))
            )
        }
    }
}


@Composable
fun HeroCard(item: HeroItem) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp), // Slight padding if items are close in pager
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // No shadow if filling space
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = null,
                placeholder = painterResource(item.imageRes),
                error = painterResource(item.imageRes),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                            startY = 300f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(item.title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.description, fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f), maxLines = 2)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { /* TODO: Handle Book Now */ },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)) // Green
                ) {
                    Text("Book now")
                }
            }
        }
    }
}

@Composable
fun PopularDestinationCard(destination: PopularDestinationItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp) // Fixed width for these cards
            .height(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = null,
                placeholder = painterResource(destination.imageRes),
                error = painterResource(destination.imageRes),
                contentDescription = destination.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box( // Simple scrim for text
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                            startY = 150f
                        )
                    )
            )
            Text(
                destination.name,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844) // Typical phone size
@Composable
fun HomeScreenPreview() {
    AppTheme {
        // Dummy ViewModel for preview
        val dummyViewModel = remember { TravelViewModel() }
        HomeScreen(viewModel = dummyViewModel, onNavigateToDetail = {})
    }
}
