package viewmodels

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
}
