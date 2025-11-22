package com.example.pet_health.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pet_health.ui.screen.*

@Composable
fun AppScreen() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "reminder_screen"
    ) {

        composable("reminder_screen") {
            ReminderScreen(navController)
        }

        composable("reminder_form") {
            ReminderFormScreen(navController)
        }

        composable("add_record") {
            AddRecordScreen(navController)
        }

        composable("tiem_thuoc_list") {
            TiemThuocListScreen(navController)
        }

        composable("notification_screen") {
            NotificationScreen(navController)
        }

        // ⭐ ROUTE ĐÃ CHUẨN HÓA 7 THAM SỐ
        composable(
            route = "reminder_detail/{pet}/{type}/{date}/{time}/{repeat}/{early}/{note}"
        ) { backStackEntry ->
            ReminderDetailScreen(
                navController = navController,
                pet = backStackEntry.arguments?.getString("pet") ?: "",
                type = backStackEntry.arguments?.getString("type") ?: "",
                date = backStackEntry.arguments?.getString("date") ?: "",
                time = backStackEntry.arguments?.getString("time") ?: "",
                repeat = backStackEntry.arguments?.getString("repeat") ?: "",
                early = backStackEntry.arguments?.getString("early") ?: "",
                note = backStackEntry.arguments?.getString("note") ?: ""
            )
        }

        composable(
            route = "record_detail/{petName}/{recordType}/{recordName}/{date}/{note}"
        ) { backStackEntry ->
            RecordDetailScreen(
                navController = navController,
                petName = backStackEntry.arguments?.getString("petName") ?: "",
                recordType = backStackEntry.arguments?.getString("recordType") ?: "",
                recordName = backStackEntry.arguments?.getString("recordName") ?: "",
                date = backStackEntry.arguments?.getString("date") ?: "",
                note = backStackEntry.arguments?.getString("note") ?: ""
            )
        }
    }
}