package com.example.warehouse.data.remote

import com.example.warehouse.data.model.Product
import com.example.warehouse.data.model.ProductDetail
import com.example.warehouse.data.model.SearchResult
import com.example.warehouse.data.model.User
import retrofit2.http.GET
import retrofit2.http.QueryMap


interface ApiService {
    @GET("bolt/newuser.json")
    suspend fun getNewUserId(): User

    @GET("twlBoltProd/price.json")
    suspend fun getProductDetail(@QueryMap paramMap: HashMap<String, String>): Product

//    @GET("bolt/product.json")
//    suspend fun getProductDetail(@QueryMap paramMap: HashMap<String, String>): ProductDetail

    @GET("twlYourWarehouseProd/search.json")
    suspend fun getSearchResult(@QueryMap paramMap: HashMap<String, String?>): SearchResult

}