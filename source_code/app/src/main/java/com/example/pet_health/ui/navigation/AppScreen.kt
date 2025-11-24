package com.example.pet_health.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pet_health.ui.screens.*


@Composable
fun AppScreen() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") { HomeScreen(navController) }
        composable("pet_list") { PetListScreen(navController) }
//        composable("add_pet") { AddPetScreen(navController) }
//        composable("health_records") { HealthRecordScreen(navController) }
//        composable("add_health_record") { AddHealthRecordScreen(navController) }

        composable(
            route = "pet_profile?name={name}&breed={breed}&age={age}&imageRes={imageRes}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType; defaultValue = "" },
                navArgument("breed") { type = NavType.StringType; defaultValue = "" },
                navArgument("age") { type = NavType.IntType; defaultValue = 0 },
                navArgument("imageRes") { type = NavType.IntType; defaultValue = 0 }
            )
        ) { backStackEntry ->

            PetProfileScreen(
                name = backStackEntry.arguments?.getString("name") ?: "",
                breed = backStackEntry.arguments?.getString("breed") ?: "",
                age = backStackEntry.arguments?.getInt("age") ?: 0,
                imageRes = backStackEntry.arguments?.getInt("imageRes") ?: 0,
                navController = navController
            )
        }
    }
}
