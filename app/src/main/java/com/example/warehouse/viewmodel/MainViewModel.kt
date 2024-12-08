package com.example.warehouse.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse.data.repository.UserRepository
import com.example.warehouse.data.utils.DataStoreUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> get() = _userId

    fun getUserId(context: Context) {
        if (_userId.value != null) return

        viewModelScope.launch {
            val userId = DataStoreUtils.getUserId(context)
            if (userId == null) {
                Log.d("UserId", "User ID is null, fetching new one.")
                val user = userRepository.fetchNewUserId()
                user?.userID?.let {
                    DataStoreUtils.putUserId(context, it)
                    _userId.postValue(it)
                } ?: run {
                    _userId.postValue(null)
                }
            } else {
                Log.d("UserId", "User ID retrieved from DataStore: $userId")
                _userId.postValue(userId)
            }
        }
    }
}