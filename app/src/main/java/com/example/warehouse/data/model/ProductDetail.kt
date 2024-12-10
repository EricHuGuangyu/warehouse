package com.example.warehouse.data.model

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
){
    companion object {
        fun empty(): ProductDetail {
            return ProductDetail(
                machineID = null,
                action = null,
                scanBarcode = null,
                scanID = null,
                userDescription = null,
                product = null,
                prodQAT = null,
                scanDateTime = null,
                found = null,
                userID = null,
                branch = null
            )
        }
    }
}
