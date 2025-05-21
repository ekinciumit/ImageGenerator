package com.example.imagegenerator

data class PromptResponse(
    val errors: List<String>,

    val coreworkertaskid: String,

    val coretaskqueueid: String,

    val userid: Int,

    val taskid: String,

    val socketaccesstoken: String,

    val modelslugowner: String,

    val modelslugproject: String,

    val status: String,

    val result: Boolean,
)
