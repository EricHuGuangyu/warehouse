package com.example.warehouse.ui.screen

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.warehouse.R

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Permission required
        val cameraPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                navController.navigate("scanner")
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        IconButton(
            onClick = {
                // check the camera permission
                if (ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    navController.navigate("scanner")
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            context as Activity,
                            android.Manifest.permission.CAMERA
                        )
                    ) {
                        Toast.makeText(
                            context,
                            "Camera permission is needed for scanning.",
                            Toast.LENGTH_SHORT
                        ).show()
                        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    } else {
                        Toast.makeText(
                            context,
                            "Please enable camera permission in settings",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_scan_selected),
                contentDescription = "Barcode",
                modifier = Modifier.size(28.dp)
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Static Image",
            modifier = Modifier.size(width = 158.dp, height = 48.dp)
        )
        Text(
            text = "Help",
            color = Color(0xFFEB002B),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 5.dp, bottom = 3.dp)
        )
    }
    Spacer(modifier = Modifier.height(32.dp))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp, top = 68.dp)
    ) {

        SearchBarScreen(navController)

        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_products),
            contentDescription = "Static Image",
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
        )

    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}