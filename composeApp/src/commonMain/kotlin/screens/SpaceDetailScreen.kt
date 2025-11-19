package screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/*
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

    if (space == null) {
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
            Column {
                TopAppBar(
                    title = { Text(space!!.name, fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            }
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
*/
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import components.PostItem
import data.SpaceMessage
import data.Story
import kotlinx.coroutines.launch
import viewmodels.SpaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceDetailScreen(
    spaceId: String,
    viewModel: SpaceViewModel,
    onNavigateBack: () -> Unit
) {
    val space by viewModel.currentSpace.collectAsState()
    var selectedTab by remember { mutableStateOf(SpaceDetailTab.POSTS) }

    LaunchedEffect(spaceId) {
        viewModel.loadSpaceDetails(spaceId)
        viewModel.listenForChatMessages(spaceId)
    }

    // Clean up when the screen is left
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSpaceDetails()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(space?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(Modifier.fillMaxSize().padding(paddingValues)) {
            SpaceDetailHeader(space?.coverImageUrl, space?.name)

            SpaceDetailTabs(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        (slideInHorizontally { height -> height } + fadeIn()).togetherWith(slideOutHorizontally { height -> -height } + fadeOut())
                    } else {
                        (slideInHorizontally { height -> -height } + fadeIn()).togetherWith(slideOutHorizontally { height -> height } + fadeOut())
                    }.using(SizeTransform(clip = false))
                }, label = "TabContent"
            ) { tab ->
                when(tab) {
                    SpaceDetailTab.POSTS -> SpacePostsContent(viewModel)
                    SpaceDetailTab.CHAT -> SpaceChatContent(viewModel, spaceId)
                }
            }
        }
    }
}

@Composable
private fun SpaceDetailHeader(coverImageUrl: String?, spaceName: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (coverImageUrl != null) {
            AsyncImage(
                model = coverImageUrl,
                contentDescription = spaceName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
    }
}

@Composable
private fun SpaceDetailTabs(selectedTab: SpaceDetailTab, onTabSelected: (SpaceDetailTab) -> Unit) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        SpaceDetailTab.entries.forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = { Text(tab.title) },
                icon = { Icon(tab.icon, contentDescription = tab.title) }
            )
        }
    }
}

@Composable
fun SpacePostsContent(viewModel: SpaceViewModel) {
    val posts by viewModel.spacePosts.collectAsState()
    if (posts.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No posts yet. Be the first to share something!", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(posts, key = { it.storyId }) { post ->
                PostItem(post, {}, {}, {}, {})
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.1f))
            }
        }
    }
}

@Composable
fun SpaceChatContent(viewModel: SpaceViewModel, spaceId: String) {
    val messages by viewModel.spaceMessages.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto-scroll to new messages
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                // Assuming current user ID can be checked from auth state elsewhere
                // For now, let's assume `isFromMe` is part of the model logic
                val isFromMe = message.authorId == viewModel.auth.currentUser?.uid
                ChatMessageBubble(message = message, isFromMe = isFromMe)
            }
        }

        MessageInputRow(
            value = viewModel.newMessageText.value,
            onValueChange = { viewModel.newMessageText.value = it },
            onSend = { viewModel.sendChatMessage(spaceId) }
        )
    }
}

@Composable
fun ChatMessageBubble(message: SpaceMessage, isFromMe: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isFromMe) {
            AsyncImage(
                model = message.authorAvatarUrl,
                contentDescription = message.authorName,
                modifier = Modifier.size(32.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isFromMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isFromMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ) {
            Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                if (!isFromMe) {
                    Text(message.authorName, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Text(message.text, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun MessageInputRow(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onSend, enabled = value.isNotBlank()) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (value.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}


private enum class SpaceDetailTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    POSTS("Posts", Icons.Default.DynamicFeed),
    CHAT("Chat", Icons.Default.ChatBubble)
}
