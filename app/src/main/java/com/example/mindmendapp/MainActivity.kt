package com.example.mindmendapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.mindmendapp.ui.theme.MindMendAppTheme
import kotlinx.coroutines.delay
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindMendAppTheme {
                Surface(color = Color(0xFFF6F8F9)) {
                    AppWithSplash()
                }
            }
        }
    }
}

@Composable
fun AppWithSplash() {
    var showSplash by remember { mutableStateOf(true) }

    // hide splash after 1200ms
    LaunchedEffect(Unit) {
        delay(1200)
        showSplash = false
    }

    AnimatedVisibility(
        visible = showSplash,
        enter = fadeIn(animationSpec = tween(400)),
        exit = fadeOut(animationSpec = tween(400))
    ) {
        // simple text splash — replace with Image if you have a logo drawable
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "MindMend",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }

    AnimatedVisibility(
        visible = !showSplash,
        enter = fadeIn(animationSpec = tween(450))
    ) {
        MoodScreen()
    }
}
