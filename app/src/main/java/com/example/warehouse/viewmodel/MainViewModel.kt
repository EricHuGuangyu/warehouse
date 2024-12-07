package com.example.warehouse.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse.data.utils.DataStoreUtils
import com.example.warehouse.data.repository.UserRepository
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
        viewModelScope.launch {
            val userId = DataStoreUtils.getUserId(context)
            if (userId == null) {
                val user = userRepository.fetchNewUserId()
                user?.userID?.let {
                    DataStoreUtils.putUserId(context, it)
                    _userId.postValue(it)
                }
            } else {
                _userId.postValue(userId)
            }
        }
    }
}
