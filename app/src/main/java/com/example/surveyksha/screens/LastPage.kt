package com.example.surveyksha.screens

import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.surveyksha.FinalResult
import com.example.surveyksha.ScreensSharedViewModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LastPage(sharedViewModel: ScreensSharedViewModel) {
    val submissions = sharedViewModel.submissions.value
    val context = LocalContext.current
    sharedViewModel.updateSubmission(context)
    val submissionList = submissions.mapNotNull { jsonString ->
        try {
            val jsonObject = Json.parseToJsonElement(jsonString.toString()).jsonObject
            parseSubmission(jsonObject)
        } catch (e: Exception) {
            Log.e("Parsing Error", "Failed to parse JSON: ${e.message}")
            null
        }
    }

    LazyRow(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Gender", Modifier.weight(0.4f), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Age", Modifier.weight(0.4f), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Selfie", Modifier.weight(0.4f), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Recording", Modifier.weight(0.4f), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("GPS", Modifier.weight(0.4f), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Submit Time", Modifier.weight(0.4f), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(20.dp))
        }

        items(submissionList.size) { index ->
            val submission = submissionList[index]
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                val gender = submission.gender?.entries?.joinToString { "${it.key}: ${it.value}" } ?: "Unknown"
                Text(gender, Modifier.weight(0.4f))

                Text(submission.age.toString(), Modifier.weight(0.4f))

                submission.selfie?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Selfie",
                        modifier = Modifier
                            .size(50.dp)
                            .weight(0.4f)
                    )
                } ?: Text("No Selfie", Modifier.weight(1f))

                Text(submission.recording?.path ?: "No Recording", Modifier.weight(0.4f))

                val latitude = submission.gps["Latitude"] ?: "Unknown"
                val longitude = submission.gps["Longitude"] ?: "Unknown"
                Text("Lat: $latitude, Long: $longitude", Modifier.weight(0.4f))

                Text(submission.submit_time?.toString() ?: "No Submit Time", Modifier.weight(0.4f))
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun parseSubmission(json: JsonObject): FinalResult? {
    return try {
        val gender = json["Q1"]?.jsonPrimitive?.content?.let { parseGender(it) }
        val age = json["Q2"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0
        val selfiePath = json["Q3"]?.jsonPrimitive?.content
        val selfie = selfiePath?.let { BitmapFactory.decodeFile(it) }
        val recordingPath = json["recording"]?.jsonPrimitive?.content
        val recording = recordingPath?.let { File(it) }
        val gps = json["gps"]?.jsonPrimitive?.content?.let { parseGps(it) } ?: emptyMap()
        val submitTime = json["submit_time"]?.jsonPrimitive?.content?.let { Instant.parse(it) }

        FinalResult(gender, age, selfie, recording, gps, submitTime)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun parseGender(genderString: String): Map<String, String> {
    return genderString.removeSurrounding("{", "}")
        .split(", ")
        .associate {
            val (key, value) = it.split("=")
            key to value
        }
}

fun parseGps(gpsString: String): Map<Any, Any> {
    return gpsString.removeSurrounding("{", "}")
        .split(", ")
        .associate {
            val (key, value) = it.split("=")
            ((key to value.toDoubleOrNull()) ?: 0.0) as Pair<Any, Any>
        }
}