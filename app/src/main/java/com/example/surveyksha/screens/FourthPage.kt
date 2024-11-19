package com.example.surveyksha.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.surveyksha.ScreensSharedViewModel
import com.example.surveyksha.audioRecorder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FourthPage(
    navController: NavHostController,
    sharedViewModel: ScreensSharedViewModel,
    recorder: audioRecorder,
    recordedFile: MutableState<File?>
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val permissionsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            Log.i("Permission", "Location permission granted")
        } else {
            Log.e("Permission", "Location permission denied")
            Toast.makeText(context, "Location permission is required. Please enable it in settings.", Toast.LENGTH_LONG).show()
        }
    }



    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                recorder.stop()
                recordedFile.value?.let { sharedViewModel.updateRecording(it) }

                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    val timestamp = Instant.now()

                    getCurrentLocation(fusedLocationClient) { location ->
                        location?.let {
                            sharedViewModel.updateGps(
                                mapOf(
                                    "Longitude" to it.longitude,
                                    "Latitude" to it.latitude
                                )
                            )
                            sharedViewModel.updateTimestamp(timestamp)
                            sharedViewModel.selfieLocation = sharedViewModel.saveSelfie(context)
                            sharedViewModel._audioLocation = sharedViewModel.saveAudioRecording(context)
                            sharedViewModel.saveSubmission(context)
                        } ?: Log.e("LocationError", "Location is null")
                    }
                    navController.navigate("LastPage")
                } else {
                    permissionsLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                }
            }
        ) {
            Text(text = "Submit")
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Previous")
        }
    }
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (Location?) -> Unit
) {
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            onLocationReceived(location)
        }
        .addOnFailureListener { e ->
            Log.e("LocationError", "Failed to fetch location", e)
            onLocationReceived(null)
        }
}
