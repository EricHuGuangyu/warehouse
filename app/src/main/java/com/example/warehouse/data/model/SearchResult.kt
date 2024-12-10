package com.example.warehouse.data.model

data class SearchResult(

	val products: List<Product>,
	val searchTerm: String,
	val suggestions: Suggestions?,
	val total: Int,
	val facets: List<Facets>?,
	val sortOptions: List<SortOptions>,
	val guest: Boolean,
	val platformDemandWare: String,
	val environment: String,
	val developmentPlatform: Boolean,
	val apiVersion: Double,
	val requestedApiVersion: Double
)

//data class SearchResult(
//	val hitCount: String?,
//	val results: List<SearchResultItem>?,
//	val searchID: String?,
//	val prodQAT: String?,
//	val found: String?
//)