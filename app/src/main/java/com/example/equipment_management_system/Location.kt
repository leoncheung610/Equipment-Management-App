package com.example.equipment_management_system

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlin.math.ceil


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(navController: NavController) {

    // Create a flow producer in a way that survives recompositions
    val locationsFlow = remember { LoginClient.getAllLocations() }


    // Collect the flow as state with an initial empty list
    val locations by locationsFlow.collectAsState(initial = emptyList())

    // Determine if we're still loading based on whether we have locations
    var isLoading by remember { mutableStateOf(true) }

    // Update isLoading when locations change
    LaunchedEffect(locations) {
        if (locations.isNotEmpty()) {
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(locations) { location ->
                    ListItem(
                        headlineContent = {Text(location)},
                        modifier= Modifier.clickable{
                            navController.navigate("location/${location}")
                        },
                        leadingContent = {
                            Icon(
                                Icons.Filled.Place,
                                contentDescription = null
                            )
                        }


                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetailScreen(navController: NavController, location: String) {
    var currentPage by remember { mutableStateOf(1) }
    var totalPages by remember { mutableStateOf(1) }

    val equipmentResponse by produceState(
        initialValue = EquipmentResponse(emptyList(), 0, 1, 10),
        key1 = currentPage,
        key2 = location,
        producer = {
            value = LoginClient.getEquipmentByLocation(location, currentPage)
            totalPages = ceil(value.total.toFloat() / value.perPage).toInt()
        }
    )

    Column(modifier = Modifier.fillMaxSize()) {
//
        // Equipment list
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(equipmentResponse.equipments) { equipment ->
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

        // Pagination
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { if (currentPage > 1) currentPage-- },
                enabled = currentPage > 1
            ) {
                Text("Previous")
            }

            Text(
                text = "Page $currentPage of $totalPages",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Button(
                onClick = { if (currentPage < totalPages) currentPage++ },
                enabled = currentPage < totalPages
            ) {
                Text("Next")
            }
        }
    }
}
//@Composable
//fun LocationItem(location: String, onClick: () -> Unit) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp)
//            .clickable(onClick = onClick),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                imageVector = Icons.Default.Place,
//                contentDescription = null,
//                tint = MaterialTheme.colorScheme.primary
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//            Text(
//                text = location,
//                style = MaterialTheme.typography.titleMedium
//            )
//            Spacer(modifier = Modifier.weight(1f))
//            Icon(
//                imageVector = Icons.Default.KeyboardArrowRight,
//                contentDescription = null
//            )
//        }
//    }
//}