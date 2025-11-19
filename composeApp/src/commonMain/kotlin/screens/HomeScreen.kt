package screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Hotel
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import components.SearchBar
import data.ForYouSuggestion
import data.HomeRepository
import data.ItemType
import data.SimpleItem
import data.TravelCategory
import data.TravelStory
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.AppTheme
import viewmodels.TravelViewModel
import kotlin.time.ExperimentalTime

// Data classes
data class HeroItem(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String
)

data class PopularDestinationItem(
    val id: String,
    val name: String,
    val imageUrl: String
)

data class SideNavItem(
    val title: String,
    val icon: ImageVector,
    val contentDescription: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: TravelViewModel,
    onNavigateToDetail: (String) -> Unit,
    userName: String = "User"
) {
    // --- CORRECTED DATA LISTS WITH DIRECT IMAGE URLS ---
    val heroItems = remember {
        HomeRepository.getHeroItems()
    }

    val popularDestinations = remember {
        HomeRepository.getPopularDestinations()
    }


    val recommendedDestinations = remember {
        HomeRepository.getRecommendedDestinations()
    }


    // --- STATE MANAGEMENT FOR UI AND SEARCH ---
    val sideNavItems = remember {
        listOf(
            SideNavItem("Hotels", Icons.Outlined.Hotel, "Hotels"),
            SideNavItem("Flights", Icons.Outlined.Flight, "Flights"),
            SideNavItem("To do", Icons.Outlined.CalendarToday, "To do")
        )
    }
    var selectedSideNavItem by remember { mutableStateOf<SideNavItem?>(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val travelCategories = remember { HomeRepository.getTravelCategories() }
    val travelStories = remember { HomeRepository.getTravelStories() }
    val forYouSuggestions = remember { HomeRepository.getForYouSuggestions() }

    // State for the SearchBar
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    // Replace with actual data from your ViewModel/Repository
    val suggestions = remember(searchQuery) {
        if (searchQuery.isNotBlank()) {
            (heroItems.map { SimpleItem(it.title,  ItemType.SONG) } +
                    popularDestinations.map { SimpleItem(it.name,  ItemType.PLAYLIST) })
                .filter { it.title.contains(searchQuery, ignoreCase = true) }
        } else {
            emptyList()
        }
    }
    val recentSearches = remember {
        listOf("Kenya", "Egypt", "Waterfalls")
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.widthIn(max = 280.dp) // --- MODERN UI: Constrain the width
            ) {
                // --- MODERN UI: Add a profile header ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                        contentDescription = "User Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Anthony",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "anthony@vaulture.com",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }


                Spacer(Modifier.height(16.dp))
                sideNavItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.title) },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        selected = item == selectedSideNavItem,
                        onClick = {
                            selectedSideNavItem = item
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp) // Add padding to items
                    )
                }

                // --- Pushes Logout to the bottom ---
                Spacer(Modifier.weight(1f))


                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout"
                        )
                    },
                    selected = false,
                    onClick = {
                        // TODO: Implement actual logout logic
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    )
    {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideScreen = this.maxWidth > 600.dp

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    HomeTopAppBar(
                        isSearchExpanded = isSearchExpanded,
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                },
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                ) {

                    AnimatedVisibility(
                        visible = !isSearchExpanded,
                        enter = fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 150)),
                        exit = fadeOut(animationSpec = tween(durationMillis = 150))
                    ) {
                        val contentPadding = if (isWideScreen) 64.dp else 0.dp
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = contentPadding)
                        ) {
                            item{
                                SearchBar(
                                    query = searchQuery,
                                    onQueryChange = { searchQuery = it },
                                    onSearch = {
                                        println("Searched for: $it")
                                        isSearchExpanded = false
                                    },
                                    isExpanded = isSearchExpanded,
                                    onToggleExpanded = { isSearchExpanded = !isSearchExpanded },
                                    placeholderText = "Search destinations...",
                                    suggestions = suggestions,
                                    onSuggestionClick = { item ->
                                        onNavigateToDetail(item.title)
                                        isSearchExpanded = false
                                    },
                                    recentSearches = recentSearches,
                                    onRecentSearchClick = { recent ->
                                        searchQuery = recent
                                        println("Searched for: $recent")
                                        isSearchExpanded = false
                                    }
                                )
                            }
                            item {
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
                                //Spacer(modifier = Modifier.height(24.dp))
                            }
                            item {
                                VaultureHorizontalDivider()
                            }

                            item {
                                SectionTitle(title = "Popular Now",onSeeMoreClick = {})
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(popularDestinations) { destination ->
                                        PopularDestinationCard(destination, onClick = { onNavigateToDetail(destination.id) })
                                    }
                                }
                                //Spacer(modifier = Modifier.height(24.dp))
                            }
                            item {
                                VaultureHorizontalDivider()
                            }

                            item {
                                SectionTitle(title = "Just for You",onSeeMoreClick = {})
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(forYouSuggestions) { suggestion ->
                                        ForYouSuggestionCard(suggestion, onClick = { /* TODO */ })
                                    }
                                }
                                // Spacer(modifier = Modifier.height(32.dp))
                            }
                            item {
                                VaultureHorizontalDivider()
                            }

                            item {
                                SectionTitle(title = "Explore by Travel Style",onSeeMoreClick = {})
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    items(travelCategories) { category ->
                                        TravelCategoryCard(category, onClick = { /* TODO: Navigate to category screen */ })
                                    }
                                }
                                //Spacer(modifier = Modifier.height(32.dp))
                            }
                            item {
                                VaultureHorizontalDivider()
                            }


                            item {
                                SectionTitle(title = "Popular Destinations",onSeeMoreClick = {}) // Use the new title composable
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(popularDestinations) { destination ->
                                        PopularDestinationCard(destination, onClick = { onNavigateToDetail(destination.id) })
                                    }
                                }
                                //Spacer(modifier = Modifier.height(32.dp))
                            }

                            item {
                                VaultureHorizontalDivider()
                            }
                            // --- 5. NEW "Featured Stories" SECTION ---
                            item {
                                SectionTitle(title = "Featured Stories",onSeeMoreClick = {})
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(travelStories) { story ->
                                        TravelStoryCard(story, onClick = { /* TODO */ })
                                    }
                                }
                                //Spacer(modifier = Modifier.height(32.dp))
                            }

                            item {
                                VaultureHorizontalDivider()
                            }
                            // --- 6. Recommended Destinations (Existing) ---
                            item {
                                SectionTitle(title = "Recommended For You", onSeeMoreClick = {})
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(recommendedDestinations) { destination ->
                                        PopularDestinationCard(destination, onClick = { onNavigateToDetail(destination.id) })
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(isSearchExpanded: Boolean, onMenuClick: () -> Unit) {
    AnimatedVisibility(
        visible = !isSearchExpanded,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        TopAppBar(
            title = { Text("Vaulture Travel") },
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )
    }
}

@Composable
fun HeroCard(item: HeroItem) {
    Card(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 150f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(top= 16.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
            ) {
                Text(item.title, style = MaterialTheme.typography.headlineMedium.copy(color = Color.White))
                Text(item.description, style = MaterialTheme.typography.bodyMedium.copy(color = Color.White), maxLines = 2)
            }
        }
    }
}

@Composable
fun PopularDestinationCard(item: PopularDestinationItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 150f
                        )
                    )
            )
            Text(
                text = item.name,
                modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SideNavigationItem(item: SideNavItem, isSelected: Boolean, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(item.title) },
        icon = { Icon(item.icon, contentDescription = item.contentDescription) },
        selected = isSelected,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}
@Composable
fun VaultureHorizontalDivider(){
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp) // Dynamic padding
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)),
        thickness = 1.dp
    )
}
@Composable
fun TravelCategoryCard(category: TravelCategory, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .size(110.dp)
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        AsyncImage(
            model = category.imageUrl,
            contentDescription = "Channel ${category.name}",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentScale = ContentScale.Crop
        )
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, end = 4.dp, start = 4.dp)
        )
    }
}


@Composable
fun TravelStoryCard(story: TravelStory, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(280.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)
                    )
                )
            ),
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            AsyncImage(
                model = story.coverImageUrl,
                contentDescription = story.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
            Column(Modifier.padding(16.dp)) {
                Text(
                    story.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = story.author.avatarUrl,
                        contentDescription = story.author.name,
                        modifier = Modifier.size(24.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "${story.author.name} • ${story.durationInMins} min read",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}


@Composable
fun ForYouSuggestionCard(item: ForYouSuggestion, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(280.dp).height(280.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        startY = 150f
                    )
                )
            )
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                Text(item.title, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(item.subtitle, color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${item.rating} (${item.reviews} reviews)", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                }
            }
        }
    }
}
@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier, onSeeMoreClick: () -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        TextButton(onClick = onSeeMoreClick) {
            Text("See more", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        }
    }
}
@OptIn(ExperimentalTime::class)
@Composable
fun Greeting(name: String?) {
    val hour = remember { kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour }
    val greetingText = when (hour) {
        in 0..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
        Text(greetingText, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (!name.isNullOrBlank()) {
            Text(name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}
@Composable
private fun HomeDrawerContent(
    userName: String,
    sideNavItems: List<SideNavItem>,
    onNavItemClick: (SideNavItem) -> Unit,
    onLogoutClick: () -> Unit
) {
    var selectedSideNavItem by remember { mutableStateOf<SideNavItem?>(null) }

    ModalDrawerSheet(modifier = Modifier.widthIn(max = 280.dp)) {
        // Profile Header
        Column(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
                contentDescription = "User Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp).clip(CircleShape).border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(userName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("anthony@vaulture.com", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Navigation Items
        sideNavItems.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.title) },
                icon = { Icon(item.icon, contentDescription = item.title) },
                selected = item == selectedSideNavItem,
                onClick = {
                    selectedSideNavItem = item
                    onNavItemClick(item)
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Spacer(Modifier.weight(1f)) // Pushes logout to the bottom

        // Logout
        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
        NavigationDrawerItem(
            label = { Text("Logout") },
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout") },
            selected = false,
            onClick = onLogoutClick,
            modifier = Modifier.padding(12.dp)
        )
    }
}



@Preview
@Composable
private fun TravelCategoryCardPreview() {
    AppTheme {
        TravelCategoryCard(HomeRepository.getTravelCategories().first(), onClick = {})
    }
}

@Preview
@Composable
private fun TravelStoryCardPreview() {
    AppTheme {
        TravelStoryCard(HomeRepository.getTravelStories().first(), onClick = {})
    }
}

@Preview
@Composable
private fun ForYouSuggestionCardPreview() {
    AppTheme {
        ForYouSuggestionCard(HomeRepository.getForYouSuggestions().first(), onClick = {})
    }
}

