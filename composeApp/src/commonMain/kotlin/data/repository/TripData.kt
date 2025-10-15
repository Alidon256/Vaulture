package data.repository


// 1. Define the data model for a Trip
data class Trip(
    val id: String,
    val title: String,    val country: String,
    val imageUrl: String,
    val description: String,
    val rating: Double,
    val reviews: Int
)

// 2. Create a repository for your mock data
object TripRepository {
    fun getTrips(): List<Trip> {
        return listOf(
            Trip("uganda", "Murchison Falls", "Uganda", "https://images.pexels.com/photos/1660603/pexels-photo-1660603.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "Experience the raw power of the Nile River forcing its way through a narrow gorge.", 4.8, 120),
            Trip("kenya", "Maasai Mara", "Kenya", "https://images.pexels.com/photos/5472260/pexels-photo-5472260.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "Witness the breathtaking Great Migration, a spectacle of millions of wildebeest and zebra.", 4.9, 250),
            Trip("egypt", "Pyramids of Giza", "Egypt", "https://images.unsplash.com/photo-1569949381669-ecf31ae8e613?q=80&w=2670&auto=format&fit=crop", "Journey back in time and marvel at the last remaining wonder of the ancient world.", 4.7, 580),
            Trip("tanzania", "Zanzibar Beaches", "Tanzania", "https://images.pexels.com/photos/1525041/pexels-photo-1525041.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "Relax on the pristine white sands and swim in the turquoise waters of this tropical paradise.", 4.6, 180),
            Trip("south_africa", "Cape Town", "South Africa", "https://images.pexels.com/photos/2395249/pexels-photo-2395249.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "Discover a vibrant city nestled between the iconic Table Mountain and the sparkling Atlantic Ocean.", 4.8, 320),
            Trip("namibia", "Deadvlei", "Namibia", "https://images.pexels.com/photos/3514066/pexels-photo-3514066.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "Walk among ancient, skeletal camel thorn trees on a white clay pan, surrounded by towering red dunes.", 4.9, 95),
            Trip("ethiopia", "Lalibela", "Ethiopia", "https://images.pexels.com/photos/12832846/pexels-photo-12832846.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "Explore a complex of 11 monolithic rock-hewn churches, a testament to incredible faith and craftsmanship.", 4.7, 110),
            Trip("zimbabwe", "Victoria Falls", "Zimbabwe", "https://images.pexels.com/photos/3354427/pexels-photo-3354427.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", "Feel the immense power and spray of 'The Smoke that Thunders', one of the world's largest waterfalls.", 4.9, 410)
        )
    }
}

data class TravelCategory(
    val id: String,
    val name: String,
    val imageUrl: String
)

data class TravelStory(
    val id: String,
    val title: String,
    val author: Member, // Re-using Member data class for the creator
    val coverImageUrl: String,
    val durationInMins: Int
)

data class ForYouSuggestion(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String
)

// --- UPDATE REPOSITORY WITH NEW MOCK DATA ---

object HomeRepository { // You can merge this with another repository or keep it separate

    // Re-use the Member data from SpaceRepository or define it here
    private val members = listOf(
        Member("user_1", "Alex", "https://images.pexels.com/photos/775358/pexels-photo-775358.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
        Member("user_4", "Diana", "https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2")
    )

    fun getTravelCategories(): List<TravelCategory> {
        return listOf(
            TravelCategory("cat_safari", "Safari", "https://images.pexels.com/photos/247376/pexels-photo-247376.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            TravelCategory("cat_beaches", "Beaches", "https://images.pexels.com/photos/1174732/pexels-photo-1174732.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            TravelCategory("cat_adventure", "Adventure", "https://images.pexels.com/photos/3354427/pexels-photo-3354427.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            TravelCategory("cat_cultural", "Cultural", "https://images.pexels.com/photos/12832846/pexels-photo-12832846.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            TravelCategory("cat_cities", "Cities", "https://images.pexels.com/photos/2395249/pexels-photo-2395249.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2")
        )
    }

    fun getTravelStories(): List<TravelStory> {
        return listOf(
            TravelStory("story_1", "A Week in the Wilds of Kenya", members[0], "https://images.pexels.com/photos/1183266/pexels-photo-1183266.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", 7),
            TravelStory("story_2", "Cape Town's Culinary Scene", members[1], "https://images.pexels.com/photos/6267/menu-restaurant-vintage-table.jpg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2", 5)
        )
    }

    fun getForYouSuggestions(): List<ForYouSuggestion> {
        return listOf(
            ForYouSuggestion("foryou_1", "Top 5 Restaurants in Cairo", "Near your upcoming trip", "https://images.pexels.com/photos/262978/pexels-photo-262978.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
            ForYouSuggestion("foryou_2", "Hidden Gems of the Nile", "Based on your interest in Egypt", "https://images.pexels.com/photos/1577903/pexels-photo-1577903.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2")
        )
    }
}

