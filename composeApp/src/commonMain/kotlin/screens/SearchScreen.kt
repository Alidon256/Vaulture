package screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.DestinationCard
import viewmodels.TravelViewModel

@Composable
fun SearchScreen(
    viewModel: TravelViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    
    val filters = listOf("Beach", "City", "Culture", "Nature", "Food", "History")
    
    val filteredDestinations = remember(searchQuery, selectedFilter, uiState.destinations) {
        uiState.destinations.filter { destination ->
            val matchesQuery = searchQuery.isEmpty() || 
                destination.name.contains(searchQuery, ignoreCase = true) ||
                destination.location.contains(searchQuery, ignoreCase = true) ||
                destination.description.contains(searchQuery, ignoreCase = true)
            
            val matchesFilter = selectedFilter == null || 
                destination.tags.any { it.equals(selectedFilter, ignoreCase = true) }
            
            matchesQuery && matchesFilter
        }
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search destinations...") },
            singleLine = true
        )
        
        // Filters
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("All") }
                )
            }
            
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Results
        if (filteredDestinations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No destinations found")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredDestinations) { destination ->
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

@Composable
private fun LazyRow(
    contentPadding: PaddingValues,
    horizontalArrangement: Arrangement.HorizontalOrVertical,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {
    androidx.compose.foundation.lazy.LazyRow(
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}