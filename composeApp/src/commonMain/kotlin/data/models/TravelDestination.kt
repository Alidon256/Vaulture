package org.vaulture.com.data.models

data class TravelDestination(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val rating: Float,
    val price: Double,
    val location: String,
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false
)