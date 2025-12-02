//package com.example.pet_health.ui.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navArgument
//import com.example.pet_health.ui.screens.*
//
//@Composable
//fun AppScreen() {
//
//    val navController = rememberNavController()
//
//    NavHost(
//        navController = navController,
//        startDestination = "home"
//    ) {
//
//        composable("home") { HomeScreen(navController) }
//        composable("pet_list") { PetListScreen(navController) }
//        composable("add_pet") { AddPetScreen(navController) }
//        composable("health_records") { HealthRecordScreen(navController) }
//        composable("add_health_record") { AddHealthRecordScreen(navController) }
//
//        composable(
//            route = "pet_profile?name={name}&breed={breed}&age={age}&imageRes={imageRes}",
//            arguments = listOf(
//                navArgument("name") { type = NavType.StringType; defaultValue = "" },
//                navArgument("breed") { type = NavType.StringType; defaultValue = "" },
//                navArgument("age") { type = NavType.IntType; defaultValue = 0 },
//                navArgument("imageRes") { type = NavType.IntType; defaultValue = 0 }
//            )
//        ) { backStackEntry ->
//
//            PetProfileScreen(
//                name = backStackEntry.arguments?.getString("name") ?: "",
//                breed = backStackEntry.arguments?.getString("breed") ?: "",
//                age = backStackEntry.arguments?.getInt("age") ?: 0,
//                imageRes = backStackEntry.arguments?.getInt("imageRes") ?: 0,
//                navController = navController
//            )
//        }
//    }
//}
package com.example.pet_health.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pet_health.ui.screen.ForgotPasswordScreen
import com.example.pet_health.ui.screen.LoginScreen
import com.example.pet_health.ui.screen.RegisterScreen
import com.example.pet_health.ui.screen.ResetPasswordScreen
import com.example.pet_health.ui.screens.*

@Composable
fun AppScreen() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // =============== AUTH SCREENS ===============

        composable("login") {
            LoginScreen(
                onLoginClick = { email, pass ->
                    // TODO: call API login
                    // Nếu login ok:
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateRegister = { navController.navigate("register") },
                onNavigateForgot = { navController.navigate("forgot") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack() // quay lại login
                },
                onNavigateLogin = { navController.popBackStack() }
            )
        }

        composable("forgot") {
            ForgotPasswordScreen(
                onSendClick = { email ->
                    // TODO: gửi OTP
                    navController.navigate("reset_password/$email")
                },
                onNavigateLogin = { navController.popBackStack() }
            )
        }

        composable(
            route = "reset_password/{email}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStack ->

            val email = backStack.arguments?.getString("email") ?: ""

            ResetPasswordScreen(
                email = email,
                onResetSuccess = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // =============== MAIN APP SCREENS ===============

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
        composable("account") {
            AccountManagementScreen(
                onBack = { navController.popBackStack() },
                onNavigateMore = { navController.navigate("account_actions") }
            )
        }
        composable("account_actions") {
            AccountActionsScreen(
                onBack = { navController.popBackStack() },
                onUpdateInfo = { /* TODO: điều hướng sửa info */ },
                onUpdateAvatar = { /* TODO */ },
                onChangePassword = { /* TODO */ },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("account") { inclusive = true }  // xoá stack để tránh quay lại
                    }
                }
            )
        }
    }
}