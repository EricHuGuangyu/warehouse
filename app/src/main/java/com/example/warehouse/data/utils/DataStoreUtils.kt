package com.example.warehouse.data.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import com.example.warehouse.data.remote.NetworkConfig

// Initialize DataStore with preferencesDataStore delegate
val Context.dataStore by preferencesDataStore(name = "user_prefs")

object DataStoreUtils {

    private val USER_ID_KEY = stringPreferencesKey(NetworkConfig.PREF_USER_ID)

    // Function to retrieve the UserId from DataStore
    suspend fun getUserId(context: Context): String? {
        // Access the DataStore instance from context
        val preferences = context.dataStore.data.first()
        return preferences[USER_ID_KEY]
    }

    // Function to save the UserId to DataStore
    suspend fun putUserId(context: Context, userId: String?) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId ?: ""
        }
    }
}
