package com.example.warehouse

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.warehouse.ui.screen.BarcodeScannerScreen
import com.example.warehouse.ui.screen.HomeScreen
import com.example.warehouse.ui.screen.ProductDetailsScreen
import com.example.warehouse.ui.screen.SearchResultsScreen
import com.example.warehouse.ui.screen.SplashScreen
import com.example.warehouse.ui.theme.WarehouseTheme
import com.example.warehouse.viewmodel.MainViewModel
import com.example.warehouse.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WarehouseTheme {
                MainApp()
            }
        }

        mainViewModel.getUserId(this)
        mainViewModel.userId.observe(this) { userId ->
            if (userId != null) {
                // User ID retrieved from DataStore or fetched from network
                Toast.makeText(this, "User ID: $userId", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Get User failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onNavigateToMain = {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("home") { HomeScreen(navController) }
        composable("scanner") { BarcodeScannerScreen(navController) }
        composable("searchResults/{keyword}") { backStackEntry ->
            val keyword = backStackEntry.arguments?.getString("keyword").orEmpty()
            SearchResultsScreen(navController, keyword)
        }
        composable("productDetails/{barCode}/{keyWord}") { backStackEntry ->
            val barCode = backStackEntry.arguments?.getString("barCode").orEmpty()
            val keyWord = backStackEntry.arguments?.getString("keyWord").orEmpty()
            ProductDetailsScreen(barCode,keyWord)
        }
    }
}

fun NavController.navigate(
    route: String,
    args: Bundle,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    val nodeId = graph.findNode(route = route)?.id
    if (nodeId != null) {
        navigate(nodeId, args, navOptions, navigatorExtras)
    }
}
