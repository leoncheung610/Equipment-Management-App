package com.example.equipment_management_system

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun loginInterface(){
    val padding = 16.dp
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginStatus by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Email")
        TextField(
            maxLines = 1,
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.size(padding))

        Text("Password")
        TextField(
            maxLines = 1,
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.size(padding))

        Button(onClick = {
            coroutineScope.launch {
                loginStatus = LoginClient.login(email, password)
                Log.i("Login", loginStatus)
            }
        }) {
            Text(text = "Login")
        }

        // Show login status
        if (loginStatus.isNotEmpty()) {
            Text(
                text = loginStatus,
                color = if (loginStatus.startsWith("Login successful"))
                    Color.Green else Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}