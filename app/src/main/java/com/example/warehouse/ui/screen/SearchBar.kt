package com.example.warehouse.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarScreen(navController: NavHostController) {
    var searchText by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(false) }
    val searchHistory = remember { mutableStateListOf("Logo", "Soft Toy", "OutDoor") }
    SearchBar(
        query = searchText,
        onQueryChange = { searchText = it },
        onSearch = {
            println("searchText: $searchText")
            if (searchText.isNotEmpty()) {
                if (!searchHistory.contains(searchText)) {
                    searchHistory.add(0, searchText)
                    println("searchHistory $searchHistory")
                }

                navController.navigate("searchResults/$searchText")
            }
        },
        active = isActive,
        onActiveChange = { isActive = it },
        placeholder = {
            Text(
                "Search"
            )
        },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = { searchText = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = SearchBarDefaults.colors(
            containerColor = Color.White
        )
    ) {
        // Dropdown results or suggestions
        if (searchHistory.isNotEmpty() && searchText.isEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Search History",
                    fontWeight = FontWeight.Bold
                )
                println("searchHistory forEach $searchHistory")
                searchHistory.forEach { historyItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                searchText = historyItem
                                isActive = false
                            }
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(historyItem)
                        IconButton(onClick = { searchHistory.remove(historyItem) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }
}