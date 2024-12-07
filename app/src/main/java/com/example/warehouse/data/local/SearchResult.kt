package com.example.warehouse.data.local

data class SearchResult(
    val hitCount: String?,
    val results: List<SearchResultItem>?,
    val searchID: String?,
    val prodQAT: String?,
    val found: String?
)
