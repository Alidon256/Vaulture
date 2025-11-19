/*package viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import auth.User
import auth.upload
import data.PostItemData
import data.Story
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Timestamp
// CORRECTED: Removed invalid imports for whereGreaterThanOrEqualTo and whereLessThanOrEqualTo
import dev.gitlive.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SpaceUiState(
    val userName: String = "",
    val userAvatarUrl: String? = null,
    val stories: List<Story> = emptyList(),
    val posts: List<PostItemData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

open class SpaceViewModel(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpaceUiState(isLoading = true))
    val uiState: StateFlow<SpaceUiState> = _uiState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults: StateFlow<List<User>> = _searchResults.asStateFlow()

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    private val _feeds = MutableStateFlow<List<Story>>(emptyList())
    val feeds: StateFlow<List<Story>> = _feeds.asStateFlow()

    init {
        loadInitialData()
        listenForFeeds()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val firebaseUser = auth.currentUser
                val userName = firebaseUser?.displayName ?: "User"
                val userAvatarUrl = firebaseUser?.photoURL

                val postsSnapshot = firestore.collection("posts").get()
                val posts = postsSnapshot.documents.map { it.data<PostItemData>() }

                _uiState.value = SpaceUiState(
                    userName = userName,
                    userAvatarUrl = userAvatarUrl,
                    stories = emptyList(),
                    posts = posts,
                    isLoading = false
                )
            } catch (e: Exception) {
                println("Error loading data: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load data: ${e.message}"
                )
            }
        }
    }

    private fun listenForFeeds() {
        viewModelScope.launch {
            try {
                firestore.collection("feeds").snapshots.collect { snapshot ->
                    println("Received snapshot for feeds: ${snapshot.documents.size} documents")
                    val fetchedFeed = snapshot.documents.map { doc -> doc.data<Story>() }
                    _feeds.value = fetchedFeed
                }
            } catch (e: Exception) {
                println("Error listening for feeds: ${e.message}")
                _feeds.value = emptyList()
            }
        }
    }

    fun searchUsers(query: String) {
        viewModelScope.launch {
            if (query.length < 2) {
                _searchResults.value = emptyList()
                return@launch
            }
            try {
                val querySnapshot = firestore.collection("users")
                    .where {
                        "displayName" greaterThanOrEqualTo query
                        "displayName" lessThanOrEqualTo query + '\uf8ff'
                    }
                    .limit(10)
                    .get()
                val users = querySnapshot.documents.map { it.data<User>() }
                _searchResults.value = users
            } catch (e: Exception) {
                println("Error searching users: ${e.message}")
                _searchResults.value = emptyList()
            }
        }
    }


    fun addStory(
        mediaContent: ByteArray?,
        thumbnailContent: ByteArray?,
        textContent: String?,
        contentType: Story.ContentType,
        isFeed: Boolean,
        aspectRatio: Float,
        onProgress: (String) -> Unit,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                onError("User not authenticated.")
                return@launch
            }

            onProgress("Preparing content...")
            val storyId = "story_${currentUser.uid}_${Timestamp.now().seconds}"
            var finalContentUrl: String? = null
            var finalThumbnailUrl: String? = null

            try {
                if (mediaContent != null) {
                    val mediaFileName = "${contentType.name.lowercase()}_${Timestamp.now().nanoseconds}"
                    val mediaStoragePath = "stories/${currentUser.uid}/$storyId/$mediaFileName"
                    onProgress("Uploading ${contentType.name.lowercase()}...")
                    finalContentUrl = uploadFileToStorage(mediaContent, mediaStoragePath)
                }

                if (thumbnailContent != null) {
                    onProgress("Uploading thumbnail...")
                    val thumbnailFileName = "thumbnail_${Timestamp.now().nanoseconds}"
                    val thumbnailStoragePath = "stories/${currentUser.uid}/$storyId/thumbnails/$thumbnailFileName"
                    finalThumbnailUrl = uploadFileToStorage(thumbnailContent, thumbnailStoragePath)
                }

                onProgress("Saving details...")
                val newStory = Story(
                    storyId = storyId,
                    userId = currentUser.uid,
                    userName = currentUser.displayName ?: "Anonymous",
                    userProfileUrl = currentUser.photoURL,
                    contentType = contentType,
                    contentUrl = finalContentUrl,
                    thumbnailUrl = finalThumbnailUrl,
                    textContent = if (contentType == Story.ContentType.TEXT) textContent else null,
                    // CORRECTED: 'timestamp' is removed. It will be handled by the data class.
                    duration = 0,
                    aspectRatio = aspectRatio,
                    isFeed = isFeed,
                    expiryTimestamp = null,
                    isViewed = false,
                    viewCount = 0,
                    interactiveElements = emptyList(),
                    locationTag = null,
                    musicTrack = null,
                    privacySetting = Story.PrivacySetting.PUBLIC,
                    isArchived = false,
                    callToAction = null,
                    linkUrl = null,
                    isBusinessStory = false,
                    likeCount = 0,
                    likedByUserIds = emptyList(),
                    commentCount = 0,
                    comments = emptyList(),
                    shareCount = 0,
                    sharedByUserIds = emptyList(),
                    isLiked = false
                )

                val collectionName = if (isFeed) "feeds" else "stories"
                firestore.collection(collectionName).document(storyId).set(newStory)

                onProgress("${if (isFeed) "Feed" else "Story"} posted successfully!")
                onSuccess()

            } catch (e: Exception) {
                println("Error adding story: ${e.message}")
                onError(e.message ?: "An unknown error occurred while adding the story.")
            }
        }
    }

    private suspend fun uploadFileToStorage(
        fileBytes: ByteArray,
        storagePath: String
    ): String {
        val storageRef = storage.reference(storagePath)
        // CORRECTED: The function is 'putBytes', not 'upload'
        storageRef.upload(fileBytes)
        return storageRef.getDownloadUrl()
    }
}*/
package viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import auth.User
import auth.upload
import data.PostItemData
import data.Space // NEW: Import for Space data model
import data.SpaceMessage // NEW: Import for SpaceMessage data model
import data.Story
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.where // NEW: Import for Firestore queries
import dev.gitlive.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// --- Original UI State (for Home/Feeds) ---
data class SpaceUiState(
    val userName: String = "",
    val userAvatarUrl: String? = null,
    val stories: List<Story> = emptyList(),
    val posts: List<PostItemData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// --- View Model with integrated "Spaces" logic ---
open class SpaceViewModel(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    val auth: FirebaseAuth
) : ViewModel() {

    // --- Original State Properties ---
    private val _uiState = MutableStateFlow(SpaceUiState(isLoading = true))
    val uiState: StateFlow<SpaceUiState> = _uiState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults: StateFlow<List<User>> = _searchResults.asStateFlow()

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    private val _feeds = MutableStateFlow<List<Story>>(emptyList())
    val feeds: StateFlow<List<Story>> = _feeds.asStateFlow()

    // --- NEW: State Properties for the "Spaces" Feature ---

    // For listing all spaces
    private val _spaces = MutableStateFlow<List<Space>>(emptyList())
    val spaces: StateFlow<List<Space>> = _spaces.asStateFlow()
    private val _isLoadingSpaces = MutableStateFlow(true)
    val isLoadingSpaces: StateFlow<Boolean> = _isLoadingSpaces.asStateFlow()

    // For creating a new space
    val spaceName = mutableStateOf("")
    val spaceDescription = mutableStateOf("")
    private val _isCreatingSpace = MutableStateFlow(false)
    val isCreatingSpace: StateFlow<Boolean> = _isCreatingSpace.asStateFlow()

    // For viewing a single space's details, posts, and chat
    private val _currentSpace = MutableStateFlow<Space?>(null)
    val currentSpace: StateFlow<Space?> = _currentSpace.asStateFlow()

    private val _spacePosts = MutableStateFlow<List<Story>>(emptyList())
    val spacePosts: StateFlow<List<Story>> = _spacePosts.asStateFlow()

    private val _spaceMessages = MutableStateFlow<List<SpaceMessage>>(emptyList())
    val spaceMessages: StateFlow<List<SpaceMessage>> = _spaceMessages.asStateFlow()
    val newMessageText = mutableStateOf("")


    init {
        // Original data loading
        loadInitialData()
        listenForFeeds()
        // NEW: Load spaces when ViewModel is created
        fetchSpaces()
    }

    // --- Original Methods (Unchanged) ---

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val firebaseUser = auth.currentUser
                val userName = firebaseUser?.displayName ?: "User"
                val userAvatarUrl = firebaseUser?.photoURL

                val postsSnapshot = firestore.collection("posts").get()
                val posts = postsSnapshot.documents.map { it.data<PostItemData>() }

                _uiState.value = SpaceUiState(
                    userName = userName,
                    userAvatarUrl = userAvatarUrl,
                    stories = emptyList(),
                    posts = posts,
                    isLoading = false
                )
            } catch (e: Exception) {
                println("Error loading data: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load data: ${e.message}"
                )
            }
        }
    }

    private fun listenForFeeds() {
        viewModelScope.launch {
            try {
                firestore.collection("feeds").snapshots.collect { snapshot ->
                    println("Received snapshot for feeds: ${snapshot.documents.size} documents")
                    val fetchedFeed = snapshot.documents.map { doc -> doc.data<Story>() }
                    _feeds.value = fetchedFeed
                }
            } catch (e: Exception) {
                println("Error listening for feeds: ${e.message}")
                _feeds.value = emptyList()
            }
        }
    }

    fun searchUsers(query: String) {
        viewModelScope.launch {
            if (query.length < 2) {
                _searchResults.value = emptyList()
                return@launch
            }
            try {
                val querySnapshot = firestore.collection("users")
                    .where {
                        "displayName" greaterThanOrEqualTo query
                        "displayName" lessThanOrEqualTo query + '\uf8ff'
                    }
                    .limit(10)
                    .get()
                val users = querySnapshot.documents.map { it.data<User>() }
                _searchResults.value = users
            } catch (e: Exception) {
                println("Error searching users: ${e.message}")
                _searchResults.value = emptyList()
            }
        }
    }


    fun addStory(
        mediaContent: ByteArray?,
        thumbnailContent: ByteArray?,
        textContent: String?,
        contentType: Story.ContentType,
        isFeed: Boolean,
        aspectRatio: Float,
        onProgress: (String) -> Unit,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                onError("User not authenticated.")
                return@launch
            }

            onProgress("Preparing content...")
            val storyId = "story_${currentUser.uid}_${Timestamp.now().seconds}"
            var finalContentUrl: String? = null
            var finalThumbnailUrl: String? = null

            try {
                if (mediaContent != null) {
                    val mediaFileName = "${contentType.name.lowercase()}_${Timestamp.now().nanoseconds}"
                    val mediaStoragePath = "stories/${currentUser.uid}/$storyId/$mediaFileName"
                    onProgress("Uploading ${contentType.name.lowercase()}...")
                    finalContentUrl = uploadFileToStorage(mediaContent, mediaStoragePath)
                }

                if (thumbnailContent != null) {
                    onProgress("Uploading thumbnail...")
                    val thumbnailFileName = "thumbnail_${Timestamp.now().nanoseconds}"
                    val thumbnailStoragePath = "stories/${currentUser.uid}/$storyId/thumbnails/$thumbnailFileName"
                    finalThumbnailUrl = uploadFileToStorage(thumbnailContent, thumbnailStoragePath)
                }

                onProgress("Saving details...")
                val newStory = Story(
                    storyId = storyId,
                    userId = currentUser.uid,
                    userName = currentUser.displayName ?: "Anonymous",
                    userProfileUrl = currentUser.photoURL,
                    contentType = contentType,
                    contentUrl = finalContentUrl,
                    thumbnailUrl = finalThumbnailUrl,
                    textContent = if (contentType == Story.ContentType.TEXT) textContent else null,
                    duration = 0,
                    aspectRatio = aspectRatio,
                    isFeed = isFeed,
                    expiryTimestamp = null,
                    isViewed = false,
                    viewCount = 0,
                    interactiveElements = emptyList(),
                    locationTag = null,
                    musicTrack = null,
                    privacySetting = Story.PrivacySetting.PUBLIC,
                    isArchived = false,
                    callToAction = null,
                    linkUrl = null,
                    isBusinessStory = false,
                    likeCount = 0,
                    likedByUserIds = emptyList(),
                    commentCount = 0,
                    comments = emptyList(),
                    shareCount = 0,
                    sharedByUserIds = emptyList(),
                    isLiked = false
                )

                val collectionName = if (isFeed) "feeds" else "stories"
                firestore.collection(collectionName).document(storyId).set(newStory)

                onProgress("${if (isFeed) "Feed" else "Story"} posted successfully!")
                onSuccess()

            } catch (e: Exception) {
                println("Error adding story: ${e.message}")
                onError(e.message ?: "An unknown error occurred while adding the story.")
            }
        }
    }

    private suspend fun uploadFileToStorage(
        fileBytes: ByteArray,
        storagePath: String
    ): String {
        val storageRef = storage.reference(storagePath)
        storageRef.upload(fileBytes)
        return storageRef.getDownloadUrl()
    }


    // --- NEW: Methods for the "Spaces" Feature ---

    /**
     * Fetches all spaces from Firestore in real-time.
     */
    fun fetchSpaces() {
        viewModelScope.launch {
            _isLoadingSpaces.value = true
            try {
                firestore.collection("spaces").snapshots.collect { snapshot ->
                    _spaces.value = snapshot.documents.map { it.data() }
                    _isLoadingSpaces.value = false
                }
            } catch (e: Exception) {
                println("Error fetching spaces: ${e.message}")
                _isLoadingSpaces.value = false
            }
        }
    }

    /**
     * Creates a new space, uploads a cover image, and adds the current user as the owner.
     * @param coverImage The cover image as a ByteArray.
     * @param onResult Callback providing success status and the new space's ID.
     */
    fun createSpace(coverImage: ByteArray?, onResult: (success: Boolean, spaceId: String?) -> Unit) {
        viewModelScope.launch {
            _isCreatingSpace.value = true
            val currentUser = auth.currentUser ?: run {
                onResult(false, null)
                _isCreatingSpace.value = false
                return@launch
            }

            try {
                // This is the definitive, correct pattern for creating a new document.
                // 1. Create a placeholder object WITHOUT an ID.
                val newSpaceData = Space(
                    // ID is left empty here, Firestore will generate it.
                    name = spaceName.value.trim(),
                    description = spaceDescription.value.trim(),
                    ownerId = currentUser.uid,
                    memberIds = listOf(currentUser.uid)
                    // coverImageUrl will be updated after upload
                )

                // 2. Add the object to the collection. This returns a reference to the new document.
                val newSpaceRef = firestore.collection("spaces").add(newSpaceData)
                val spaceId = newSpaceRef.id
                println("SUCCESS: New space document created with ID: $spaceId")

                // 3. Upload image using the newly acquired ID.
                val coverImageUrl = if (coverImage != null) {
                    uploadFileToStorage(coverImage, "space_covers/$spaceId.jpg")
                } else ""

                // 4. Update the document with the final ID and image URL.
                newSpaceRef.update(
                    "id" to spaceId,
                    "coverImageUrl" to coverImageUrl
                )

                // Reset state and report success
                spaceName.value = ""
                spaceDescription.value = ""
                _isCreatingSpace.value = false
                onResult(true, spaceId)

            } catch (e: Exception) {
                println("Error creating space: ${e.message}")
                _isCreatingSpace.value = false
                onResult(false, null)
            }
        }
    }

    /**
     * Joins the current user to a space by adding their UID to the memberIds array.
     */
    fun joinSpace(spaceId: String) {
        viewModelScope.launch {
            val currentUser = auth.currentUser ?: return@launch
            try {
                // CORRECTED: The update function requires field-value pairs using the `to` infix function.
                firestore.collection("spaces").document(spaceId).update(
                    "memberIds" to FieldValue.arrayUnion(currentUser.uid)
                )
            } catch (e: Exception) {
                println("Error joining space: ${e.message}")
            }
        }
    }

    /**
     * Loads the details for a specific space and listens for its associated posts.
     * Establishes real-time listeners.
     */
    fun loadSpaceDetails(spaceId: String) {
        viewModelScope.launch {
            // Listen for space details
            firestore.collection("spaces").document(spaceId).snapshots.collect { snapshot ->
                _currentSpace.value = snapshot.data()
            }
        }
        viewModelScope.launch {
            // Listen for posts in this space
            // CORRECTED: 'equalTo' is replaced with the 'isEqualTo' infix operator.
            firestore.collection("feeds")
                .where { "spaceId" equalTo  spaceId }
                .snapshots.collect { snapshot ->
                    _spacePosts.value = snapshot.documents.map { it.data() }
                }
        }
    }

    /**
     * Establishes a real-time listener for chat messages within a specific space.
     */
    fun listenForChatMessages(spaceId: String) {
        viewModelScope.launch {
            firestore.collection("spaces").document(spaceId).collection("messages")
                .orderBy("timestamp")
                .snapshots.collect { snapshot ->
                    _spaceMessages.value = snapshot.documents.map { it.data() }
                }
        }
    }

    /**
     * Sends a new chat message to the currently viewed space.
     */
    fun sendChatMessage(spaceId: String) {
        val text = newMessageText.value.trim()
        if (text.isBlank()) return

        val currentUser = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                // This is the definitive, correct pattern for creating a new document in a sub-collection.
                // 1. Create the data object without an ID.
                val messageData = SpaceMessage(
                    spaceId = spaceId,
                    authorId = currentUser.uid,
                    authorName = currentUser.displayName ?: "User",
                    authorAvatarUrl = currentUser.photoURL ?: "",
                    text = text
                )

                // 2. Add the object. This creates the document and returns a reference.
                val messageRef = firestore.collection("spaces").document(spaceId).collection("messages").add(messageData)
                val messageId = messageRef.id

                // 3. Update the new document with its own ID for future reference.
                messageRef.update("id" to messageId)

                newMessageText.value = "" // Clear input field after sending

            } catch (e: Exception) {
                println("Error sending chat message: ${e.message}")
            }
        }
    }

    /**
     * Clears all detailed space data to prevent data leaks and unnecessary listeners
     * when the user navigates away from the SpaceDetailScreen.
     */
    fun clearSpaceDetails() {
        _currentSpace.value = null
        _spacePosts.value = emptyList()
        _spaceMessages.value = emptyList()
    }
}
