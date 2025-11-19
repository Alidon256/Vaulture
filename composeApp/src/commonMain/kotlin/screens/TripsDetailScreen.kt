package screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
//import com.kizitonwose.calendar.compose.HorizontalCalendar
//import com.kizitonwose.calendar.compose.rememberCalendarState
//import com.kizitonwose.calendar.core.*
import data.TripRepository
import kotlinx.coroutines.delay
//import kotlinx.datetime.*
//import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.painterResource
import vaulture.composeapp.generated.resources.Res
import vaulture.composeapp.generated.resources.maps
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    tripId: String,
    onNavigateBack: () -> Unit
) {
    val trip = remember { TripRepository.getTrips().find { it.id == tripId } }
    var isContentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200) // Delay to sync with navigation animation
        isContentVisible = true
    }

    if (trip == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Trip not found.", style = MaterialTheme.typography.titleLarge)
        }
        return
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Handle booking action */ },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Bookmark, contentDescription = "Book Now")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Book Now")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
            AsyncImage(
                model = trip.imageUrl,
                contentDescription = trip.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Top bar with back button
            TopAppBar(
                title = {},
                navigationIcon = {
                    // Back button FIX: Add a background for better visibility
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(start = 8.dp)
                            .background(Color.Black.copy(alpha = 0.3f), CircleShape).zIndex(1f)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                )
            )

            // Content scrolled over the image
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(250.dp))

                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = MaterialTheme.shapes.extraLarge.copy(bottomEnd = CornerSize(0.dp), bottomStart = CornerSize(0.dp))
                ) {
                    AnimatedVisibility(
                        visible = isContentVisible,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            // Trip Info Section
                            Text(
                                text = trip.title,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${trip.rating} (${trip.reviews} reviews)",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = trip.description,
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))

                            // Gallery Section
                            FeatureSection(
                                icon = Icons.Default.PhotoLibrary,
                                title = "Photo Gallery"
                            )
                            ImageGallery(images = trip.gallery)
                            Spacer(modifier = Modifier.height(24.dp))

                            // Map Section
                            FeatureSection(
                                icon = Icons.Default.Place,
                                title = "Location"
                            )
                            // Placeholder for a map view
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { /* TODO: Open map */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(Res.drawable.maps),
                                    contentDescription = "Map Location",
                                    modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(16.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))

                            // Calendar Section - REAL IMPLEMENTATION
                            FeatureSection(
                                icon = Icons.Default.CalendarMonth,
                                title = "Select Dates"
                            )
                            //ComposeCalendar()

                            // Spacer for the FAB
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}

// --- CALENDAR IMPLEMENTATION ---
/*
@OptIn(ExperimentalTime::class)
@Composable
private fun ComposeCalendar() {
    // Corrected: Chain .date to get the LocalDate, then get its yearMonth
    val currentMonth = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.yearMonth }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    // Corrected: Use kotlinx.datetime.DayOfWeek
    val daysOfWeek = remember { DayOfWeek.entries }

    // Mock dates: Pre-select a 5-day range starting from the 10th of the current month
    // Corrected: Use kotlinx-datetime for date manipulation
    // Corrected: Get the .date part from the LocalDateTime
    val today = remember { kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    var selection by remember {
        val startDate = LocalDate(today.year, today.month, 10)
        val endDate = startDate.plus(4, DateTimeUnit.DAY)
        mutableStateOf(DateSelection(startDate, endDate))
    }


    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    HorizontalCalendar(
        state = state,
        dayContent = { day ->
            Day(day, selection = selection) { clickedDate ->
                val (startDate, _) = selection
                selection = if (startDate == null || selection.end != null) {
                    // Nothing is selected or a range is already selected.
                    // Start a new selection.
                    DateSelection(start = clickedDate, end = null)
                } else if (clickedDate < startDate) {
                    // The clicked date is before the current start date.
                    // Start a new selection from the clicked date.
                    DateSelection(start = clickedDate, end = null)
                } else {
                    // A start date is selected and the clicked date is after it.
                    // Complete the range.
                    DateSelection(start = startDate, end = clickedDate)
                }
            }
        },
        monthHeader = { month ->
            MonthHeader(month.yearMonth.displayText())
            // Days of the week header
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayOfWeek in daysOfWeek) {
                    Text(
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        // Corrected: Simple, multiplatform-safe way to display day name
                        text = dayOfWeek.name.take(1),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    )
}



@Composable
private fun Day(day: CalendarDay, selection: DateSelection, onClick: (LocalDate) -> Unit) {
    val date = day.date
    val isSelected = date >= (selection.start ?: date) && date <= (selection.end ?: date)

    val boxModifier = if (day.position == DayPosition.MonthDate) {
        val startCorner = if (date == selection.start) 50 else 0
        val endCorner = if (date == selection.end) 50 else 0
        Modifier.padding(horizontal = 2.dp).clip(
            RoundedCornerShape(
                topStartPercent = startCorner,
                bottomStartPercent = startCorner,
                topEndPercent = endCorner,
                bottomEndPercent = endCorner
            )
        )
    } else Modifier

    Box(
        modifier = Modifier
            .aspectRatio(1f) // Makes the day a square
            .then(boxModifier)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            )
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                onClick = { onClick(date) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = when {
                day.position != DayPosition.MonthDate -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                isSelected -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
private fun MonthHeader(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        textAlign = TextAlign.Center
    )
}

fun YearMonth.displayText(short: Boolean = false): String {
    val monthName = this.month.name.lowercase().replaceFirstChar { it.titlecase() }
    return "$monthName ${this.year}"
}

data class DateSelection(val start: LocalDate? = null, val end: LocalDate? = null)
*/
// --- END CALENDAR IMPLEMENTATION ---


@Composable
fun FeatureSection(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Column {
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ImageGallery(images: List<String>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(images) { imageUrl ->
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .clickable { /* Handle image click */ },
                shape = MaterialTheme.shapes.medium
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
