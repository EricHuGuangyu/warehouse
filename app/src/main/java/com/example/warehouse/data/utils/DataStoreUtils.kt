package com.example.warehouse.data.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import com.example.warehouse.data.remote.NetworkConfig

val Context.dataStore by preferencesDataStore(name = "user_prefs")

object DataStoreUtils {
    private val USER_ID_KEY = stringPreferencesKey(NetworkConfig.PREF_USER_ID)

    suspend fun getUserId(context: Context): String? {
        val preferences = context.dataStore.data.first()
        return preferences[USER_ID_KEY]
    }

    suspend fun putUserId(context: Context, userId: String?) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId ?: ""
        }
    }
}
