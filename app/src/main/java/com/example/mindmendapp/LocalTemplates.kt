package com.example.mindmendapp

object LocalTemplates {

    data class ResponseBundle(
        val affirmations: List<String>,
        val copingTips: List<String>,
        val prompts: List<String>
    )

    private val templates = mapOf(
        "Happy" to ResponseBundle(
            affirmations = listOf("Your joy is powerful.", "This happiness is deserved."),
            copingTips = listOf("Share a smile with someone.", "Pause and embrace this moment."),
            prompts = listOf("What made you smile today?", "How can you extend this feeling?")
        ),
        "Sad" to ResponseBundle(
            affirmations = listOf("It’s okay to feel sad.", "You’re stronger than you think."),
            copingTips = listOf("Drink some water.", "Take a slow deep breath for 10 seconds."),
            prompts = listOf("What’s one small comfort you can give yourself?", "What do you wish someone told you right now?")
        ),
        "Anxious" to ResponseBundle(
            affirmations = listOf("You are safe right now.", "This feeling will pass."),
            copingTips = listOf("Try the 4-7-8 breathing method.", "Relax your shoulders."),
            prompts = listOf("What triggered this feeling?", "Is it something you can control?")
        ),
        "Stressed" to ResponseBundle(
            affirmations = listOf("You’re doing your best.", "Small progress is still progress."),
            copingTips = listOf("Take a 1-minute break.", "Stretch your neck and hands."),
            prompts = listOf("What’s the smallest next step you can take?", "What can wait till later?")
        ),
        "Angry" to ResponseBundle(
            affirmations = listOf("Your feelings are valid.", "It’s okay to take space."),
            copingTips = listOf("Count 1–10 slowly.", "Walk for 1 minute."),
            prompts = listOf("What caused this anger?", "What outcome do you want most?")
        ),
        "Tired" to ResponseBundle(
            affirmations = listOf("You deserve rest.", "Your body is asking for care."),
            copingTips = listOf("Drink water.", "Stretch for 20 seconds."),
            prompts = listOf("What helps you recharge?", "How many hours did you sleep?")
        ),
        "Neutral" to ResponseBundle(
            affirmations = listOf("Being neutral is okay.", "Today can still be meaningful."),
            copingTips = listOf("Try a short walk.", "Do a 30-second breathing exercise."),
            prompts = listOf("What could make today 5% better?", "What do you want your day to feel like?")
        )
    )

    fun getForMood(mood: String): ResponseBundle {
        return templates[mood] ?: templates["Neutral"]!!
    }
}