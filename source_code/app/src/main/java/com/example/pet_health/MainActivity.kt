package com.example.pet_health

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pet_health.ui.screen.HealthTrackingScreen
import com.example.pet_health.repository.PetRepository
import com.example.pet_health.data.local.dao.PetDao
import com.example.pet_health.ui.screen.WeightHeightScreen
import com.example.pet_health.ui.viewmodel. PetViewModel
import pet_health.data.local.AppDatabase


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1️⃣ Tạo database và DAO thật
        val db =  AppDatabase.getDatabase(applicationContext) // giả sử bạn đã có PetDatabase singleton
        val dao: PetDao = db.petDao()
        val repository = PetRepository(dao)

        // 2️⃣ Khởi tạo ViewModel với DAO thật
        val petViewModel = PetViewModel(repository)
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(navController)
                }
                composable("health_tracking") {
                    HealthTrackingScreen(viewModel = petViewModel)
                }
                composable("weight_height") {
                    WeightHeightScreen()
                }
            }
        }
    }
}



@Composable
fun HomeScreen(navController: androidx.navigation.NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate("health_tracking") }) {
            Text("Đi tới Health Tracking")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("weight_height") }) {
            Text("Đi tới Weight & Height")
        }
    }
}
