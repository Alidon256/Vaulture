package screens

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import data.ChatMessage
import data.ChatRepository
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatId: String,
    onNavigateBack: () -> Unit
) {
    val chat = remember(chatId) { ChatRepository.getChats().find { it.id == chatId } }
    val messages = remember(chatId) { ChatRepository.getMessagesForChat(chatId) }
    var newMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        coroutineScope.launch {
            // Scroll to the bottom when a new message arrives
            listState.animateScrollToItem(messages.size)
        }
    }

    if (chat == null) {
        // Handle case where chat is not found
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Chat not found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = chat.partnerAvatarUrl,
                            contentDescription = chat.partnerName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(36.dp).clip(CircleShape)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(chat.partnerName, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            MessageInputBar(
                value = newMessage,
                onValueChange = { newMessage = it },
                onSendClick = {
                    if (newMessage.isNotBlank()) {
                        // TODO: Add logic to send message
                        newMessage = ""
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                MessageBubble(message)
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isFromMe) {
            AsyncImage(
                model = message.authorAvatarUrl,
                contentDescription = "Author",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .align(Alignment.Bottom)
            )
            Spacer(Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(if (message.text.length > 40) 0.9f else 0.7f)
                .background(
                    color = if (message.isFromMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.text,
                color = if (message.isFromMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .navigationBarsPadding(), // Add padding for gesture navigation
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { /*TODO: Attach file*/ }) {
                Icon(Icons.Default.Add, "Attach file", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message...") },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                )
            )

            val isMicVisible = value.isBlank()

            Crossfade(targetState = isMicVisible, label = "SendButton") { isMic ->
                if (isMic) {
                    IconButton(onClick = { /*TODO: Record voice*/ }) {
                        Icon(Icons.Default.Mic, "Record voice", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    FilledIconButton(
                        onClick = onSendClick,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, "Send message", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChatDetailScreenPreview() {
    AppTheme {
        ChatDetailScreen("1", onNavigateBack = {})
    }
}
