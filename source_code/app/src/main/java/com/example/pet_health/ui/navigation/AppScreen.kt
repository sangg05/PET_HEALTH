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
        composable("add_pet") { AddPetScreen(navController) }
        composable("health_records") { HealthRecordScreen(navController) }
        composable("add_health_record") { AddHealthRecordScreen(navController) }

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
        composable(
            route = "add_pet_new?editMode={editMode}&initName={initName}&initType={initType}&initAge={initAge}&initColor={initColor}&initWeight={initWeight}&initHeight={initHeight}&initAdoptionDate={initAdoptionDate}&initImageUri={initImageUri}",
            arguments = listOf(
                navArgument("editMode") { type = NavType.BoolType; defaultValue = false },
                navArgument("initName") { type = NavType.StringType; defaultValue = "" },
                navArgument("initType") { type = NavType.StringType; defaultValue = "" },
                navArgument("initAge") { type = NavType.StringType; defaultValue = "" },
                navArgument("initColor") { type = NavType.StringType; defaultValue = "" },
                navArgument("initWeight") { type = NavType.StringType; defaultValue = "" },
                navArgument("initHeight") { type = NavType.StringType; defaultValue = "" },
                navArgument("initAdoptionDate") { type = NavType.StringType; defaultValue = "" },
                navArgument("initImageUri") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            AddPetScreen(
                navController = navController,
                editMode = backStackEntry.arguments?.getBoolean("editMode") ?: false,
                initName = backStackEntry.arguments?.getString("initName") ?: "",
                initType = backStackEntry.arguments?.getString("initType") ?: "",
                initAge = backStackEntry.arguments?.getString("initAge") ?: "",
                initColor = backStackEntry.arguments?.getString("initColor") ?: "",
                initWeight = backStackEntry.arguments?.getString("initWeight") ?: "",
                initHeight = backStackEntry.arguments?.getString("initHeight") ?: "",
                initAdoptionDate = backStackEntry.arguments?.getString("initAdoptionDate") ?: "",
                initImageUri = backStackEntry.arguments?.getString("initImageUri")
            )
        }
    }
}
