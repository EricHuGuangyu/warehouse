package com.example.warehouse.data.repository

import com.example.warehouse.data.model.User
import com.example.warehouse.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton


interface UserRepository {
    suspend fun fetchNewUserId(): User?
//    {
//        return try {
//            println("fetchNewUserId")
//            //apiService.getNewUserId()
//            val response =
//            println("UserID: ${response.userID}")
//            response.takeIf { it.userID != null }
//        } catch (e: Exception) {
//            null
//        }
//    }
}
