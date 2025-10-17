package screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import data.Message
import data.Space
import data.SpaceRepository
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceDetailScreen(
    spaceId: String,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var space by remember { mutableStateOf<Space?>(null) }
    //val messages = remember { SpaceRepository.getMessagesForSpace(spaceId) }
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var newMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(spaceId) {
        isLoading = true
        space = SpaceRepository.getSpaceById(spaceId)
        messages = SpaceRepository.getMessagesForSpace(spaceId)
        isLoading = false
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    /*if (space == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Space not found.") }
        return
    }*/
    if (space == null) {
        // --- TEMPORARY DIAGNOSTIC ---// This will display the exact value of spaceId that is causing the problem.
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Space not found.")
                // Add this Text element to show us the faulty ID
                Text("Received ID: [${spaceId}]")
            }
        }
        return
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(space!!.name, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            MessageInput(
                value = newMessage,
                onValueChange = { newMessage = it },
                onSend = {
                    if (newMessage.isNotBlank()) {
                        coroutineScope.launch {
                            //SpaceRepository.sendMessageToSpace(spaceId, newMessage)
                            messages = SpaceRepository.getMessagesForSpace(spaceId)
                            newMessage = ""
                            listState.animateScrollToItem(messages.size)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message = message)
            }
        }
    }
}

@Composable
private fun MessageBubble(message: Message) {
    val horizontalArrangement = if (message.isFromCurrentUser) Arrangement.End else Arrangement.Start
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isFromCurrentUser) {
            AsyncImage(
                model = message.author.avatarUrl,
                contentDescription = message.author.name,
                modifier = Modifier.size(32.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(8.dp))
        }
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (message.isFromCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (message.isFromCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Column(Modifier.padding(12.dp)) {
                if (!message.isFromCurrentUser) {
                    Text(message.author.name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
                Text(message.content, style = MaterialTheme.typography.bodyLarge)
                Text(
                    message.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun MessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(Modifier.width(8.dp))
            AnimatedVisibility(
                visible = value.isNotBlank(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                IconButton(onClick = onSend) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}


// --- Previews ---

@Preview
@Composable
private fun SpaceDetailScreenPreview() {
    AppTheme {
        SpaceDetailScreen("space_1", onNavigateBack = {})
    }
}

@Preview
@Composable
private fun MessageBubbleCurrentUserPreview() {
    AppTheme {
        MessageBubble(
            Message(
                "id",
                SpaceRepository.getSpaces().first().members.first(),
                "This is a test message.",
                "10:40 AM",
                true
            )
        )
    }
}

@Preview
@Composable
private fun MessageBubbleOtherUserPreview() {
    AppTheme {
        MessageBubble(Message("id", SpaceRepository.getSpaces().first().members[1], "This is a reply.", "10:41 AM", false))
    }
}
