package com.example.pet_health

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.pet_health.ui.navigation.AppScreen
import com.example.pet_health.ui.theme.PetHealthTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetHealthTheme {
                AppScreen()   // <-- Chỉ chạy 1 NavHost duy nhất
            }
        }
    }
}