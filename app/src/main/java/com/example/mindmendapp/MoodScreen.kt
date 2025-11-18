package com.example.mindmendapp

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindmendapp.datastore.MoodDataStore
import com.example.mindmendapp.model.MoodEntry
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// --- EMOJIS ---
private val MOOD_EMOJIS = mapOf(
    "Happy" to "😊",
    "Sad" to "😢",
    "Anxious" to "😰",
    "Stressed" to "😫",
    "Angry" to "😡",
    "Tired" to "😴",
    "Neutral" to "😐"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = remember { MoodDataStore(context) }

    var selectedMood by remember { mutableStateOf<String?>(null) }
    var userNote by remember { mutableStateOf("") }

    val savedEntries by dataStore.getEntriesFlow().collectAsState(initial = emptyList())

    // gradient background
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFDAD6FF),
            Color(0xFFFFD6E0),
            Color(0xFFD6F0FF)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "How are you feeling today?",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F1724),
                modifier = Modifier.padding(vertical = 6.dp)
            )

            val moods = listOf("Happy", "Sad", "Anxious", "Stressed", "Angry", "Tired", "Neutral")

            // Chip sizing modifier (prevents vertical wrapping)
            val chipBaseModifier = Modifier
                .widthIn(min = 96.dp)   // <- prevents long labels from wrapping vertically
                .padding(vertical = 4.dp)

            // ---- Row 1: horizontally scrollable (safe on narrow screens) ----
            LazyVerticalGrid(
                columns = GridCells.Adaptive(120.dp),
                modifier = Modifier.wrapContentWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(moods) { mood ->
                    MoodChip(
                        mood = mood,
                        selected = selectedMood,
                        onSelect = { selectedMood = it },
                        modifier = chipBaseModifier
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ---- Row 2: horizontally scrollable ----
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .horizontalScroll(rememberScrollState()),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Spacer(modifier = Modifier.width(6.dp))
//                moods.drop(4).forEach { mood ->
//                    MoodChip(
//                        mood = mood,
//                        selected = selectedMood,
//                        onSelect = { selectedMood = it },
//                        modifier = chipBaseModifier
//                    )
//                }
//                Spacer(modifier = Modifier.width(6.dp))
//            }

            Spacer(modifier = Modifier.height(8.dp))

            // ---- AI CARD ----
            AnimatedVisibility(
                visible = selectedMood != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.wrapContentHeight()
            ) {
                selectedMood?.let { mood ->
                    val data = LocalTemplates.getForMood(mood)

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Affirmations:", fontWeight = FontWeight.Bold)
                            data.affirmations.forEach {
                                Text("• $it", modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Coping Tips:", fontWeight = FontWeight.Bold)
                            data.copingTips.forEach {
                                Text("• $it", modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Journaling Prompts:", fontWeight = FontWeight.Bold)
                            data.prompts.forEach {
                                Text("• $it", modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = userNote,
                                onValueChange = { userNote = it },
                                label = { Text("Write your thoughts…") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    scope.launch {
                                        dataStore.saveEntry(
                                            MoodEntry(
                                                mood = mood,
                                                affirmations = data.affirmations,
                                                copingTips = data.copingTips,
                                                prompts = data.prompts,
                                                note = userNote,
                                                timestamp = System.currentTimeMillis()
                                            )
                                        )
                                    }
                                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                                    userNote = ""
                                    selectedMood = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Save Entry")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "Recent Reflections",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color(0xFF0F1724)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(savedEntries.take(5)) { entry ->
                    RecentEntryCard(entry)
                }
            }
        }
    }
}

// ---------------- MoodChip ----------------

@Composable
fun MoodChip(
    mood: String,
    selected: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier // allow sizing passed from caller
) {

    val isSelected = mood == selected

    val scale by animateFloatAsState(if (isSelected) 1.10f else 1f, animationSpec = tween(160))
    val elevation by animateDpAsState(if (isSelected) 10.dp else 4.dp)
    val bgColor by animateColorAsState(if (isSelected) Color(0xFF9DEBCF) else Color.White)

    val textColor = if (isSelected) Color(0xFF04372A) else Color(0xFF0F1724)
    val emoji = MOOD_EMOJIS[mood] ?: ""

    // gentle pulsing halo when selected
    val infinite = rememberInfiniteTransition()
    val pulseScale by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(animation = tween(900), repeatMode = RepeatMode.Reverse)
    )
    val pulseAlpha by infinite.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.06f,
        animationSpec = infiniteRepeatable(animation = tween(900), repeatMode = RepeatMode.Reverse)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(4.dp)
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .scale(pulseScale)
                    .background(color = bgColor.copy(alpha = pulseAlpha), shape = CircleShape)
            )
        }

        Surface(
            color = bgColor,
            shadowElevation = elevation,
            shape = RoundedCornerShape(22.dp),
            modifier = modifier
                .width(120.dp)
                .scale(scale)
                .clickable { onSelect(mood) }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                if (emoji.isNotEmpty()) {
                    Text(emoji, fontSize = 17.sp, modifier = Modifier.padding(end = 6.dp))
                }
                Text(mood, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textColor)
            }
        }
    }
}

// ---------------- Recent Entry ----------------

@Composable
fun RecentEntryCard(entry: MoodEntry) {
    val formatter = SimpleDateFormat("hh:mm a • dd MMM", Locale.getDefault())

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("${entry.mood} — ${formatter.format(Date(entry.timestamp))}", fontWeight = FontWeight.Bold)
            if (!entry.note.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text("Note: ${entry.note}")
            }
        }
    }
}

// ---------------- GlassCard ----------------

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {

    val color = Color.White.copy(alpha = 0.70f)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), content = content)
    }
}
