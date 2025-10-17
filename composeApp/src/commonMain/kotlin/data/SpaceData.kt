package data

// --- DATA MODELS ---

data class Member(
    val id: String,
    val name: String,
    val avatarUrl: String
)

data class Space(
    val id: String,
    val name: String,
    val description: String,
    val members: List<Member>,
    val unreadCount: Int,
    val coverImageUrl: String
)

data class Message(
    val id: String,
    val author: Member,
    val content: String,
    val timestamp: String,
    val isFromCurrentUser: Boolean // Simplifies UI alignment
)

// --- MOCK DATA REPOSITORY ---

object SpaceRepository {

    private val members = listOf(
        Member("user_0", "You", "https://images.pexels.com/photos/1855582/pexels-photo-1855582.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Member("user_1", "Alex", "https://images.pexels.com/photos/775358/pexels-photo-775358.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Member("user_2", "Brianna", "https://images.pexels.com/photos/1065084/pexels-photo-1065084.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Member("user_3", "Carlos", "https://images.pexels.com/photos/842980/pexels-photo-842980.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Member("user_4", "Diana", "https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Member("user_5", "Ethan", "https://images.pexels.com/photos/91227/pexels-photo-91227.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2")
    )

    private val spaces = listOf(
        Space("space_1", "Weekend Hikers", "Planning our next mountain adventure!", members.shuffled().take(4), 3, "https://images.pexels.com/photos/572897/pexels-photo-572897.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Space("space_2", "Digital Nomads", "Tips for working and traveling.", members.shuffled().take(5), 0, "https://images.pexels.com/photos/1631677/pexels-photo-1631677.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Space("space_3", "Project Phoenix", "Client-facing discussion for the new app.", members.shuffled().take(3), 8, "https://images.pexels.com/photos/326503/pexels-photo-326503.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Space("space_4", "Foodies Unite", "Sharing the best street food finds.", members.shuffled().take(6), 1, "https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Space("space_5", "Book Club", "Monthly book discussions and reviews.", members.shuffled().take(4), 2, "https://images.pexels.com/photos/590493/pexels-photo-590493.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Space("space_6", "Fitness Buddies", "Motivation and workout plans.", members.shuffled().take(5), 5, "https://images.pexels.com/photos/1552242/pexels-photo-1552242.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Space("space_7", "Movie Nights", "Vote for the next movie!", members.shuffled().take(3), 0, "https://images.pexels.com/photos/799137/pexels-photo-799137.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Space("space_8", "Tech Geeks", "Latest gadgets and tech news.", members.shuffled().take(6), 7, "https://images.pexels.com/photos/1181671/pexels-photo-1181671.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Space("space_9", "Art Lovers", "Share your artwork and inspiration.", members.shuffled().take(4), 4, "https://images.pexels.com/photos/208821/pexels-photo-208821.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Space("space_10", "Travel Stories", "Post your travel experiences.", members.shuffled().take(5), 6, "https://images.pexels.com/photos/346885/pexels-photo-346885.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2")
    )

   private val messages = listOf(
      Message("msg_1", members[1], "Hey team! I was thinking we could try the Blue Ridge trail this Saturday. Weather looks great.", "10:30 AM", false),
      Message("msg_2", members[2], "Sounds amazing! I'm in. What time should we meet?", "10:31 AM", false),
      Message("msg_3", members[0], "Great idea, Alex! Let's aim for a 9 AM start at the trailhead. I'll bring snacks.", "10:35 AM", true),
      Message("msg_4", members[1], "Perfect!", "10:36 AM", false),
      Message("msg_5", members[4], "Just joined the group. Can't make this weekend but excited for the next one!", "11:00 AM", false),
      Message("msg_6", members[3], "Does anyone have a spare water bottle I can borrow?", "11:15 AM", false),
      Message("msg_7", members[5], "I can bring an extra one for you, Carlos.", "11:20 AM", false),
      Message("msg_8", members[2], "Don't forget to pack sunscreen, everyone!", "11:25 AM", false),
      Message("msg_9", members[0], "Good call, Brianna. I'll add it to my list.", "11:30 AM", true),
      Message("msg_10", members[1], "Should we carpool to the trailhead?", "11:35 AM", false),
      Message("msg_11", members[3], "I can drive! I have space for 3 more people.", "11:40 AM", false),
      Message("msg_12", members[4], "Thanks, Carlos! I'll ride with you.", "11:45 AM", false),
      Message("msg_13", members[5], "I'll bring some energy bars for everyone.", "11:50 AM", false),
      Message("msg_14", members[2], "Does anyone know if the trail has restrooms?", "11:55 AM", false),
      Message("msg_15", members[0], "Yes, there are restrooms at the parking lot.", "12:00 PM", true),
      Message("msg_16", members[1], "Awesome! This is going to be a great hike.", "12:05 PM", false),
      Message("msg_17", members[3], "Agreed! Looking forward to it.", "12:10 PM", false),
      Message("msg_18", members[4], "I'll bring my camera to capture the views.", "12:15 PM", false),
      Message("msg_19", members[5], "Great idea, Diana! Can't wait to see the photos.", "12:20 PM", false),
      Message("msg_20", members[2], "See you all on Saturday at 9 AM!", "12:25 PM", false)
  )

    fun getSpaces(): List<Space> = spaces

    fun getSpaceById(id: String): Space? = spaces.find { it.id == id }

    fun getMessagesForSpace(spaceId: String): List<Message> = messages.shuffled() // Return shuffled for variety
}
