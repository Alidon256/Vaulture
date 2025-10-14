/*package org.vaulture.com.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.vaulture.com.presentation.components.DestinationCard
import org.vaulture.com.presentation.viewmodels.TravelUiState
import org.vaulture.com.presentation.viewmodels.TravelViewModel
import vaulture.composeapp.generated.resources.Res
import vaulture.composeapp.generated.resources.ic_img1

@Composable
fun ProfileScreen(
    viewModel: TravelViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            ProfileHeader()
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your Favorites",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        val favorites = uiState.destinations.filter { it.isFavorite }
        if (favorites.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "You haven't added any favorites yet",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        } else {
            items(favorites) { destination ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    DestinationCard(
                        destination = destination,
                        onCardClick = { onNavigateToDetail(destination.id) },
                        onFavoriteClick = { viewModel.toggleFavorite(destination.id) }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Recent Trips",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Dummy recent trips
            ListItem(
                headlineContent = { Text("Bali, Indonesia") },
                supportingContent = { Text("April 10-17, 2023") },
                leadingContent = {
                    Image(
                        painter = painterResource(Res.drawable.ic_img1),
                        contentDescription = null,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            )
            
            ListItem(
                headlineContent = { Text("Paris, France") },
                supportingContent = { Text("January 5-12, 2023") },
                leadingContent = {
                    Image(
                        painter = painterResource(Res.drawable.ic_img1),
                        contentDescription = null,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            )
        }
    }
}

@Composable
private fun ProfileHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "JD",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "John Doe",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Travel Enthusiast",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "12",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text("Trips")
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "5",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text("Countries")
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "2",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text("Favorites")
            }
        }
    }
}

@Composable
@Preview()
fun ProfileStatsPreview() {
    ProfileHeader()
}
@Preview
            @Composable
            fun ProfileScreenPreview() {
                val mockUiState = MutableStateFlow(
                    TravelUiState(
                        destinations = listOf(
                            org.vaulture.com.data.models.TravelDestination(
                                id = "1",
                                name = "Bali, Indonesia",
                                description = "A beautiful island known for its forested volcanic mountains, iconic rice paddies, beaches and coral reefs.",
                                imageUrl = "travel.webp",
                                rating = 4.8f,
                                price = 1200.0,
                                location = "Indonesia",
                                tags = listOf("Beach", "Culture", "Nature"),
                                isFavorite = true
                            ),
                            org.vaulture.com.data.models.TravelDestination(
                                id = "2",
                                name = "Paris, France",
                                description = "The City of Light is known for its iconic Eiffel Tower, world-class cuisine, and charming streets.",
                                imageUrl = "travel2.webp",
                                rating = 4.7f,
                                price = 1500.0,
                                location = "France",
                                tags = listOf("City", "Culture", "Food"),
                                isFavorite = true
                            )
                        ),
                        isLoading = false
                    )
                )
                val mockViewModel = object : TravelViewModel() {
                    override var uiState: StateFlow<TravelUiState> = mockUiState
                }
                ProfileScreen(
                    viewModel = mockViewModel,
                    onNavigateToDetail = {}
                )
            }*/

// Create a new file: ProfileScreen.kt
package org.vaulture.com.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // If back navigation is needed
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert // Or Settings icon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.vaulture.com.presentation.theme.AppTheme
import org.vaulture.com.presentation.viewmodels.TravelViewModel // Assuming you use this
import vaulture.composeapp.generated.resources.* // Your resources

data class ProfileDetailItem(val label: String, val value: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    //viewModel: TravelViewModel, // Or a dedicated ProfileViewModel
    onNavigateBack: (() -> Unit)? = null, // Optional back navigation
    onEditProfile: () -> Unit,
    onSignOut: () -> Unit
) {
    val userProfileDetails = listOf(
        ProfileDetailItem("Username", "tanyaedwards"),
        ProfileDetailItem("Email", "tanyaedwards@gmail.com"),
        ProfileDetailItem("Phone", "(239) 555-0108"),
        ProfileDetailItem("Date of Birth", "March 27, 1998"),
        ProfileDetailItem("Address", "6391 Elgin St. Celina, Delaware"),
        ProfileDetailItem("Account", "Gold")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    onNavigateBack?.let { // Show back arrow only if onNavigateBack is provided
                        IconButton(onClick = it) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onEditProfile) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface // Or a light gray like Color(0xFFF7F7F7)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp) // Main horizontal padding
                .verticalScroll(rememberScrollState()), // Make content scrollable
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Profile Picture
            Image(
                painter = painterResource(Res.drawable.profile), // Replace with actual image
                contentDescription = "Tanya Edwards",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant) // Placeholder background
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Name and Location
            Text(
                "Tanya Edwards",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                "San Francisco, CA",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Profile Details List
            userProfileDetails.forEach { item ->
                ProfileDetailRow(label = item.label, value = item.value)
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Out Button
            Button(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)) // Green
            ) {
                Text("Sign out", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(16.dp)) // Space at the very bottom
        }
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f) // Adjust weight for label width
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.6f)
        )
    }
}
@Composable
@Preview
fun ProfileScreenPreview() {
    AppTheme {
        ProfileScreen(
            onNavigateBack = { /* Do nothing */ },
            onEditProfile = { /* Do nothing */ },
            onSignOut = { /* Do nothing */ }
        )
    }

}
