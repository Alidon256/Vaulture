package screens

import androidx.compose.foundation.background
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

/*
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import components.SearchBar // Import the SearchBar component
import data.Member
import data.Space
import data.SpaceRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.AppTheme
import vaulture.composeapp.generated.resources.Res
import vaulture.composeapp.generated.resources.logo
import viewmodels.SpaceViewModel

enum class SpaceFilter {
    Spaces, Memories, Chats
}

@Composable
fun SpacesScreen(
    selectedFilter: SpaceFilter,
    onFilterSelected: (SpaceFilter) -> Unit,
    onSpaceClick: (spaceId: String) -> Unit,
    onCreateSpaceClick: () -> Unit,
    onAddStoryClick: () -> Unit,
    onChatClick: (chatId: String) -> Unit
){

    BoxWithConstraints(modifier = Modifier.fillMaxSize()){
        val isExpanded = maxWidth > 920.dp
        
        AnimatedContent(
            targetState = isExpanded,
            transitionSpec = {fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())},
            label = "ResponsiveSpacesLayout"
        ){expanded ->
            if (expanded){
                SpaceScreenExpandable(
                    selectedFilter = selectedFilter,
                    onFilterSelected = onFilterSelected,
                    onSpaceClick = onSpaceClick,
                    onCreateSpaceClick = onCreateSpaceClick,
                    onAddStoryClick = onAddStoryClick,
                    onChatClick = onChatClick
                )
            }else {
                SpacesScreenCompat(
                    selectedFilter = selectedFilter,
                    onFilterSelected = onFilterSelected,
                    onSpaceClick = onSpaceClick,
                    onCreateSpaceClick = onCreateSpaceClick,
                    onAddStoryClick = onAddStoryClick,
                    onChatClick = onChatClick
                )
            }
        }
    }
}
@Composable
fun SpaceScreenExpandable(
    selectedFilter: SpaceFilter,
    onFilterSelected: (SpaceFilter) -> Unit,
    onSpaceClick: (spaceId: String) -> Unit,
    onCreateSpaceClick: () -> Unit,
    onAddStoryClick: () -> Unit,
    onChatClick: (chatId: String) -> Unit
){
    var selectedRailItem by remember { mutableStateOf("Spaces") }
    val spaceViewModel = remember {
        SpaceViewModel(
            firestore = Firebase.firestore,
            storage = Firebase.storage,
            auth = Firebase.auth
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }


    Row(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ){
        NavigationRail(
            header = {
                Icon(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "Vaulture Logo",
                    modifier = Modifier.padding(top = 16.dp, bottom = 48.dp).size(70.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .padding(end = 8.dp)
                .fillMaxHeight()
                .border(width = 1.dp, color = MaterialTheme.colorScheme.surface)
        ) {
            NavigationRailItem(
                selected = selectedRailItem == "Spaces", onClick = { selectedRailItem = "Spaces" },
                icon = { Icon(if (selectedRailItem == "Spaces") Icons.Filled.People else Icons.Outlined.People, null) },
                label = { Text("Spaces") }
            )
            NavigationRailItem(
                selected = selectedRailItem == "Wallet", onClick = { selectedRailItem = "Wallet" },
                icon = { Icon(if (selectedRailItem == "Wallet") Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet, null) },
                label = { Text("Wallet") }
            )
            NavigationRailItem(
                selected = selectedRailItem == "Docs", onClick = { selectedRailItem = "Docs" },
                icon = { Icon(if (selectedRailItem == "Docs") Icons.Filled.Description else Icons.Outlined.Description, null) },
                label = { Text("Documents") }
            )
            NavigationRailItem(
                selected = selectedRailItem == "Settings", onClick = { selectedRailItem = "Settings" },
                icon = { Icon(if (selectedRailItem == "Settings") Icons.Filled.Settings else Icons.Outlined.Settings, null) },
                label = { Text("Settings") }
            )
            Spacer(Modifier.weight(1f))
            UpgradeToProCard(modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp))
        }
        HorizontalDivider(Modifier.fillMaxHeight().width(1.dp))

        Scaffold(
            topBar = {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = {},
                        isExpanded = isSearchExpanded,
                        onToggleExpanded = { isSearchExpanded = !isSearchExpanded },
                        placeholderText = "Search...",
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    if (!isSearchExpanded) {
                        FilterTabBar(
                            selectedFilter = selectedFilter, // Use the parameter
                            onFilterSelected = onFilterSelected // Use the parameter
                        )
                    }
                }
            },
            floatingActionButton = {
                // --- UPDATED: Conditional FAB based on selected tab ---
                if (!isSearchExpanded) {
                    when (selectedFilter) {
                        SpaceFilter.Spaces -> {
                            FloatingActionButton(
                                onClick = onCreateSpaceClick,
                                shape = CircleShape,
                                containerColor = MaterialTheme.colorScheme.primary
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Create Space")
                            }
                        }
                        SpaceFilter.Memories -> {
                            FloatingActionButton(
                                onClick = onAddStoryClick, // Use the new callback
                                shape = CircleShape,
                                containerColor = MaterialTheme.colorScheme.primary
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Add Story")
                            }
                        }
                        SpaceFilter.Chats -> {
                            // No FAB for chats, or you can add one for new messages
                        }
                    }
                }
            },
            modifier = Modifier.weight(1f)
        ) { paddingValues ->
            if (isSearchExpanded) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Search results for \"$searchQuery\" will appear here.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                AnimatedContent(
                    targetState = selectedFilter,
                    modifier = Modifier.padding(paddingValues),
                    transitionSpec = {
                        if (targetState.ordinal > initialState.ordinal) {
                            (slideInHorizontally { height -> height } + fadeIn()).togetherWith(slideOutHorizontally { height -> -height } + fadeOut())
                        } else {
                            (slideInHorizontally { height -> -height } + fadeIn()).togetherWith(slideOutHorizontally { height -> height } + fadeOut())
                        }.using(SizeTransform(clip = false))
                    },
                    label = "ScreenContent"
                ) { filter ->
                    when (filter) {
                        SpaceFilter.Spaces -> SpacesListContent(onSpaceClick = onSpaceClick)
                        SpaceFilter.Memories -> MemoriesScreen(
                            modifier = Modifier.fillMaxSize(),
                            viewModel = spaceViewModel // Pass the ViewModel
                        )
                        SpaceFilter.Chats -> ChatsScreen(
                            modifier = Modifier.fillMaxSize(),
                            onChatClick = onChatClick
                        )
                    }
                }
            }
        }

        HorizontalDivider(Modifier.fillMaxHeight().width(1.dp))
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(320.dp)
                .background(MaterialTheme.colorScheme.background)
                .border(width = 1.dp, color = MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            WalletCard(balance = "5,573.46")
            TrustedContactsSection()
            DocumentsSection()
            Spacer(Modifier.weight(1f))
            SignOutButton(onSignOut = {})
        }
    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpacesScreenCompat(
    selectedFilter: SpaceFilter,
    onFilterSelected: (SpaceFilter) -> Unit,
    onSpaceClick: (spaceId: String) -> Unit,
    onCreateSpaceClick: () -> Unit,
    onAddStoryClick: () -> Unit,
    onChatClick: (chatId: String) -> Unit
) {
     val spaceViewModel = remember {
        SpaceViewModel(
            firestore = Firebase.firestore,
            storage = Firebase.storage,
            auth = Firebase.auth
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {},
                    isExpanded = isSearchExpanded,
                    onToggleExpanded = { isSearchExpanded = !isSearchExpanded },
                    placeholderText = "Search...",
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                if (!isSearchExpanded) {
                    FilterTabBar(
                        selectedFilter = selectedFilter, // Use the parameter
                        onFilterSelected = onFilterSelected // Use the parameter
                    )
                }
            }
        },
        floatingActionButton = {
            // --- UPDATED: Conditional FAB based on selected tab ---
            if (!isSearchExpanded) {
                when (selectedFilter) {
                    SpaceFilter.Spaces -> {
                        FloatingActionButton(
                            onClick = onCreateSpaceClick,
                            shape = CircleShape,
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Create Space")
                        }
                    }
                    SpaceFilter.Memories -> {
                        FloatingActionButton(
                            onClick = onAddStoryClick, // Use the new callback
                            shape = CircleShape,
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Add Story")
                        }
                    }
                    SpaceFilter.Chats -> {
                        // No FAB for chats, or you can add one for new messages
                    }
                }
            }
        }
    ) { paddingValues ->
        if (isSearchExpanded) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Search results for \"$searchQuery\" will appear here.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            AnimatedContent(
                targetState = selectedFilter,
                modifier = Modifier.padding(paddingValues),
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        (slideInHorizontally { height -> height } + fadeIn()).togetherWith(slideOutHorizontally { height -> -height } + fadeOut())
                    } else {
                        (slideInHorizontally { height -> -height } + fadeIn()).togetherWith(slideOutHorizontally { height -> height } + fadeOut())
                    }.using(SizeTransform(clip = false))
                },
                label = "ScreenContent"
            ) { filter ->
                when (filter) {
                    SpaceFilter.Spaces -> SpacesListContent(onSpaceClick = onSpaceClick)
                    SpaceFilter.Memories -> MemoriesScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = spaceViewModel // Pass the ViewModel
                    )
                    SpaceFilter.Chats -> ChatsScreen(
                        modifier = Modifier.fillMaxSize(),
                        onChatClick = onChatClick
                    )
                }
            }
        }
    }
}

// ... (Rest of the file remains the same)
@Composable
private fun FilterTabBar(
    selectedFilter: SpaceFilter,
    onFilterSelected: (SpaceFilter) -> Unit
) {
    TabRow(
        modifier = Modifier.padding(horizontal = 8.dp),
        selectedTabIndex = selectedFilter.ordinal,
        containerColor = MaterialTheme.colorScheme.background,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedFilter.ordinal]),
                height = 3.dp,
                color = MaterialTheme.colorScheme.primary
            )
        },
        divider = {}
    ) {
        SpaceFilter.entries.forEach { filter ->
            Tab(
                selected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) },
                text = {
                    Text(
                        text = filter.name,
                        fontWeight = if (filter == selectedFilter) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SpacesListContent(onSpaceClick: (spaceId: String) -> Unit) {
    val spaces = remember { SpaceRepository.getSpaces() }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(spaces, key = { it.id }) { space ->
                SpaceListItem(
                    space = space,
                    onClick = { onSpaceClick(space.id) }
                )
            }
        }
}


@Composable
private fun SpaceListItem(
    space: Space,
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(16.dp))
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
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Text(space.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text(
            space.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MemberAvatarGroup(members = space.members)
            if (space.unreadCount > 0) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) { Text("${space.unreadCount}") }
            }
        }
    }
}


@Composable
private fun MemberAvatarGroup(
    members: List<Member>,
    modifier: Modifier = Modifier,
    maxVisible: Int = 4
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
        val visibleMembers = members.take(maxVisible)
        visibleMembers.forEach { member ->
            AsyncImage(
                model = member.avatarUrl,
                contentDescription = member.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(2.dp) // Creates a border effect
                    .clip(CircleShape)
            )
        }
        if (members.size > maxVisible) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "+${members.size - maxVisible}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

// --- Previews ---

@Preview
@Composable
private fun SpacesScreenPreview() {
    AppTheme {
        SpacesScreen(
            onSpaceClick = {},
            onCreateSpaceClick = {},
            onAddStoryClick = {},
            onChatClick = {},
            onFilterSelected = {},
            selectedFilter = SpaceFilter.Spaces
        )
    }
}

@Preview
@Composable
private fun SpaceListItemPreview() {
    AppTheme {
        SpaceListItem(
            space = SpaceRepository.getSpaces().first(),
            onClick = {}
        )
    }
}*/

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import data.ChatRepository
import data.Space
import org.jetbrains.compose.resources.painterResource
import vaulture.composeapp.generated.resources.Res
import vaulture.composeapp.generated.resources.bg_one
import viewmodels.SpaceViewModel

enum class SpaceFilter {
    Spaces, Memories, Chats
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpacesScreen(
    viewModel: SpaceViewModel, // ViewModel is now passed in
    selectedFilter: SpaceFilter,
    onFilterSelected: (SpaceFilter) -> Unit,
    onSpaceClick: (spaceId: String) -> Unit,
    onCreateSpaceClick: () -> Unit,
    onAddStoryClick: () -> Unit,
    onChatClick: (chatId: String) -> Unit
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                TopAppBar(
                    title = { Text("Community", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
                FilterTabBar(
                    selectedFilter = selectedFilter,
                    onFilterSelected = onFilterSelected
                )
            }
        },
        floatingActionButton = {
            when (selectedFilter) {
                SpaceFilter.Spaces -> {
                    FloatingActionButton(
                        onClick = onCreateSpaceClick,
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create Space")
                    }
                }
                SpaceFilter.Memories -> {
                    FloatingActionButton(
                        onClick = onAddStoryClick,
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Add Story")
                    }
                }
                SpaceFilter.Chats -> { /* No FAB for chats */ }
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = selectedFilter,
            modifier = Modifier.padding(paddingValues),
            transitionSpec = {
                val slideDir = if (targetState.ordinal > initialState.ordinal) 1 else -1
                (slideInHorizontally { it * slideDir } + fadeIn())
                    .togetherWith(slideOutHorizontally { -it * slideDir } + fadeOut())
                    .using(SizeTransform(clip = false))
            },
            label = "ScreenContent"
        ) { filter ->
            when (filter) {
                SpaceFilter.Spaces -> SpacesListContent(
                    viewModel = viewModel,
                    onSpaceClick = onSpaceClick
                )
                SpaceFilter.Memories -> MemoriesScreen(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = viewModel
                )
                SpaceFilter.Chats -> ChatsScreen(
                    modifier = Modifier.fillMaxSize(),
                    onChatClick = onChatClick
                )
            }
        }
    }
}

@Composable
fun SpacesListContent(viewModel: SpaceViewModel, onSpaceClick: (String) -> Unit) {
    val spaces by viewModel.spaces.collectAsState()
    val isLoading by viewModel.isLoadingSpaces.collectAsState()

    if (isLoading && spaces.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(spaces, key = { it.id }) { space ->
                SpaceCard(space = space, onClick = { onSpaceClick(space.id) })
            }
        }
    }
}

@Composable
private fun SpaceCard(space: Space, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(220.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = space.coverImageUrl,
                contentDescription = space.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = painterResource(Res.drawable.bg_one) // Fallback image
            )
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(0.8f)),
                        startY = 250f
                    )
                )
            )
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = space.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${space.memberIds.size} members",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun FilterTabBar(
    selectedFilter: SpaceFilter,
    onFilterSelected: (SpaceFilter) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedFilter.ordinal,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedFilter.ordinal]),
                height = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        Tab(
            selected = selectedFilter == SpaceFilter.Spaces,
            onClick = { onFilterSelected(SpaceFilter.Spaces) },
            icon = { Icon(Icons.Outlined.People, null) },
            text = { Text("Spaces") }
        )
        Tab(
            selected = selectedFilter == SpaceFilter.Memories,
            onClick = { onFilterSelected(SpaceFilter.Memories) },
            icon = { Icon(Icons.Outlined.PhotoLibrary, null) },
            text = { Text("Memories") }
        )
        Tab(
            selected = selectedFilter == SpaceFilter.Chats,
            onClick = { onFilterSelected(SpaceFilter.Chats) },
            icon = { Icon(Icons.Outlined.Chat, null) },
            text = { Text("Chats") }
        )
    }
}
