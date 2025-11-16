package com.example.pet_health

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pet_health.ui.screens.HealthRecordScreen
import com.example.pet_health.ui.screens.AddHealthRecordScreen
import com.example.pet_health.ui.screens.HomeScreen
import com.example.pet_health.ui.screens.PetListScreen
import com.example.pet_health.ui.screens.AddPetScreen
import com.example.pet_health.ui.theme.PetHealthTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetHealthApp()
        }
    }
}

@Composable
fun PetHealthApp() {
    PetHealthTheme {
        Surface {
            // Hệ thống điều hướng giữa các màn hình
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(navController) // Trang chủ
                }
                composable("health_record") {
                    HealthRecordScreen(navController) // Hồ sơ bệnh án
                }
                composable("add_health_record") {
                    AddHealthRecordScreen(navController) // Trang thêm bệnh án
                }
                composable("pet_list") {
                    PetListScreen(navController)
                }
                composable("add_pet") {
                    AddPetScreen(navController)
                }
            }
        }
    }
}
