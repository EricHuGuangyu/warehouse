package com.example.warehouse.data.local

data class ProductDetail(
    val machineID: String?,
    val action: String?,
    val scanBarcode: String?,
    val scanID: String?,
    val userDescription: String?,
    val product: Product?,
    val prodQAT: String?,
    val scanDateTime: String?,
    val found: String?,
    val userID: String?,
    val branch: String?
)
