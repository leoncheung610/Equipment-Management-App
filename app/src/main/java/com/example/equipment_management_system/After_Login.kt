package com.example.equipment_management_system

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun After_login_screen(navController: NavController) {
    var isLoading by remember { mutableStateOf(true) }
    var isLoggingOut by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    // Create a flow producer in a way that survives recompositions
    val rentedFlow = remember { LoginClient.getAllRentedByUser() }

    // Collect the flow as state with an initial empty list
    val rented_equipments by rentedFlow.collectAsState(initial = emptyList())

    // Load data when the screen is first shown
    // Update isLoading when locations change OR after a timeout
    LaunchedEffect(rented_equipments) {
        if (rented_equipments.isNotEmpty()) {
            isLoading = false
        }
    }

    // Add this additional LaunchedEffect for timeout
    LaunchedEffect(Unit) {
        // Set a timeout to stop loading after 3 seconds regardless of result
        delay(6000)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Rented Equipment") },
                actions = {
                    // Option 1: Simple Button with clear text
                    Button(
                        onClick = {
                            isLoggingOut = true
                            coroutineScope.launch {
                                val result = LoginClient.logout()
                                isLoggingOut = false
                                navController.navigate("user") {
                                    popUpTo("user") { inclusive = true }
                                }
                            }
                        },
                        enabled = !isLoggingOut,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        if (isLoggingOut) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Logout")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        // Content area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                // Loading indicator
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (rented_equipments.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "You haven't rented any equipment yet",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.navigate("Highlight_Equipment") }) {
                            Text("Browse Equipment")
                        }
                    }
                }
            } else {
                // Equipment list
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(rented_equipments) { equipment ->
                        Card(
                            onClick = { navController.navigate("equipments/${equipment._id}/${equipment.rented}") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(8.dp)
                        ) {
                            Column {
                                AsyncImage(
                                    model = equipment.image,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )
                                Box(Modifier.fillMaxSize()) {
                                    Text(equipment.name, Modifier.align(Alignment.Center))
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}
