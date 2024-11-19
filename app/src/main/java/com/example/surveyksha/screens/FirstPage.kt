package com.example.surveyksha.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.surveyksha.ScreensSharedViewModel
import com.example.surveyksha.audioRecorder
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstPage(
    navController: NavHostController,
    sharedViewModel: ScreensSharedViewModel,
    recorder: audioRecorder,
    recordedFile: MutableState<File?>
) {
    val genders = mapOf("id1" to "Male", "id2" to "Female", "id3" to "Other")
    val context = LocalContext.current

    recordedFile.value = File(context.cacheDir, "audio.wav").also {
        recorder.start(it)
    }
    val selectedGender = sharedViewModel.finalResult.value?.gender ?: emptyMap()

    var expanded by remember {
        mutableStateOf(false)
    }
    var itemClicked by remember {
        mutableStateOf("")
    }
    val icon = if (expanded){
        Icons.Filled.KeyboardArrowUp
    }else{
        Icons.Filled.KeyboardArrowDown
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = "Select your gender", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(20.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }) {
                        TextField(
                            value = if (selectedGender.isEmpty()) "Choose the correct option" else selectedGender.values.first(),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(imageVector = icon, contentDescription = null)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 40.dp)
                                .menuAnchor().clip(RoundedCornerShape(12.dp)),
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)) {
                    genders.forEach { (id, value) ->
                        DropdownMenuItem(
                            text = { Text(text = value) },
                            onClick = {
                                sharedViewModel.updateGender(mapOf(id to value))
                                itemClicked = id
                                expanded = false }
                        )
                    }
                }
            }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { selectedGender?.let { sharedViewModel.updateGender(it) }
            navController.navigate("SecondPage") }) {
            Text(text = "Next")
        }
        }
}