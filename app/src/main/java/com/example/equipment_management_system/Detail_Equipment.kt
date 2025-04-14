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
fun Equipment_Detail(navController: NavController, Equipment_id: String) {
    // Equipment data state
    var equipment by remember { mutableStateOf<Equipment?>(null) }
    // Track if the user has already rented this equipment
    var isRented by remember { mutableStateOf(false) }
    // Loading states
    var isLoading by remember { mutableStateOf(true) }
    var actionInProgress by remember { mutableStateOf(false) }
    // Status message
    var actionStatus by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    // Function to check if user has rented this equipment
    suspend fun checkRentalStatus() {
        if (LoginClient.token.isEmpty()) return

        try {
            // Make a GET request to check rental status
            val response = LoginClient.httpClient.get("https://equipments-api.azurewebsites.net/api/equipments/${Equipment_id}/rent") {
                header("Authorization", "Bearer ${LoginClient.token}")
            }
            // If request succeeds, user has rented this equipment
            isRented = response.status.isSuccess()
        } catch (e: Exception) {
            // If request fails, user hasn't rented this equipment
            isRented = false
            Log.e("API", "Error checking rental status: ${e.message}")
        }
    }

    // Load equipment data when composable is created
    LaunchedEffect(Equipment_id) {
        equipment = KtorClient.getOneEquipment(Equipment_id)
        // Only check rental status if user is logged in
        if (LoginClient.token.isNotEmpty()) {
            checkRentalStatus()
        }

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
                                try {
                                    LoginClient.httpClient.delete("https://equipments-api.azurewebsites.net/api/equipments/${Equipment_id}/rent") {
                                        header("Authorization", "Bearer ${LoginClient.token}")
                                    }
                                    actionStatus = "Equipment unreserved successfully"
                                    isRented = false
                                } catch (e: Exception) {
                                    actionStatus = "Failed to unreserve: ${e.message}"
                                    Log.e("API", "Unreserve error: ${e.message}", e)
                                }
                            } else {
                                // Reserve the equipment
                                actionStatus = "Processing reservation..."
                                try {
                                    // Fix: Use correct equipment ID and formatted dates
                                    val today = "2025-04-13"
                                    val tomorrow = "2025-04-14"
                                    val rentalRequest = LoginClient.RentalRequest(today, tomorrow)

                                    LoginClient.httpClient.post("https://equipments-api.azurewebsites.net/api/equipments/${Equipment_id}/rent") {
                                        header("Authorization", "Bearer ${LoginClient.token}")
                                        contentType(ContentType.Application.Json)
                                        setBody(rentalRequest)
                                    }
                                    actionStatus = "Equipment reserved successfully"
                                    isRented = true
                                } catch (e: Exception) {
                                    actionStatus = "Failed to reserve: ${e.message}"
                                    Log.e("API", "Rental error: ${e.message}", e)
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