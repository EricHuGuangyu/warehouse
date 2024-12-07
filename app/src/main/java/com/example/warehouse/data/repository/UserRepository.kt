package com.example.warehouse.data.repository

import com.example.warehouse.data.local.User
import com.example.warehouse.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun fetchNewUserId(): User? {
        return try {
            println("fetchNewUserId")
            val response = apiService.getNewUserId()
            println("UserID: ${response.userID}")
            response.takeIf { it.userID != null }
        } catch (e: Exception) {
            null
        }
    }
}
