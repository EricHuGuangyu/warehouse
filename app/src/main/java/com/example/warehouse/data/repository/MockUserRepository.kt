package com.example.warehouse.data.repository

import com.example.warehouse.data.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockUserRepository  @Inject constructor() : UserRepository {
    override suspend fun fetchNewUserId(): User {
        //QAT for test
        return User("QAT","Guest")
    }
}