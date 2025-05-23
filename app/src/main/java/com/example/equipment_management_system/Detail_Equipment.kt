package com.example.equipment_management_system

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

    // Date selection states
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // Format for displaying and sending dates
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Load equipment data when composable is created
    LaunchedEffect(Equipment_id) {
        equipment = LoginClient.getOneEquipment(Equipment_id)
        isRented = equipment?.rented ?: rented
    }


    // Date pickers
    if (showStartDatePicker) {
        val startDatePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    // Get the selected date from the state
                    startDatePickerState.selectedDateMillis?.let { millis ->
                        startDate = dateFormatter.format(Date(millis))
                    }
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    if (showEndDatePicker) {
        val endDatePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis() + 86400000 // next day
        )

        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    // Get the selected date from the state
                    endDatePickerState.selectedDateMillis?.let { millis ->
                        endDate = dateFormatter.format(Date(millis))
                    }
                    showEndDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }


   if (equipment != null) {
        Column(modifier = Modifier.fillMaxWidth()
            .verticalScroll(rememberScrollState())
        )
        {
            Card(
                onClick = { /* Do something */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                    AsyncImage(
                        model = equipment!!.image,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    )

            }
            // Equipment details section
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Name: " + equipment!!.name, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Contact Person: " + equipment!!.contact_person)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Description: \n" + equipment!!.description)
                HorizontalDivider(modifier= Modifier.padding(vertical = 8.dp) )
                Text("Location: " + equipment!!.location)
                HorizontalDivider(modifier= Modifier.padding(vertical = 8.dp))
                Text("Color: " + equipment!!.color)
                HorizontalDivider(modifier= Modifier.padding(vertical = 8.dp))
                Text("Created at: " + equipment!!.created_at)
                HorizontalDivider(modifier= Modifier.padding(vertical = 8.dp))
                Text("Modified at: " + equipment!!.modified_at)
            }

            Spacer(modifier = Modifier.height(16.dp))
            // Only show reservation button if user is logged in
            if (LoginClient.token.isNotEmpty()) {

                // Only show date selection when not rented
                if (!isRented) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Rental Period", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Start Date Selection
                        OutlinedButton(
                            onClick = { showStartDatePicker = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (startDate.isEmpty()) "Select Start Date" else "Start Date: $startDate")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // End Date Selection
                        OutlinedButton(
                            onClick = { showEndDatePicker = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (endDate.isEmpty()) "Select End Date" else "End Date: $endDate")
                        }
                    }
                }

                // Reserve/Unreserve Button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            actionInProgress = true

                            if (isRented) {
                                // Unreserve the equipment
                                actionStatus = "Canceling reservation..."
                                val response = LoginClient.unreserve_Equipment(Equipment_id)
                                if (response.startsWith("Equipment")) {
                                    actionStatus = "Reservation canceled successfully"
                                    equipment = LoginClient.getOneEquipment(Equipment_id)
                                    isRented = false
                                } else {
                                    actionStatus = "Failed to cancel reservation"
                                }
                            } else {
                                // Validate dates first
                                if (startDate.isEmpty() || endDate.isEmpty()) {
                                    actionStatus = "Please select both start and end dates"
                                } else {
                                    try {
                                        val start = dateFormatter.parse(startDate)!!
                                        val end = dateFormatter.parse(endDate)!!

                                        if (end.before(start)) {
                                            actionStatus = "End date must be after start date"
                                        } else {
                                            // Reserve the equipment with selected dates
                                            actionStatus = "Processing reservation..."
                                            val response = LoginClient.rentEquipment(startDate, endDate, Equipment_id)
                                            if (response.startsWith("Equipment")) {
                                                actionStatus = "Equipment reserved successfully"
                                                equipment = LoginClient.getOneEquipment(Equipment_id)
                                                isRented = true
                                            } else {
                                                actionStatus = "Failed to reserve the equipment"
                                            }
                                        }
                                    } catch (e: Exception) {
                                        actionStatus = "Invalid dates. Please try again."
                                    }
                                }
                            }
                            actionInProgress = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = !actionInProgress && (isRented || (startDate.isNotEmpty() && endDate.isNotEmpty()))
                ) {
                    if (actionInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(if (isRented) "Cancel Reservation" else "Reserve")
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