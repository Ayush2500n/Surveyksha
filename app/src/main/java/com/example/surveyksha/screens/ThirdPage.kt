package com.example.surveyksha.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.surveyksha.ScreensSharedViewModel

@Composable
fun ThirdPage(navController: NavHostController, sharedViewModel: ScreensSharedViewModel) {
    var selfie by remember {
        mutableStateOf(sharedViewModel.finalResult.value?.selfie)
    }
    val cameraLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicturePreview(), onResult = { selfie = it
    })
    val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(), onResult = {
        if (it){
            cameraLauncher.launch(null)
        }
    })
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        selfie?.asImageBitmap()?.let { Image(bitmap = it, contentDescription = null, modifier = Modifier.size(100.dp)) }
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = { permissionLauncher.launch("android.permission.CAMERA") }) {
            Text(text = "Upload your selfie")
        }
        Spacer(modifier = Modifier.height(40.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Previous")
            }
            Button(onClick = { selfie?.let { sharedViewModel.updateSelfie(it) }
                navController.navigate("FourthPage") }) {
                Text(text = "Next")
            }
        }
    }
}
