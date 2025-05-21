package com.example.imagegenerator

data class PromptRequest(
    val prompt: String,
    val negativePrompt: String,
    val samples: String,
    val steps: String,
    val scale: String,
    val seed: String,
    val clipSkip: String,
    val width: String,
    val height: String,
    val scheduler: String
)
