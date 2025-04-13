package com.example.equipment_management_system


import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

object KtorClient {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json() // enable the client to perform JSON serialization
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
    }
//    suspend fun getFeeds(): List<Equipment> {
//        return try {
//            val response: EquipmentResponse = httpClient.get("https://equipments-api.azurewebsites.net/api/equipments/").body()
//            response.equipments} catch (e: Exception) {
//            Log.e("API", "Error fetching equipment data: ${e.message}", e)
//            emptyList()
//        }
//    }

    suspend fun getFeeds(page: Int): EquipmentResponse {
        return try {
            // Add page parameter to the URL
            httpClient.get("https://equipments-api.azurewebsites.net/api/equipments/?page=$page").body()
        } catch (e: Exception) {
            Log.e("API", "Error fetching equipment data: ${e.message}", e)
            EquipmentResponse(emptyList(), 0, page, 10)
        }
    }

suspend fun getOneEquipment(id: String): Equipment? {
        return try {
            httpClient.get("https://equipments-api.azurewebsites.net/api/equipments/${id}")
                .body<Equipment>()
        } catch (e: Exception) {
            Log.e("API", "Error fetching equipment data: ${e.message}", e)
            null
        }

    }
}