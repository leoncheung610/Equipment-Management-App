package com.example.equipment_management_system

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun Equipment_Detail(Equipment_id:String){
    val equipment by produceState<Equipment?>(
        initialValue = null,
        producer = {
            value = KtorClient.getOneEquipment(Equipment_id)
        }
    )
    val equipmentState = equipment
    if (equipmentState != null) {
        Card(
            onClick = { /* Do something */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            Column {
                AsyncImage(
                    model = equipmentState.image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Text(equipmentState.name)
                Text(equipmentState.description)
                // Add more details as needed
            }
        }
    } else {
        Text("Loading...")
    }

}