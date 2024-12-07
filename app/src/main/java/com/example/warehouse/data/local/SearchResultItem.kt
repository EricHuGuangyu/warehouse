package com.example.warehouse.data.local

data class SearchResultItem(
    val description: String?,
    val products: List<ProductWithoutPrice>?
)
