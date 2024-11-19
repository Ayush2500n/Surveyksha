package com.example.surveyksha

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.surveyksha.screens.FirstPage
import com.example.surveyksha.screens.FourthPage
import com.example.surveyksha.screens.LastPage
import com.example.surveyksha.screens.SecondPage
import com.example.surveyksha.screens.ThirdPage
import com.example.surveyksha.ui.theme.SurveykshaTheme
import java.io.File

class MainActivity : ComponentActivity() {

    private val RECORD_AUDIO_REQUEST_CODE = 1
    private var isPermissionGranted by mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_REQUEST_CODE)
        } else {
            isPermissionGranted = true
        }
        setContent {
            SurveykshaTheme {
                if (isPermissionGranted) {
                    val navController = rememberNavController()
                    val recorder by lazy { audioRecorder(this) }
                    val recordedFile = remember { mutableStateOf<File?>(null) }
                    val sharedViewModel = ScreensSharedViewModel()

                    NavHost(navController = navController, startDestination = "FirstPage") {
                        composable("FirstPage") { FirstPage(navController, sharedViewModel, recorder, recordedFile) }
                        composable("SecondPage") { SecondPage(navController, sharedViewModel) }
                        composable("ThirdPage") { ThirdPage(navController, sharedViewModel) }
                        composable("FourthPage") { FourthPage(navController, sharedViewModel, recorder, recordedFile) }
                        composable("LastPage") { LastPage(sharedViewModel) }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_REQUEST_CODE && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true
        }
    }
}


