package screens

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import auth.AuthService
import auth.User
import coil3.compose.AsyncImage
import data.TripRepository
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.AppTheme
import vaulture.composeapp.generated.resources.*

// Main entry point for the responsive Profile Screen
@Composable
fun ProfileScreen(
    authService: AuthService,
    onSignOut: () -> Unit,
    onTripClick: (String) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isExpanded = maxWidth > 920.dp // Breakpoint for switching to dashboard layout

        AnimatedContent(
            targetState = isExpanded,
            transitionSpec = { fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring()) },
            label = "ResponsiveProfileLayout"
        ) { expanded ->
            if (expanded) {
                ProfileScreenExpanded(authService = authService, onSignOut = onSignOut, onTripClick = onTripClick)
            } else {
                ProfileScreenCompact(authService = authService, onSignOut = onSignOut, onTripClick = onTripClick)
            }
        }
    }
}

// =================================================================================
// Compact Layout (Mobile) - RE-ENGINEERED FOR PERFECT PARALLAX & AVATAR PLACEMENT
// =================================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreenCompact(
    authService: AuthService,
    onSignOut: () -> Unit,
    onTripClick: (String) -> Unit,
    onNavigateToSettings: () -> Unit = {},
) {
    val user by authService.currentUser.collectAsState(null)
    val lazyListState = rememberLazyListState()
    val bannerHeight = 220.dp
    val bannerHeightPx = with(LocalDensity.current) { bannerHeight.toPx() }
    val avatarInitialSize = 120.dp
    val avatarFinalSize = 40.dp

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(bottom = paddingValues.calculateBottomPadding()),
            state = lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // --- Banner & Avatar Layout ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        // This Box is responsible for the banner height + half the avatar height
                        .height(bannerHeight + avatarInitialSize / 2)
                ) {
                    // --- Parallax Banner ---
                    AsyncImage(
                        model = "https://images.pexels.com/photos/1051075/pexels-photo-1051075.jpeg",
                        contentDescription = "Profile banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(bannerHeight)
                            .graphicsLayer {
                                val scrollOffset = if (lazyListState.firstVisibleItemIndex == 0) lazyListState.firstVisibleItemScrollOffset.toFloat() else bannerHeightPx
                                translationY = scrollOffset * 0.5f
                                alpha = 1f - (scrollOffset / bannerHeightPx).coerceIn(0f, 1f)
                            }
                    )
                    Box(modifier = Modifier.matchParentSize().background(Brush.verticalGradient(listOf(Color.Black.copy(0.4f), Color.Transparent), endY = 200f)))

                    // --- Animated Avatar (Correctly Positioned) ---
                    val scrollOffset = if (lazyListState.firstVisibleItemIndex == 0) lazyListState.firstVisibleItemScrollOffset.toFloat() else bannerHeightPx
                    val collapsedPercentage = (scrollOffset / (bannerHeightPx - (avatarInitialSize.value / 2))).coerceIn(0f, 1f)
                    val avatarSize = lerp(avatarInitialSize, avatarFinalSize, collapsedPercentage)
                    // The Y offset starts at the bottom of the banner minus half the avatar's size
                    val avatarY = (bannerHeight - (avatarInitialSize / 2))

                    Box(
                        Modifier
                            .padding(top = avatarY)
                            .size(avatarSize)
                            .align(Alignment.TopCenter)
                            .graphicsLayer {
                                // We don't need to move this Box itself, its content changes size.
                                // It will be covered by the sticky header.
                            }
                    ) {
                        ProfileAvatar(user?.photoUrl, "Profile Picture")
                    }
                }
            }

            // --- Sticky Header (Covers the animated avatar) ---
            stickyHeader {
                val scrollOffset = if (lazyListState.firstVisibleItemIndex == 0) lazyListState.firstVisibleItemScrollOffset.toFloat() else bannerHeightPx

                // Correctly calculate pixel values within a Density scope
                val (collapseStartOffset, isCollapsed) = with(LocalDensity.current) {
                    // Collapse starts when avatar is about to go under the sticky header
                    val start = bannerHeightPx - avatarInitialSize.toPx() / 2 - 8.dp.toPx()
                    val collapsed = lazyListState.firstVisibleItemIndex > 0 || scrollOffset > start
                    start to collapsed // Return both calculated values
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = if(isCollapsed) MaterialTheme.colorScheme.background else Color.Transparent,
                    shadowElevation = if (isCollapsed) 4.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedVisibility(visible = isCollapsed, enter = fadeIn(), exit = fadeOut()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(avatarFinalSize)) {
                                    ProfileAvatar(user?.photoUrl, "Profile Picture")
                                }
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    text = user?.displayName ?: "Tanya Edwards",
                                    style = MaterialTheme.typography.titleLarge,
                                )
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                        }
                    }
                }
            }


            // --- User Info ---
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = user?.displayName ?: "Tanya Edwards",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = user?.email ?: "tanya.edwards@example.com",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(24.dp))
            }

            item {
                TravelStatsRow()
                Spacer(Modifier.height(32.dp))
            }

            // --- FEATURE PARITY: Adding web dashboard cards to mobile ---
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    WalletCard(balance = "5,573.46")
                    UpcomingTripCard(
                        imageUrl = "https://images.pexels.com/photos/1660603/pexels-photo-1660603.jpeg",
                        destination = "Murchison Falls",
                        dateRange = "Oct 28 - Nov 05, 2025"
                    )
                }
                Spacer(Modifier.height(32.dp))
            }

            item {
                ProfileContentTabs(onTripClick, isExpanded = false)
                Spacer(Modifier.height(32.dp))
            }

            item {
                SignOutButton(onSignOut)
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

// =================================================================================
// Expanded Layout (Web/Tablet) - Three-Pane Dashboard
// =================================================================================

@Composable
private fun ProfileScreenExpanded(
    authService: AuthService,
    onSignOut: () -> Unit,
    onTripClick: (String) -> Unit
) {
    val user by authService.currentUser.collectAsState(null)
    var selectedRailItem by remember { mutableStateOf("Profile") }

    Row(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // --- Left Navigation Rail ---
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
                selected = selectedRailItem == "Profile", onClick = { selectedRailItem = "Profile" },
                icon = { Icon(if (selectedRailItem == "Profile") Icons.Filled.Person else Icons.Outlined.Person, null) },
                label = { Text("Profile") }
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

        // --- Center Content Panel ---
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 48.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ProfileAvatar(user?.photoUrl ?: "https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg", "Profile Picture", modifier = Modifier.size(120.dp))
                    Spacer(Modifier.height(16.dp))
                    Text(
                        user?.displayName ?: "Mugumya Ali",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        user?.email ?: "mugumyaali@gmail.com",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(32.dp))
            }

            item {
                TravelStatsRow()
                Spacer(Modifier.height(48.dp))
            }

            item {
                Text(
                    "Upcoming Trips",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, start = 8.dp),
                    textAlign = TextAlign.Start
                )
                UpcomingTripCard(
                    imageUrl = "https://images.pexels.com/photos/1660603/pexels-photo-1660603.jpeg",
                    destination = "Murchison Falls",
                    dateRange = "Oct 28 - Nov 05, 2025"
                )
                Spacer(Modifier.height(48.dp))
            }

            item {
                ProfileContentTabs(onTripClick, isExpanded = true)
            }
        }
        HorizontalDivider(Modifier.fillMaxHeight().width(1.dp))

        // --- Right "Vault" Panel ---
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
            SignOutButton(onSignOut)
        }
    }
}


// =================================================================================
// Reusable & Polished Components
// =================================================================================

@Composable
fun ProfileAvatar(model: String?, contentDescription: String, modifier: Modifier = Modifier) {
    Card(shape = CircleShape, elevation = CardDefaults.cardElevation(4.dp), modifier = modifier) {
        AsyncImage(
            model = model ?: "https://images.pexels.com/photos/1065084/pexels-photo-1065084.jpeg",
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
    }
}

@Composable
private fun TravelStatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem("Countries", "12")
        Divider(modifier = Modifier.height(40.dp).width(1.dp))
        StatItem("Trips Taken", "42")
        Divider(modifier = Modifier.height(40.dp).width(1.dp))
        StatItem("Bucket List", "8")
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContentTabs(onTripClick: (String) -> Unit, isExpanded: Boolean) {
    val tabs = listOf("My Trips", "Photos", "Wishlist")
    var selectedTab by remember { mutableStateOf(tabs.first()) }
    val trips = remember { TripRepository.getTrips().shuffled() }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            tabs.forEach { title ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = tabs.indexOf(title), count = tabs.size),
                    onClick = { selectedTab = title },
                    selected = title == selectedTab,
                    label = { Text(title) }
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = { fadeIn(spring()) togetherWith fadeOut(spring()) },
            label = "ProfileContent"
        ) { tab ->
            when (tab) {
                "My Trips" -> {
                    val columns = if (isExpanded) 3 else 2
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(columns),
                        modifier = Modifier.heightIn(max = 600.dp).padding(horizontal = 16.dp),
                        verticalItemSpacing = 12.dp,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(trips, key = { it.id }) { trip ->
                            TripCard(trip, onClick = { onTripClick(trip.id) })
                        }
                    }
                }
                else -> {
                    Box(Modifier.fillMaxWidth().height(200.dp).padding(horizontal = 16.dp), Alignment.Center) {
                        Text("$tab Content Goes Here", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun SignOutButton(onSignOut: () -> Unit) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        TextButton(onClick = onSignOut) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.width(8.dp))
            Text("Sign Out", color = MaterialTheme.colorScheme.error)
        }
    }
}

// --- NEW/Polished Components for Expanded View ---

@Composable
fun WalletCard(balance: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Digital Wallet", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
            Spacer(Modifier.height(8.dp))
            Text("$$balance", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            Spacer(Modifier.height(4.dp))
            Text("Available balance", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun UpcomingTripCard(imageUrl: String, destination: String, dateRange: String) {
    Card(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = imageUrl, contentDescription = destination, contentScale = ContentScale.Crop,
                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            )
            Column(Modifier.padding(16.dp)) {
                Text(destination, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(dateRange, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Details", modifier = Modifier.padding(end = 16.dp))
        }
    }
}

@Composable
fun TrustedContactsSection() {
    Column {
        Text("Trusted Contacts", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ProfileAvatar("https://images.pexels.com/photos/91227/pexels-photo-91227.jpeg", "Contact 1", Modifier.size(48.dp))
            ProfileAvatar("https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg", "Contact 2", Modifier.size(48.dp))
            ProfileAvatar("https://images.pexels.com/photos/774909/pexels-photo-774909.jpeg", "Contact 3", Modifier.size(48.dp))
            Box(Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer).clickable {}.padding(4.dp), Alignment.Center) {
                Icon(Icons.Default.Add, "Add Contact", tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
    }
}

@Composable
fun DocumentsSection() {
    Column {
        Text("Documents", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 12.dp))
        DocumentCard(icon = Icons.Outlined.Flight, title = "Passport", subtitle = "Expires 2030")
        Spacer(Modifier.height(8.dp))
        DocumentCard(icon = Icons.Outlined.Key, title = "US Visa", subtitle = "Expires 2028")
    }
}

@Composable
private fun DocumentCard(icon: ImageVector, title: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Details")
        }
    }
}

@Composable
fun UpgradeToProCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.WorkspacePremium, "Premium", modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onTertiaryContainer)
            Spacer(Modifier.height(8.dp))
            Text("Upgrade to Pro", textAlign = TextAlign.Center, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
            Spacer(Modifier.height(4.dp))
            Text("Get exclusive travel features!", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
        }
    }
}


// --- Previews ---

@Preview
@Composable
private fun ProfileScreenMobilePreview() {
    AppTheme { ProfileScreen(authService = createMockAuthService(), onSignOut = {}, onTripClick = {}) }
}

@Preview
@Composable
private fun ProfileScreenWebPreview() {
    AppTheme { ProfileScreen(authService = createMockAuthService(), onSignOut = {}, onTripClick = {}) }
}

@Composable
private fun createMockAuthService(): AuthService {
    return object : AuthService {
        override val isAuthenticated = kotlinx.coroutines.flow.MutableStateFlow(true)
        override val currentUser = kotlinx.coroutines.flow.MutableStateFlow(
            User("1", "Tanya Edwards", "tanya.edwards@example.com", false, "https://images.pexels.com/photos/1065084/pexels-photo-1065084.jpeg")
        )
        // Dummy implementations for preview
        override suspend fun createAuthUser(email: String, password: String) = ""
        override suspend fun createUserProfile(uid: String, email: String, username: String, profilePicture: ByteArray?) {}
        override suspend fun onSignInSuccess() = true
        override suspend fun signInWithEmail(email: String, password: String) {}
        override suspend fun signInWithGoogle() {}
        override suspend fun signOut() {}
    }
}
