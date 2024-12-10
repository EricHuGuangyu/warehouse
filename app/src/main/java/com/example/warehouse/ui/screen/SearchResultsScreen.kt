package com.example.warehouse.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.warehouse.R
import com.example.warehouse.data.model.Product
import com.example.warehouse.viewmodel.SearchViewModel
import com.example.warehouse.viewmodel.common.NetworkResult
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun SearchResultsScreen(
    navController: NavHostController,
    keyword: String
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val uiState by viewModel.uiState.observeAsState()
    val hasLoaded = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(keyword) {
        println("LaunchedEffect hasLoaded:${hasLoaded.value}")
        if (keyword.isNotEmpty() && !hasLoaded.value) {
            viewModel.loadSearchResults(keyword, isLoadMore = false)
            hasLoaded.value = true
        }
    }

    when (uiState) {
        is NetworkResult.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Loading...")
            }
        }

        is NetworkResult.Success -> {
            val products = (uiState as NetworkResult.Success<List<Product>>).data
            //val products = searchResultItems.flatMap { it.products ?: emptyList() }
            val lazyGridState = rememberLazyGridState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            ) {
                SearchBarScreen(navController)

                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = lazyGridState,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(products.size) { index ->
                            val product = products[index]
                            Box(modifier = Modifier.fillMaxSize()) {
                                ProductCard(
                                    product = product,
                                    onClick = {
                                        navController.navigate(route = "productDetails/${product.productBarcode}/${keyword}") {
                                        }
                                    }
                                )
                            }
                        }
                    }
                    if (viewModel.isLoadingMore.value) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp)
                        )
                    }
                }
                // Scroll to load more
                InfiniteListHandler(listState = lazyGridState) {
                    viewModel.loadSearchResults(keyword, isLoadMore = true)
                }
            }
        }

        is NetworkResult.Error -> {
            val error = (uiState as NetworkResult.Error).message
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("No results found for \"$keyword\" \n Error: $error")
            }
        }

        else -> {}
    }
}

@Composable
fun InfiniteListHandler(
    listState: LazyGridState,
    buffer: Int = 4,
    onLoadMore: () -> Unit
) {
    val loadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            totalItemsNumber > 0 && lastVisibleItemIndex > 0 &&
                    lastVisibleItemIndex > (totalItemsNumber - buffer)
        }
    }

    LaunchedEffect(loadMore) {
        snapshotFlow { loadMore.value }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore) {
                    onLoadMore()
                }
            }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        // Card for product details
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(1.dp)
                .height(230.dp),
            shape = RoundedCornerShape(1.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = product.productImageUrl,
                        placeholder = painterResource(id = R.drawable.ic_logo),
                        error = painterResource(id = R.drawable.ic_logo)
                    ),
                    contentDescription = product.brandDescription,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp * 2 / 3)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = product.productName,
                    fontSize = 12.sp,
                    maxLines = 2,
                    lineHeight = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Price information
                Text(
                    text = "$" + product.priceInfo.price.toString(),
                    fontSize = 12.sp,
                    color = Color(0xFFEB002B),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchResultsScreenPreview() {
    val navController = rememberNavController()
    SearchResultsScreen(navController, "navController = navController")
}
