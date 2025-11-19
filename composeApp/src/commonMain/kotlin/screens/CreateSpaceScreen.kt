package screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import data.HomeRepository
import data.Member
import data.SpaceRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSpaceScreen(
    onNavigateBack: () -> Unit,
    onCreateSpace: () -> Unit,
) {
    var spaceName by remember { mutableStateOf("") }
    var spaceDescription by remember { mutableStateOf("") }
    val allMembers = remember { SpaceRepository.getMembers() }
    var selectedMembers by remember { mutableStateOf(setOf<Member>()) }
    var privacy by remember { mutableStateOf(SpacePrivacy.PUBLIC) }
    val isFormValid by derivedStateOf { spaceName.isNotBlank() && spaceDescription.isNotBlank() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Space", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            Button(
                onClick = onCreateSpace,
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp)
            ) {
                Text("Create Space")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                CoverImageSelector()
            }
            item {
                OutlinedTextField(
                    value = spaceName,
                    onValueChange = { spaceName = it },
                    label = { Text("Space Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = spaceDescription,
                    onValueChange = { spaceDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            item {
                PrivacySelector(
                    selectedPrivacy = privacy,
                    onPrivacySelected = { privacy = it }
                )
            }
            item {
                MemberSelector(
                    allMembers = allMembers,
                    selectedMembers = selectedMembers,
                    onMemberToggled = { member ->
                        selectedMembers = if (selectedMembers.contains(member)) {
                            selectedMembers - member
                        } else {
                            selectedMembers + member
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CoverImageSelector() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { /* TODO: Implement image picker */ },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.AddAPhoto, contentDescription = "Add Cover Photo", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Add Cover Photo", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrivacySelector(
    selectedPrivacy: SpacePrivacy,
    onPrivacySelected: (SpacePrivacy) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Privacy", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PrivacyChip(
                text = "Public",
                icon = Icons.Default.Public,
                isSelected = selectedPrivacy == SpacePrivacy.PUBLIC,
                onClick = { onPrivacySelected(SpacePrivacy.PUBLIC) }
            )
            PrivacyChip(
                text = "Private",
                icon = Icons.Default.Lock,
                isSelected = selectedPrivacy == SpacePrivacy.PRIVATE,
                onClick = { onPrivacySelected(SpacePrivacy.PRIVATE) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrivacyChip(text: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(text) },
        leadingIcon = { Icon(icon, contentDescription = text) },
        shape = CircleShape
    )
}

@Composable
private fun MemberSelector(
    allMembers: List<Member>,
    selectedMembers: Set<Member>,
    onMemberToggled: (Member) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Invite Members (${selectedMembers.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(allMembers) { member ->
                MemberInviteCard(
                    member = member,
                    isSelected = selectedMembers.contains(member),
                    onClick = { onMemberToggled(member) }
                )
            }
        }
    }
}

@Composable
private fun MemberInviteCard(member: Member, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box {
            AsyncImage(
                model = member.avatarUrl,
                contentDescription = member.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = CircleShape
                    )
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.BottomEnd)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(4.dp)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(member.name, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
    }
}

enum class SpacePrivacy {
    PUBLIC, PRIVATE
}
