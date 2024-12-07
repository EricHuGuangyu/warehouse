package com.example.warehouse.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehouse.data.local.SearchResultItem
import com.example.warehouse.data.utils.DataStoreUtils
import com.example.warehouse.data.remote.ApiService
import com.example.warehouse.data.remote.NetworkConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {
    private val _searchResults = MutableStateFlow<List<SearchResultItem>>(emptyList())
    val searchResults: StateFlow<List<SearchResultItem>> = _searchResults

    private var currentStartIndex = 0
    private val itemsPerPage = 20

    fun loadSearchResults(keyword: String) {
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
                _searchResults.value += response.results ?: emptyList()


            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
