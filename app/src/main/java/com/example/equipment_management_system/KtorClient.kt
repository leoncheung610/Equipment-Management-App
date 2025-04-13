package com.example.equipment_management_system


import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable

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

    suspend fun getFeeds(page: Int): EquipmentResponse {
        return try {
            // Add page parameter to the URL
            httpClient.get("https://equipments-api.azurewebsites.net/api/equipments/?page=$page").body()
        } catch (e: Exception) {
            Log.e("API", "Error fetching equipment data: ${e.message}", e)
            EquipmentResponse(emptyList(), 0, page, 10)
        }
    }
    suspend fun search_equip(keyword:String,page: Int): EquipmentResponse {
        return try {
            // Add page parameter to the URL
            httpClient.get("https://equipments-api.azurewebsites.net/api/equipments/?keyword=$keyword&?page=$page").body()
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
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String
)

object LoginClient{
    var token: String = ""
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json() // enable the client to perform JSON serialization
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
    }

    suspend fun login(email: String, password: String): String {
        return try{val loginRequest = LoginRequest(email, password)

        val response: LoginResponse = this.httpClient.post("https://equipments-api.azurewebsites.net/api/login") {
            setBody(loginRequest) }.body()

        token = response.token
        "Login successful " +response.toString()
    }catch (e: Exception){
            Log.e("API", "Login error: ${e.message}", e)
            "Login failed: ${e.message}"
    }
    }
    // Then make the rental request with the token
    suspend fun rentEquipment( startDate: String, returnDate: String): String {
        return try {
            // LoginClient should have the token stored after successful login
            val rentalRequest = RentalRequest(startDate, returnDate)

            val response = httpClient.post("https://equipments-api.azurewebsites.net/api/equipments/$token/rent") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(rentalRequest)
            }

            "Equipment rented successfully"
        } catch (e: Exception) {
            Log.e("API", "Rental error: ${e.message}", e)
            "Rental failed: ${e.message}"
        }
    }
    @Serializable
    data class RentalRequest(
        val startDate: String,
        val returnDate: String
    )

}

//    suspend fun getFeeds(): List<Equipment> {
//        return try {
//            val response: EquipmentResponse = httpClient.get("https://equipments-api.azurewebsites.net/api/equipments/").body()
//            response.equipments} catch (e: Exception) {
//            Log.e("API", "Error fetching equipment data: ${e.message}", e)
//            emptyList()
//        }
//    }