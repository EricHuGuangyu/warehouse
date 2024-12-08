package com.example.warehouse.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse.data.local.SearchResultItem
import com.example.warehouse.data.remote.ApiService
import com.example.warehouse.data.remote.NetworkConfig
import com.example.warehouse.data.utils.DataStoreUtils
import com.example.warehouse.viewmodel.common.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableLiveData<NetworkResult<List<SearchResultItem>>>()
    val uiState: LiveData<NetworkResult<List<SearchResultItem>>> = _uiState

    private var currentStartIndex = 0
    private val itemsPerPage = 20

    fun loadSearchResults(keyword: String) {
        _uiState.value = NetworkResult.Loading
        viewModelScope.launch {
            try {
                val paramsMap = hashMapOf(
                    "Search" to keyword,
                    "MachineID" to NetworkConfig.MACHINE_ID,
                    "UserID" to DataStoreUtils.getUserId(context),
                    "Branch" to NetworkConfig.BRANCH_ID,
                    "Start" to currentStartIndex.toString(),
                    "Limit" to itemsPerPage.toString()
                )
                println("Search keyword: $keyword")
                val response = apiService.getSearchResult(paramsMap)

                currentStartIndex += itemsPerPage
                val currentResults = (_uiState.value as NetworkResult.Success).data

                _uiState.value = NetworkResult.Success(
                    data = currentResults + (response.results ?: emptyList())
                )

            } catch (e: Exception) {
                _uiState.value = NetworkResult.Error("${e.message}")
            }
        }
    }
}
