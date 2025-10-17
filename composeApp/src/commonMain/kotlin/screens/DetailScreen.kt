package screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import data.TravelDestination
import org.jetbrains.compose.resources.painterResource
import vaulture.composeapp.generated.resources.Res
import vaulture.composeapp.generated.resources.ic_img1
import viewmodels.TravelViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: TravelViewModel,
    destinationId: String,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val destination = uiState.destinations.find { it.id == destinationId }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(destination?.name ?: "Destination Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { destination?.let { viewModel.toggleFavorite(it.id) } }
                    ) {
                        if (destination?.isFavorite == true) {
                            Text("★")
                        } else {
                            Text("☆")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (destination == null) {
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                Text(
                    text = "Destination not found",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            DestinationDetails(
                destination = destination,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun DestinationDetails(
    destination: TravelDestination,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_img1),
            contentDescription = destination.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Crop
        )
        
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = destination.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "$${destination.price}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("★")
                Text(
                    text = "${destination.rating} • ${destination.location}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "About",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = destination.description,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Tags",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                destination.tags.forEach { tag ->
                    SuggestionChip(
                        onClick = { },
                        label = { Text(tag) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { /* Book now functionality */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Book Now")
            }
        }
    }
}