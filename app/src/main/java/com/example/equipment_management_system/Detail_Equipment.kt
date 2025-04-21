package com.example.equipment_management_system

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Equipment_Detail(navController: NavController, Equipment_id: String,rented:Boolean) {
    // Equipment data state
    var equipment by remember { mutableStateOf<Equipment?>(null) }
    var isRented by remember { mutableStateOf(rented) }
    // Loading states
    var isLoading by remember { mutableStateOf(true) }
    var actionInProgress by remember { mutableStateOf(false) }
    // Status message
    var actionStatus by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()


    // Load equipment data when composable is created
    LaunchedEffect(Equipment_id) {
        equipment = LoginClient.getOneEquipment(Equipment_id)
        isRented = equipment?.rented ?: rented

    }

   if (equipment != null) {
        Column(modifier = Modifier.fillMaxWidth())
        {
            TopAppBar(
                title = { Text("${equipment?.name} Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
            Card(
                onClick = { /* Do something */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Column {
                    AsyncImage(
                        model = equipment!!.image,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Text(equipment!!.name)
                    Text(equipment!!.description)
                    // Add more details as needed
                }
            }

            // Only show reservation button if user is logged in
            if (LoginClient.token.isNotEmpty()) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            actionInProgress = true

                            if (isRented) {
                                // Unreserve the equipment
                                actionStatus = "Canceling reservation..."
                                val response= LoginClient.unreserve_Equipment(Equipment_id)
                                if (response.startsWith("Equipment")){
                                    actionStatus = "Equipment reserved successfully"
                                    equipment = LoginClient.getOneEquipment(Equipment_id)
                                    isRented=false
                                }
                                else {
                                    actionStatus="Fail to reserve the equipment"
                                }
                            } else {
                            // Reserve the equipment
                            actionStatus = "Processing reservation..."
                            // Fix: Use correct equipment ID and formatted dates
                            val today = "2025-04-13"
                            val tomorrow = "2025-04-14"
                            val response = LoginClient.rentEquipment(today, tomorrow, Equipment_id)
                            if (response.startsWith("Equipment")) {
                                actionStatus = "Equipment reserved successfully"
                                equipment = LoginClient.getOneEquipment(Equipment_id)
                                isRented=true
                            } else {
                                actionStatus = "Fail to reserve the equipment"
                            }

                        }
                            actionInProgress = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = !actionInProgress
                ) {
                    if (actionInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(if (isRented) "Unreserve" else "Reserve")
                    }
                }

                // Show status message
                if (actionStatus.isNotEmpty()) {
                    Text(
                        text = actionStatus,
                        color = when {
                            actionStatus.contains("Processing") || actionStatus.contains("Canceling") -> Color.Blue
                            actionStatus.contains("successfully") -> Color.Green
                            else -> Color.Red
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    } else {
        Text("Failed to load equipment details")
    }
}
//@Composable
//fun Equipment_Detail(Equipment_id:String){
//    val equipment by produceState<Equipment?>(
//        initialValue = null,
//        producer = {
//            value = KtorClient.getOneEquipment(Equipment_id)
//        }
//    )
//    val equipmentState = equipment
//    if (equipmentState != null) {
//        Card(
//            onClick = { /* Do something */ },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(300.dp)
//        ) {
//            Column {
//                AsyncImage(
//                    model = equipmentState.image,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                )
//                Text(equipmentState.name)
//                Text(equipmentState.description)
//                // Add more details as needed
//            }
//        }
//    } else {
//        Text("Loading...")
//    }
//
//}