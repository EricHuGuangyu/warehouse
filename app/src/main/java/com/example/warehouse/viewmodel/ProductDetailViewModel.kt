package com.example.warehouse.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse.data.local.ProductDetail
import com.example.warehouse.data.remote.ApiService
import com.example.warehouse.data.utils.DataStoreUtils
import com.example.warehouse.viewmodel.common.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableLiveData<NetworkResult<ProductDetail>>()
    val uiState: LiveData<NetworkResult<ProductDetail>> = _uiState

    fun getProductDetails(barCode: String) {
        _uiState.value = NetworkResult.Loading

        viewModelScope.launch {
            try {
                // Get UserID from DataStore
                val userId = DataStoreUtils.getUserId(context)

                // Prepare parameters for the API call
                val paramMap = hashMapOf<String, String>(
                    "BarCode" to barCode,
                    // "MachineID" to NetworkConfig.MACHINE_ID,
                    // "UserID" to (userId ?: ""), // Use empty string if UserID is null
                    // "Branch" to NetworkConfig.BRANCH_ID
                )

                println("apiService getProductDetail")
                val response = apiService.getProductDetail(paramMap)
                _uiState.value = NetworkResult.Success(response)
                println("apiService response: $response")

            } catch (e: Exception) {
                _uiState.value = NetworkResult.Error("${e.message}")
            }
        }
    }


}
