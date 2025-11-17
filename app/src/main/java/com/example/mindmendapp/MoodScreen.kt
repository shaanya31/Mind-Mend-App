package com.example.mindmendapp

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodScreen() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = remember { MoodDataStore(context) }

    // UI State
    var selectedMood by remember { mutableStateOf<String?>(null) }
    var userNote by remember { mutableStateOf("") }

    // Read saved entries
    val savedEntries by dataStore.getEntriesFlow().collectAsState(initial = emptyList())

    // Gradient colors
    val GradientLavender = Color(0xFFDAD6FF)
    val GradientPink = Color(0xFFFFD6E0)
    val GradientBlue = Color(0xFFD6F0FF)

    // Create a vertical gradient brush
    val backgroundBrush = remember {
        Brush.verticalGradient(
            colors = listOf(GradientLavender, GradientPink, GradientBlue),
            startY = 0f,
            endY = 2000f
        )
    }

    // Root container: gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "How are you feeling today?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color(0xFF0F1724)
            )

            // Mood Options
            val moods = listOf("Happy", "Sad", "Anxious", "Stressed", "Angry", "Tired", "Neutral")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                moods.take(4).forEach { mood ->
                    MoodChip(mood = mood, selected = selectedMood) { selectedMood = it }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                moods.drop(4).forEach { mood ->
                    MoodChip(mood = mood, selected = selectedMood) { selectedMood = it }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Once mood selected → show AI style box
            AnimatedVisibility(
                visible = selectedMood != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                selectedMood?.let { mood ->

                    val data = LocalTemplates.getForMood(mood)

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(Color.White.copy(alpha = 0.95f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text("Affirmations:", fontWeight = FontWeight.Bold)
                            data.affirmations.forEach { a ->
                                Text("• $a", modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                            }

                            Spacer(Modifier.height(12.dp))

                            Text("Coping Tips:", fontWeight = FontWeight.Bold)
                            data.copingTips.forEach { t ->
                                Text("• $t", modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                            }

                            Spacer(Modifier.height(12.dp))

                            Text("Journaling Prompts:", fontWeight = FontWeight.Bold)
                            data.prompts.forEach { p ->
                                Text("• $p", modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                            }

                            Spacer(Modifier.height(16.dp))

                            OutlinedTextField(
                                value = userNote,
                                onValueChange = { userNote = it },
                                label = { Text("Write your thoughts…") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    scope.launch {
                                        val entry = MoodEntry(
                                            mood = mood,
                                            affirmations = data.affirmations,
                                            copingTips = data.copingTips,
                                            prompts = data.prompts,
                                            note = userNote,
                                            timestamp = System.currentTimeMillis()
                                        )
                                        dataStore.saveEntry(entry)
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

            Spacer(modifier = Modifier.height(20.dp))

            // Show recent entries
            Text(
                "Recent Reflections",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color(0xFF0F1724)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(savedEntries.take(5)) { entry ->
                    RecentEntryCard(entry)
                }
            }
        }
    }
}

// --- Helper composables ---

@Composable
fun MoodChip(mood: String, selected: String?, onSelect: (String) -> Unit) {
    val isSelected = mood == selected
    Surface(
        color = if (isSelected) Color(0xFFB0E0E6) else Color.White,
        shadowElevation = if (isSelected) 8.dp else 4.dp,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(4.dp)
            .clickable { onSelect(mood) }
    ) {
        Text(
            text = mood,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            fontSize = 14.sp
        )
    }
}

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

            Text("${entry.mood} — ${formatter.format(Date(entry.timestamp))}",
                fontWeight = FontWeight.Bold
            )

            if (!entry.note.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text("Note: ${entry.note}")
            }
        }
    }
}
