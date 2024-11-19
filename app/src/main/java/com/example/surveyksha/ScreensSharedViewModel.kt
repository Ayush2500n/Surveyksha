package com.example.surveyksha

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.time.Instant

class ScreensSharedViewModel: ViewModel() {
    private val _finalResult = mutableStateOf(FinalResult())
    val finalResult: State<FinalResult?> get() = _finalResult

    private val _submissions = mutableStateOf<List<String>>(emptyList())
    val submissions: State<List<String>> get() = _submissions

    var selfieLocation by mutableStateOf("")

    var _audioLocation by mutableStateOf("")

    fun updateSubmission(context: Context){
        _submissions.value = readAllSubmissions(context)
    }
    fun updateGender(gender: Map<String, String>){
        _finalResult.value = _finalResult.value.copy(gender = gender)
    }
    fun updateAge(age: Int){
        _finalResult.value = _finalResult.value.copy(age = age)
    }
    fun updateSelfie(selfie: Bitmap){
        _finalResult.value = _finalResult.value.copy(selfie = selfie)
    }
    fun updateRecording(recording: File){
        _finalResult.value = _finalResult.value.copy(recording = recording)
    }
    fun updateGps(gps: Map<Any, Any>){
        _finalResult.value = _finalResult.value.copy(gps = gps)
    }
    fun updateTimestamp(time: Instant){
        _finalResult.value = _finalResult.value.copy(submit_time = time)
    }

    fun saveSelfie(context: Context): String {
        val directory = File(context.filesDir, "ContactFormSubmissions")
        if (!directory.exists()) {
            directory.mkdir()
        }

        val fileName = "selfie_${System.currentTimeMillis()}.png"
        val file = File(directory, fileName)

        FileOutputStream(file).use { output ->
            finalResult.value?.selfie?.compress(Bitmap.CompressFormat.PNG, 100, output)
        }

        return file.absolutePath
    }
    fun saveAudioRecording(context: Context): String {
        val directory = File(context.filesDir, "ContactFormSubmissions")
        if (!directory.exists()) {
            directory.mkdir()
        }

        val newFile = File(directory, "audio_${System.currentTimeMillis()}.wav")
        finalResult.value?.recording?.copyTo(newFile, overwrite = true)

        return newFile.absolutePath
    }
    fun saveSubmission(context: Context) {
        val formData = JSONObject().apply {
            put("Q1", finalResult.value?.gender)
            put("Q2", finalResult.value?.age)
            put("Q3", selfieLocation)
            put("recording", _audioLocation)
            put("gps", finalResult.value?.gps)
            put("submit_time", finalResult.value?.submit_time)
        }

        saveFormData(context, formData)

        _submissions.value = readAllSubmissions(context)
    }


    fun saveFormData(context: Context, formData: JSONObject) {
        val directory = File(context.filesDir, "ContactFormSubmissions")
        if (!directory.exists()) {
            directory.mkdir()
        }

        val fileName = "submission_${System.currentTimeMillis()}.json"
        val file = File(directory, fileName)

        FileOutputStream(file).use { output ->
            output.write(formData.toString().toByteArray())
        }
    }
    fun readAllSubmissions(context: Context): List<String> {
        val directory = File(context.filesDir, "ContactFormSubmissions")
        val submissions = mutableListOf<String>()

        directory.listFiles()?.filter { it.extension == "json" }?.forEach { file ->
            submissions.add(file.readText())
        }

        return submissions
    }



}