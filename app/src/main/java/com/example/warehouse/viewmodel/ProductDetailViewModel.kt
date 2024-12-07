package com.example.warehouse.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse.data.local.ProductDetail
import com.example.warehouse.data.utils.DataStoreUtils
import com.example.warehouse.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {

    // State for loading, error, and product details
    private val _productDetail = mutableStateOf<ProductDetail?>(null)
    val productDetail: State<ProductDetail?> = _productDetail

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun getProductDetails(barCode: String) {
        _isLoading.value = true
        _error.value = null

        // Use launch to call suspend functions inside viewModelScope
        viewModelScope.launch {
            try {
                // Get UserID from DataStore
                val userId = DataStoreUtils.getUserId(context) // Make sure context is available here

                // Prepare parameters for the API call
                val paramMap = hashMapOf<String, String>(
                    "BarCode" to barCode,
//                    "MachineID" to NetworkConfig.MACHINE_ID,
//                    "UserID" to (userId ?: ""), // Use empty string if UserID is null
//                    "Branch" to NetworkConfig.BRANCH_ID
                )
                println("apiService getProductDetail")
                // Make the network call
                val response = apiService.getProductDetail(paramMap)
                _productDetail.value = response
                println("apiService response: $response")

            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

}
