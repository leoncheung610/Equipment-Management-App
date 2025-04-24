package com.example.equipment_management_system


import android.R.attr.onClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import kotlinx.serialization.Serializable
import kotlin.math.ceil



@Composable
fun Search_Equipment(navController: NavController) {
    var currentPage by remember { mutableStateOf(1) }
    var totalPages by remember { mutableStateOf(1) }
    var keyword by remember {mutableStateOf("") }

    val equipmentResponse by produceState(
        initialValue = EquipmentResponse(emptyList(), 0, 1, 10),
        key1 = currentPage, // Reload when page changes
        key2= keyword,
        producer = {
            value = LoginClient.search_equip(keyword,currentPage)
            totalPages = ceil(value.total.toFloat() / value.perPage).toInt()
        }
    )

    Column {
        // Enhanced search field with descriptive elements
        OutlinedTextField(
            value = keyword,
            onValueChange = { keyword = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            label = { Text("Search Equipment") },
            placeholder = { Text("Enter name") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (keyword.isNotEmpty()) {
                    IconButton(onClick = { keyword = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                // Reset to page 1 when searching
                currentPage = 1
            })
        )
        Spacer(Modifier.size(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(equipmentResponse.equipments) { feed ->
                Card(
                    onClick = { navController.navigate("equipments/${feed._id}/${feed.rented}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(8.dp)
                ) {
                    Column {
                        AsyncImage(
                            model = feed.image,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                        Box(Modifier.fillMaxSize()) {
                            Text(feed.name, Modifier.align(Alignment.Center))
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
        //Pagination
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous button
            Button(
                onClick = { if (currentPage > 1) currentPage-- },
                enabled = currentPage > 1
            ) {
                Text("Previous")
            }

            // Page indicators
            Text(
                text = "Page $currentPage of $totalPages",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Next button
            Button(
                onClick = { if (currentPage < totalPages) currentPage++ },
                enabled = currentPage < totalPages
            ) {
                Text("Next")
            }
        }
    }
}



//    val feeds by produceState(
//        initialValue = listOf<Equipment>(),
//        producer = {
//            value = KtorClient.getFeeds()
//        }
//    )
//    LazyColumn {
//        items(feeds) { feed ->
//            Card(
//                onClick = { navController.navigate("equipments/${feed._id}") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(300.dp)
//            ) {
//                Column {
//                    AsyncImage(
//                        model = feed.image,
//                        contentDescription = null,
//                        modifier = Modifier.fillMaxWidth()
//                            .height(200.dp)
//                    )
//                    Box(Modifier.fillMaxSize()) {
//                        Text(feed.name, Modifier.align(Alignment.Center))
//                    }
//                }
//                HorizontalDivider()
//            }
//        }
//    }
//}


