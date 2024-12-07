package com.example.warehouse.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.warehouse.R
import com.example.warehouse.data.local.ProductWithoutPrice
import com.example.warehouse.viewmodel.SearchViewModel

@Composable
fun SearchResultsScreen(
    navController: NavHostController,
    keyword: String
) {
    //val viewModel: SearchViewModel = hiltViewModel()
    //val searchResults by viewModel.searchResults.collectAsState()
    val products = mockProductData()// searchResults.flatMap { it.products ?: emptyList() }

//    LaunchedEffect(keyword) {
//        if (keyword.isNotEmpty()) {
//            viewModel.loadSearchResults(keyword)
//        }
//    }

    if(false) {//if (searchResults.isEmpty() && keyword.isNotEmpty()) {
        // No results message
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("No results found for \"$keyword\"")
        }
    } else {
        val lazyGridState = rememberLazyGridState()
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = lazyGridState,
            modifier = Modifier.fillMaxSize().padding(8.dp).background(Color.White)
                .padding(top = 16.dp)
        ) {
            items(products.size) { index ->
                val product = products[index]
                ProductCard(
                    product = product,
                    onClick = { navController.navigate("productDetails/${product.barcode}") }
                )
            }
        }
        // Scroll to load more
        LaunchedEffect(lazyGridState) {
            snapshotFlow { lazyGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .collect { lastVisibleIndex ->
                    if (lastVisibleIndex == products.size - 1) {
                       // viewModel.loadSearchResults(keyword)
                    }
                }
        }
    }
}

@Composable
fun ProductCard(product: ProductWithoutPrice, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(2.dp)
            .clickable { onClick() }
            .fillMaxWidth()
            .background(Color.White)

    ) {
        Column(
            modifier = Modifier.padding(8.dp).fillMaxSize().background(Color.White),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Image(
//                painter = rememberAsyncImagePainter(
//                    model = product.imageURL,
//                    placeholder = painterResource(id = R.drawable.ic_scan_selected),
//                    error = painterResource(id = R.drawable.ic_scan_selected)
//                ),
//                contentDescription = product.description,
//                modifier = Modifier.size(128.dp)
//            )
            Image(
                painter = painterResource(id = R.drawable.ic_product1),
                contentDescription = "Product Image"
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = product.description ?: "No Description",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$23.99",
                fontSize = 12.sp, color = Color(0xFFEB002B),
                fontWeight = FontWeight.Bold
            )

        }
    }
}

fun mockProductData(): List<ProductWithoutPrice> {
    return List(50) { index ->
        ProductWithoutPrice(
            description = "P $index Description",
            imageURL = "https://via.placeholder.com/150?text=Product+$index"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchResultsScreenPreview() {
    val navController = rememberNavController()
    SearchResultsScreen(navController,"navController = navController")
}