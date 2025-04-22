package com.example.equipment_management_system


import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlin.math.ceil

//object KtorClient {
//    val httpClient = HttpClient {
//        install(ContentNegotiation) {
//            json() // enable the client to perform JSON serialization
//        }
//        defaultRequest {
//            contentType(ContentType.Application.Json)
//            accept(ContentType.Application.Json)
//        }
//    }
//    //Get Equipment List
//    suspend fun getFeeds(page: Int): EquipmentResponse {
//        return try {
//            // Add page parameter to the URL
//            httpClient.get("https://equipments-api.azurewebsites.net/api/equipments/?page=$page").body()
//        } catch (e: Exception) {
//            Log.e("API", "Error fetching equipment data: ${e.message}", e)
//            EquipmentResponse(emptyList(), 0, page, 10)
//        }
//    }
//    //Search Equipment
//    suspend fun search_equip(keyword:String,page: Int): EquipmentResponse {
//        return try {
//            // Add page parameter to the URL
//            httpClient.get("https://equipments-api.azurewebsites.net/api/equipments/?keyword=$keyword&?page=$page").body()
//        } catch (e: Exception) {
//            Log.e("API", "Error fetching equipment data: ${e.message}", e)
//            EquipmentResponse(emptyList(), 0, page, 10)
//        }
//    }
//
//    suspend fun getOneEquipment(id: String): Equipment? {
//            return try {
//                 httpClient.get("https://equipments-api.azurewebsites.net/api/equipments/${id}")
//                    .body<Equipment>()
//
//
//            } catch (e: Exception) {
//                Log.e("API", "Error fetching equipment data: ${e.message}", e)
//                null
//            }
//
//        }
//    fun getAllLocations(): Flow<List<String>> = flow {
//        val locations = mutableSetOf<String>()
//        var currentPage = 1
//        var hasMorePages = true
//
//        while (hasMorePages) {
//            try {
//                val response = httpClient.get("https://equipments-api.azurewebsites.net/api/equipments/?page=$currentPage").body<EquipmentResponse>()
//
//                // Add all locations from current page
//                var hadNewLocations = false
//                response.equipments.forEach { equipment ->
//                    if (locations.add(equipment.location)) {
//                        hadNewLocations = true
//                    }
//                }
//
//                // If we added new locations, emit the current list
//                if (hadNewLocations) {
//                    emit(locations.toList().sorted())
//                }
//
//                // Check if we've reached the last page
//                val totalPages = ceil(response.total.toFloat() / response.perPage).toInt()
//                hasMorePages = currentPage < totalPages
//                currentPage++
//
//                // Optional: Add a delay to avoid overloading the API
//                delay(200)
//            } catch (e: Exception) {
//                Log.e("API", "Error fetching locations: ${e.message}", e)
//                break
//            }
//        }
//
//        // Emit one final time to ensure we've emitted the complete list
//        emit(locations.toList().sorted())
//    }
//    // Function to get equipment by location
//    suspend fun getEquipmentByLocation(location: String, page: Int): EquipmentResponse {
//        return try {
//            httpClient.get("https://equipments-api.azurewebsites.net/api/equipments/?location=$location&page=$page").body()
//        } catch (e: Exception) {
//            Log.e("API", "Error fetching equipment by location: ${e.message}", e)
//            EquipmentResponse(emptyList(), 0, page, 10)
//        }
//    }
//}
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String
)
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val contact: String,
    val department: String,
    val remark: String,
    val isAdmin: Boolean= false
)
@Serializable
data class RegisterResponse(
    val id: String
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
    //Get Equipment List
    suspend fun getFeeds(page: Int): EquipmentResponse {
        return try {
            // Add page parameter to the URL
            httpClient.get("https://equipments-api.azurewebsites.net/api/equipments/?page=$page").body()
        } catch (e: Exception) {
            Log.e("API", "Error fetching equipment data: ${e.message}", e)
            EquipmentResponse(emptyList(), 0, page, 10)
        }
    }
    //Search Equipment
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
    fun getAllLocations(): Flow<List<String>> = flow {
        val locations = mutableSetOf<String>()
        var currentPage = 1
        var hasMorePages = true

        while (hasMorePages) {
            try {
                val response = httpClient.get("https://equipments-api.azurewebsites.net/api/equipments/?page=$currentPage").body<EquipmentResponse>()

                // Add all locations from current page
                var hadNewLocations = false
                response.equipments.forEach { equipment ->
                    if (locations.add(equipment.location)) {
                        hadNewLocations = true
                    }
                }

                // If we added new locations, emit the current list
                if (hadNewLocations) {
                    emit(locations.toList().sorted())
                }

                // Check if we've reached the last page
                val totalPages = ceil(response.total.toFloat() / response.perPage).toInt()
                hasMorePages = currentPage < totalPages
                currentPage++

                // Optional: Add a delay to avoid overloading the API
                delay(200)
            } catch (e: Exception) {
                Log.e("API", "Error fetching locations: ${e.message}", e)
                break
            }
        }

        // Emit one final time to ensure we've emitted the complete list
        emit(locations.toList().sorted())
    }

    fun getAllRentedByUser(): Flow<List<Equipment>> = flow {
        val allRentedEquipment = mutableListOf<Equipment>()
        var currentPage = 1
        var hasMorePages = true

        try {
            while (hasMorePages) {
                // Make API request with auth token
                val response = httpClient.get("https://equipments-api.azurewebsites.net/api/equipments/?page=$currentPage") {
                }.body<EquipmentResponse>()

                // Filter only rented equipment
                response.equipments.forEach { equipment ->
                    if (equipment.rented == true) {
                        allRentedEquipment.add(equipment)
                    }
                }
                emit(allRentedEquipment)
                // Check if there are more pages
                val totalPages = ceil(response.total.toFloat() / response.perPage).toInt()
                hasMorePages = currentPage < totalPages
                currentPage++

                delay(200) // Small delay to avoid overloading the API
            }

            // Emit the complete list after collecting all items
            emit(allRentedEquipment)
        } catch (e: Exception) {
            Log.e("API", "Error fetching rented equipment: ${e.message}", e)
            emit(emptyList()) // Emit empty list on error
        }
    }
    // Function to get equipment by location
    suspend fun getEquipmentByLocation(location: String, page: Int): EquipmentResponse {
        return try {
            httpClient.get("https://equipments-api.azurewebsites.net/api/equipments/?location=$location&page=$page").body()
        } catch (e: Exception) {
            Log.e("API", "Error fetching equipment by location: ${e.message}", e)
            EquipmentResponse(emptyList(), 0, page, 10)
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
    suspend fun logout(): String {
        return try{
            this.httpClient.post("https://equipments-api.azurewebsites.net/api/logout")
            token= ""
            "Logout successful "
        }catch (e: Exception){
            Log.e("API", "Logout error: ${e.message}", e)
            "Logout failed: ${e.message}"
        }
    }
    //Create a user account
    suspend fun register(email: String, password: String,contact: String,department: String,remark: String): String {
        return try{val registerRequest = RegisterRequest(email, password,contact,department,remark)

            val response: RegisterResponse = this.httpClient.post("https://equipments-api.azurewebsites.net/api/users") {
                setBody(registerRequest) }.body()


            "Register successful $response"
        }catch (e: Exception){
            Log.e("API", "Register error: ${e.message}", e)
            "Register failed: ${e.message}"
        }
    }
    // Then make the rental request with the equipment id
    suspend fun rentEquipment( startDate: String, returnDate: String, equipment_ID: String): String {
        return try {
            // LoginClient should have the token stored after successful login
            val rentalRequest = RentalRequest(startDate, returnDate)

            val response = httpClient.post("https://equipments-api.azurewebsites.net/api/equipments/$equipment_ID/rent") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(rentalRequest)
            }
            val responseBody = response.bodyAsText()
            Log.i("API", "Reserve Equipment response: Status=${response.status}, Body=$responseBody")

            if (response.status.isSuccess()) {
                "Equipment rented successfully"
            } else {
                "Rental failed: ${response.status}"
            }
        } catch (e: Exception) {
            Log.e("API", "Rental error: ${e.message}", e)
            "Rental failed: ${e.message}"
        }
    }
    suspend fun unreserve_Equipment(id: String):String{
        return try {
            // LoginClient should have the token stored after successful login

            val response = httpClient.delete("https://equipments-api.azurewebsites.net/api/equipments/$id/rent") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
            }
            "Equipment unreserved successfully"
        } catch (e: Exception) {
            Log.e("API", "Unreserved error: ${e.message}", e)
            "Unreserved failed: ${e.message}"
        }
    }
}
    @Serializable
    data class RentalRequest(
        val startDate: String,
        val returnDate: String
    )



//    suspend fun getFeeds(): List<Equipment> {
//        return try {
//            val response: EquipmentResponse = httpClient.get("https://equipments-api.azurewebsites.net/api/equipments/").body()
//            response.equipments} catch (e: Exception) {
//            Log.e("API", "Error fetching equipment data: ${e.message}", e)
//            emptyList()
//        }
//    }