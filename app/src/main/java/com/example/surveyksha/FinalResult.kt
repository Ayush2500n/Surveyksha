package com.example.surveyksha

import android.graphics.Bitmap
import java.io.File
import java.time.Instant

data class FinalResult(
    var gender: Map<String, String>? = null,
    val age: Int = 0,
    val selfie: Bitmap? = null,
    val recording: File? = null,
    val gps: Map<Any, Any> = emptyMap(),
    val submit_time: Instant? = null
)
