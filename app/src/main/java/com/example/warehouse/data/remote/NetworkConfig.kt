package com.example.warehouse.data.remote

import com.example.warehouse.BuildConfig

object NetworkConfig {
    const val BASE_URL = "https://twg.azure-api.net/"
    const val BRANCH_ID = "208";
    const val PREF_USER_ID = "userId";
    const val SUBSCRIPTION_KEY = BuildConfig.API_KEY
    const val MACHINE_ID: String = "1234567890"
}