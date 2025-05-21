package com.example.imagegenerator
data class TaskListResponse(
    val total: String,
    val errors: List<String>,
    val tasklist: List<TaskItem>,
    val result: Boolean
)

data class TaskItem(
    val id: String,
    val uuid: String,
    val coretaskid: String,
    val name: String,
    val socketaccesstoken: String,
    val parameters: Parameters,
    val debug: String,
    val outputs: List<OutputItem>,
    val starttime: String,
    val endtime: String,
    val elapsedseconds: String,
    val status: String,
    val cps: String,
    val totalcost: String,
    val coreuserid: String,
    val guestid: Any?, // Doğru isimlendirme
    val projectid: String,
    val modelid: String,
    val description: String,
    val basemodelid: String,
    val outputfolderid: String,
    val runtype: String,
    val modelfolderid: String,
    val modelfileid: String,
    val callbackurl: String,
    val marketplaceid: Any? // Doğru isimlendirme
)

data class Parameters(
    val inputImage: String?,          // Ekledim
    val inputImageUrl: String?,       // Ekledim
    val inputImageMask: String?,      // Ekledim
    val inputImageMaskUrl: String?,   // Ekledim
    val selectedModel: String?,       // Ekledim
    val selectedModelPrivate: String?, // Ekledim
    val prompt: String,
    val negativePrompt: String,
    val samples: String,
    val steps: String,
    val scale: String,
    val seed: String,
    val clipSkip: String,
    val width: String,
    val height: String,
    val scheduler: String,
    val strength: String?             // Ekledim
)

data class OutputItem(
    val id: String,
    val name: String,
    val url: String,
    val folderid: String,
    val foldername: String,
    val contenttype: String,
    val length: String
)
