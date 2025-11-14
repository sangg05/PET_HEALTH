package com.example.pet_health

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.*
import com.example.pet_health.ui.screen.HealthTrackingScreen
import com.example.pet_health.ui.screen.WeightHeightScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomeScreen(navController) }
                composable("health_tracking") { HealthTrackingScreen() }
                composable("weight_height") { WeightHeightScreen() }

            }
        }
    }
}

@Composable
fun HomeScreen(navController: androidx.navigation.NavHostController) {
    // Dùng Column để canh dọc
    Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center, // canh giữa theo chiều dọc
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.Button(onClick = {
            navController.navigate("health_tracking")
        }) {
            androidx.compose.material3.Text("Đi tới Health Dashboard")
        }
        androidx.compose.material3.Button(onClick = {
            navController.navigate("weight_height")
        }) {
            androidx.compose.material3.Text("Đi tới weight_height")
        }
    }
}
