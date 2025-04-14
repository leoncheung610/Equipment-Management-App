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
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun Register(navController: NavController){
    val padding = 16.dp
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var remark by remember { mutableStateOf("") }

    var registerStatus by remember { mutableStateOf("") }
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
        Text("Contact Number")
        TextField(
            maxLines = 1,
            value = contact,
            onValueChange = { contact = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.size(padding))

        Text("Department")
        TextField(
            maxLines = 1,
            value = department,
            onValueChange = { department = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.size(padding))

        Text("Remark")
        TextField(
            maxLines = 1,
            value = remark,
            onValueChange = { remark = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.size(padding))

        Button(onClick = {
            coroutineScope.launch {
                registerStatus = LoginClient.register(email, password,contact,department,remark)
                Log.i("Register", registerStatus)
            }
        }) {
            Text(text = "Register")
        }


        // Show login status
        if (registerStatus.isNotEmpty()) {
            Text(
                text = registerStatus,
                color = if (registerStatus.startsWith("Register successful"))
                    Color.Green else Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )

        }
    }
}