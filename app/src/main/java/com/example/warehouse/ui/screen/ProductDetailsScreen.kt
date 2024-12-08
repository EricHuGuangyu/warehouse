package com.example.warehouse.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.warehouse.data.local.ProductDetail
import com.example.warehouse.viewmodel.ProductDetailViewModel
import com.example.warehouse.viewmodel.common.NetworkResult

@Composable
fun ProductDetailsScreen(barCode: String) {

    val viewModel: ProductDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.observeAsState()

    LaunchedEffect(barCode) {
        viewModel.getProductDetails(barCode)
    }

    when (uiState) {
        is NetworkResult.Loading -> Text("Loading...")
        is NetworkResult.Success -> {
            val productDetail = (uiState as NetworkResult.Success<ProductDetail>).data
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val imageUrl = productDetail.product?.imageURL
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Product Image",
                    modifier = Modifier.size(428.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                productDetail.product?.description?.let {
                    Text(
                        it, fontSize = 18.sp, fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                productDetail.product?.branchPrice?.let {
                    Text(
                        it, fontSize = 20.sp, color = Color(0xFFEB002B), fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NumberInputField(
                        initialValue = 1,
                        onValueChange = { newValue ->
                            println("New quantity: $newValue")
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEB002B)),
                        shape = RoundedCornerShape(8.dp),
                        onClick = { /* Add to cart logic */ }) {
                        Text("ADD TO CART")
                    }
                }

            }
        }
        is NetworkResult.Error -> {
            val error = (uiState as NetworkResult.Error).message
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("No results found for \"$barCode\" \n Error: $error")
            }
        }
        else -> {}
    }
}

@Composable
fun NumberInputField(
    modifier: Modifier = Modifier,
    initialValue: Int = 1,
    minValue: Int = 1,
    maxValue: Int = Int.MAX_VALUE,
    onValueChange: (Int) -> Unit = {}
) {
    var quantity by remember { mutableIntStateOf(initialValue) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Minus Button
        IconButton(onClick = {
            if (quantity > minValue) {
                quantity--
                onValueChange(quantity)
            }
        }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Decrease"
            )
        }

        // Editable TextField for numeric input
        OutlinedTextField(
            value = quantity.toString(),
            onValueChange = {
                val newValue = it.toIntOrNull()
                if (newValue != null && newValue in minValue..maxValue) {
                    quantity = newValue
                    onValueChange(quantity)
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(55.dp),
            singleLine = true
        )

        // Plus Button
        IconButton(onClick = {
            if (quantity < maxValue) {
                quantity++
                onValueChange(quantity)
            }
        }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailsScreenPreview() {
    ProductDetailsScreen("navController = navController")
}