package com.example.mindmendapp.model

data class MoodEntry(
    val mood: String,
    val affirmations: List<String>,
    val copingTips: List<String>,
    val prompts: List<String>,
    val note: String?,
    val timestamp: Long
)
