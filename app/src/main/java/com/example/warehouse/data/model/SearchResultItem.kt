package com.example.warehouse.data.model

data class SearchResultItem(
    val description: String?,
    val products: List<ProductWithoutPrice>?
)
