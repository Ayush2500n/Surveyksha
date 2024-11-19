package com.example.surveyksha.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.surveyksha.ScreensSharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondPage(navController: NavHostController, sharedViewModel: ScreensSharedViewModel) {
    var age by remember {
        mutableStateOf(sharedViewModel.finalResult.value?.age?.toString() ?: "")
    }
    var isFocused by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Type your age", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = if (isFocused) age else if (age.isEmpty() || age.toInt() == 0) "" else age,
            onValueChange = {
                age = it
                showError = false // Reset error on value change
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { Text(text = "Enter your correct age") },
            modifier = Modifier
                .onFocusChanged {
                    if (it.isFocused) {
                        if (age == "0") age = ""
                        isFocused = true
                    }
                }.clip(RoundedCornerShape(12.dp)), colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            isError = showError // Highlight the TextField with error state
        )

        // Display an error message when input is invalid
        if (showError) {
            Text(
                text = "Please enter a valid age.",
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        val color = ButtonDefaults.buttonColors(
            if (age.toIntOrNull() == null || age == "0") Color.LightGray else Color.Unspecified
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Previous")
            }
            Button(
                onClick = {
                    val ageInt = age.toIntOrNull()
                    if (ageInt != null && ageInt > 0) {
                        sharedViewModel.updateAge(ageInt)
                        navController.navigate("ThirdPage")
                    } else {
                        showError = true
                    }
                },
                colors = color,
                enabled = !age.isEmpty()
            ) {
                Text(text = "Next")
            }
        }
    }
}
