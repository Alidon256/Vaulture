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
import androidx.compose.material.icons.filled.Menu
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
import data.models.Channel
import data.repository.ForYouSuggestion
import data.repository.HomeRepository
import data.repository.ItemType
import data.repository.SearchableItem
import data.repository.SimpleItem
import data.repository.TravelCategory
import data.repository.TravelStory
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.AppTheme
import viewmodels.TravelViewModel

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
        listOf(
            HeroItem("uganda_hero", "Uganda", "Discover the breathtaking Murchison Falls and diverse wildlife.", "https://images.pexels.com/photos/1660603/pexels-photo-1660603.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            HeroItem("kenya_hero", "Kenya", "Witness the great wildebeest migration in the Maasai Mara.", "https://images.pexels.com/photos/7139704/pexels-photo-7139704.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            HeroItem("egypt_hero", "Egypt", "Explore the ancient pyramids and mysteries of the Pharaohs.", "https://images.unsplash.com/photo-1569949381669-ecf31ae8e613?q=80&w=2670&auto=format&fit=crop"),
            HeroItem("tanzania_hero", "Tanzania", "Ascend Mount Kilimanjaro or relax on the beaches of Zanzibar.", "https://images.pexels.com/photos/1525041/pexels-photo-1525041.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            HeroItem("south_africa_hero", "South Africa", "Experience the vibrant culture of Cape Town and its stunning Table Mountain.", "https://images.pexels.com/photos/2395249/pexels-photo-2395249.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            HeroItem("namibia_hero", "Namibia", "Marvel at the surreal landscapes of Deadvlei and the Fish River Canyon.", "https://images.pexels.com/photos/3514066/pexels-photo-3514066.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            HeroItem("ethiopia_hero", "Ethiopia", "Uncover the rock-hewn churches of Lalibela and ancient history.", "https://images.pexels.com/photos/12832846/pexels-photo-12832846.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            HeroItem("botswana_hero", "Botswana", "Glide through the Okavango Delta in a traditional mokoro.", "https://images.pexels.com/photos/1618526/pexels-photo-1618526.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            HeroItem("zimbabwe_hero", "Zimbabwe", "Feel the thunder of Victoria Falls, one of the Seven Natural Wonders.", "https://images.pexels.com/photos/3354427/pexels-photo-3354427.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            HeroItem("seychelles_hero", "Seychelles", "Relax on pristine white-sand beaches and turquoise waters.", "https://images.pexels.com/photos/1174732/pexels-photo-1174732.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2")
        )
    }

    val popularDestinations = remember {
        listOf(
            PopularDestinationItem("nigeria", "Nigeria", "https://images.pexels.com/photos/18447817/pexels-photo-18447817.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("rwanda", "Rwanda", "https://images.pexels.com/photos/13532393/pexels-photo-13532393.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("algeria", "Algeria", "https://images.pexels.com/photos/14878411/pexels-photo-14878411.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("senegal", "Senegal", "https://images.pexels.com/photos/8083515/pexels-photo-8083515.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("cameroon", "Cameroon", "https://images.pexels.com/photos/7477943/pexels-photo-7477943.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("madagascar", "Madagascar", "https://images.pexels.com/photos/2422588/pexels-photo-2422588.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("mozambique", "Mozambique", "https://images.pexels.com/photos/3225528/pexels-photo-3225528.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("zambia", "Zambia", "https://images.pexels.com/photos/592077/pexels-photo-592077.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("tunisia", "Tunisia", "https://images.pexels.com/photos/1126569/pexels-photo-1126569.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("ivory_coast", "Côte d'Ivoire", "https://images.pexels.com/photos/12716492/pexels-photo-12716492.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2")
        )
    }

    val recommendedDestinations = remember {
        listOf(
            PopularDestinationItem("morocco", "Morocco", "https://images.pexels.com/photos/3531895/pexels-photo-3531895.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("ghana", "Ghana", "https://images.pexels.com/photos/4553109/pexels-photo-4553109.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("libya", "Libya", "https://images.pexels.com/photos/8996220/pexels-photo-8996220.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("sudan", "Sudan", "https://images.pexels.com/photos/8885133/pexels-photo-8885133.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("angola", "Angola", "https://images.pexels.com/photos/12397446/pexels-photo-12397446.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("gabon", "Gabon", "https://images.pexels.com/photos/6636336/pexels-photo-6636336.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("mali", "Mali", "https://images.pexels.com/photos/5472260/pexels-photo-5472260.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("somalia", "Somalia", "https://images.pexels.com/photos/7139704/pexels-photo-7139704.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("togo", "Togo", "https://images.pexels.com/photos/12848979/pexels-photo-12848979.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            PopularDestinationItem("benin", "Benin", "https://images.pexels.com/photos/7793739/pexels-photo-7793739.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2")
        )
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
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    Text(
                        "Vaulture",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    sideNavItems.forEach { item ->
                        SideNavigationItem(
                            item = item,
                            isSelected = item == selectedSideNavItem,
                            onClick = {
                                selectedSideNavItem = item
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                }
            }
        }
    ) {
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
                                Text(
                                    "Popular destination",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                                )
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
                                Text(
                                    "Recommended destinations",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                                )
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(recommendedDestinations) { destination ->
                                        PopularDestinationCard(destination, onClick = { onNavigateToDetail(destination.id) })
                                    }
                                }
                                //Spacer(modifier = Modifier.height(16.dp))
                            }
                            item {
                                VaultureHorizontalDivider()
                            }
                            item {
                                SectionTitle(title = "Just for You")
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
                            // --- 3. NEW "Travel Styles" SECTION ---
                            item {
                                SectionTitle(title = "Explore by Travel Style")
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

                            // --- 4. Popular Destinations (Existing) ---
                            item {
                                SectionTitle(title = "Popular Destinations") // Use the new title composable
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
                                SectionTitle(title = "Featured Stories")
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
                                SectionTitle(title = "Recommended For You")
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



@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    )
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
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 300f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
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
fun ForYouSuggestionCard(suggestion: ForYouSuggestion, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(220.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = suggestion.imageUrl,
                contentDescription = suggestion.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(64.dp).clip(MaterialTheme.shapes.medium)
            )

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    suggestion.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Text(
                    suggestion.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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

