package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import data.Member
import data.Space
import data.SpaceRepository
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpacesScreen(
    onSpaceClick: (spaceId: String) -> Unit
) {
    val spaces = remember { SpaceRepository.getSpaces() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spaces", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Create new space */ },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Space")
            }
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            val contentPadding = if (maxWidth > 600.dp) 80.dp else 16.dp

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = contentPadding, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(spaces) { space ->
                    SpaceListItem(
                        space = space,
                        onClick = { onSpaceClick(space.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SpaceListItem(
    space: Space,
    onClick: () -> Unit,modifier: Modifier = Modifier
) {
    Column(
        // The overall horizontal alignment for the content inside.
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
                shape = RoundedCornerShape(16.dp) // Match the clip shape
            )
            // --- KEY FIX: Add consistent padding for all content inside the card ---
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
                    // Use a contrasting color for better readability on a primary background
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
        SpacesScreen(onSpaceClick = {})
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
}
