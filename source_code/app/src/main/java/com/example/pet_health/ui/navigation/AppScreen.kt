package com.example.pet_health.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.compose.navigation
import com.example.pet_health.data.repository.PetRepository
import com.example.pet_health.data.repository.UserRepository
import com.example.pet_health.ui.screen.AccountActionsScreen
//import com.example.pet_health.ui.screen.AccountManagementScreen
import com.example.pet_health.ui.screen.AddRecordScreen
import com.example.pet_health.ui.screen.ChangePasswordScreen
import com.example.pet_health.ui.screen.ChooseAvatarScreen
import com.example.pet_health.ui.screen.ForgotPasswordScreen
import com.example.pet_health.ui.screen.LoginScreen
import com.example.pet_health.ui.screen.NotificationScreen
import com.example.pet_health.ui.screen.RegisterScreen
import com.example.pet_health.ui.screen.ReminderDetailScreen
import com.example.pet_health.ui.screen.ReminderFormScreen
import com.example.pet_health.ui.screen.ReminderScreen
import com.example.pet_health.ui.screen.ResetPasswordScreen
import com.example.pet_health.ui.screen.TiemThuocListScreen
import com.example.pet_health.ui.screen.UpdateInfoScreen
import com.example.pet_health.ui.screen.WeightHeightScreen
import com.example.pet_health.ui.screen.RecordDetailScreen
import com.example.pet_health.ui.screens.*
import com.example.pet_health.ui.viewmodel.PetViewModel
import com.example.pet_health.ui.viewmodel.PetViewModelFactory
import com.example.pet_health.ui.viewmodel.ReminderViewModel
import com.example.pet_health.ui.viewmodel.ReminderViewModelFactory
import kotlinx.coroutines.launch
import pet_health.data.local.AppDatabase

@Composable
fun AppScreen() {

    val navController = rememberNavController()
    val context = LocalContext.current
    val userRepository = remember { UserRepository(context) }
    val scope = rememberCoroutineScope()
    val database = AppDatabase.getDatabase(context)
    val repository = PetRepository(database)

    // ===== KHỞI TẠO REMINDER VIEWMODEL =====
    val reminderViewModel: ReminderViewModel = viewModel(
        factory = ReminderViewModelFactory(context.applicationContext as android.app.Application)
    )

    NavHost(
        navController = navController,
        startDestination = "auth"
    ) {

        navigation(startDestination = "login", route = "auth") {

            composable("login") {
                LoginScreen(
                    // --- SỬA LỖI: Thêm dòng này vào ---
                    navController = navController,
                    // ----------------------------------
                    userRepository = userRepository,
                    onLoginClick = { email, pass ->
                        scope.launch {
                            val (success, msg) = userRepository.loginUser(email, pass)
                            if (success) {
                                navController.navigate("home") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onNavigateRegister = { navController.navigate("register") },
                    onNavigateForgot = { navController.navigate("forgot") }
                )
            }
            composable("register") {
                RegisterScreen(
                    userRepository = userRepository,
                    onNavigateLogin = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onRegisterSuccess = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                )
            }
            composable("forgot") {
                ForgotPasswordScreen(
                    onSendClick = { email ->
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

            composable("home") {
                HomeScreen(
                    navController = navController,
                    userRepository = userRepository
                )
            }

            composable("pet_list") { backStackEntry ->
                val petViewModel: PetViewModel = viewModel(
                    backStackEntry,
                    factory = PetViewModelFactory(repository)
                )

                LaunchedEffect(Unit) {
                    petViewModel.fetchPetsFromFirebaseToRoom()
                }

                PetListScreen(
                    navController = navController,
                    petViewModel = petViewModel
                )
            }

            composable(
                route = "add_pet?editMode={editMode}&initName={initName}&initType={initType}&initAge={initAge}&initColor={initColor}&initWeight={initWeight}&initHeight={initHeight}&initAdoptionDate={initAdoptionDate}",
                arguments = listOf(
                    navArgument("editMode") { type = NavType.BoolType; defaultValue = false },
                    navArgument("initName") { type = NavType.StringType; defaultValue = "" },
                    navArgument("initType") { type = NavType.StringType; defaultValue = "" },
                    navArgument("initAge") { type = NavType.StringType; defaultValue = "" },
                    navArgument("initColor") { type = NavType.StringType; defaultValue = "" },
                    navArgument("initWeight") { type = NavType.StringType; defaultValue = "" },
                    navArgument("initHeight") { type = NavType.StringType; defaultValue = "" },
                    navArgument("initAdoptionDate") { type = NavType.StringType; defaultValue = "" },
                )
            ) { backStackEntry ->

                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("pet_list") }
                val petViewModel: PetViewModel = viewModel(
                    parentEntry,
                    factory = PetViewModelFactory(repository)
                )

                val editMode = backStackEntry.arguments?.getBoolean("editMode") ?: false
                val initName = backStackEntry.arguments?.getString("initName") ?: ""
                val initType = backStackEntry.arguments?.getString("initType") ?: ""
                val initAge = backStackEntry.arguments?.getString("initAge") ?: ""
                val initColor = backStackEntry.arguments?.getString("initColor") ?: ""
                val initWeight = backStackEntry.arguments?.getString("initWeight") ?: ""
                val initHeight = backStackEntry.arguments?.getString("initHeight") ?: ""
                val initAdoptionDate = backStackEntry.arguments?.getString("initAdoptionDate") ?: ""
                val initImageUri = petViewModel.tempImageUri?.toString()

                AddPetScreen(
                    navController = navController,
                    petViewModel = petViewModel,
                    editMode = editMode,
                    initName = initName,
                    initType = initType,
                    initAge = initAge,
                    initColor = initColor,
                    initWeight = initWeight,
                    initHeight = initHeight,
                    initAdoptionDate = initAdoptionDate,
                    initImageUri = initImageUri
                )
            }

            composable(
                route = "pet_profile?petId={petId}",
                arguments = listOf(navArgument("petId") { type = NavType.StringType })
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("pet_list") }
                val petViewModel: PetViewModel = viewModel(
                    parentEntry,
                    factory = PetViewModelFactory(repository)
                )

                val petId = backStackEntry.arguments?.getString("petId")
                val pet = petViewModel.pets.value.find { it.petId == petId }

                if (pet != null) {
                    PetProfileScreen(
                        pet = pet,
                        navController = navController,
                        repository = repository
                    )
                }
            }

            composable("health_records") { HealthRecordScreen(navController) }
            composable("add_health_record") { AddHealthRecordScreen(navController) }

            // ===== PHẦN NHẮC LỊCH =====
            composable("reminder") {
                ReminderScreen(navController = navController, viewModel = reminderViewModel)
            }

            composable(
                route = "reminder_form?reminderId={reminderId}",
                arguments = listOf(
                    navArgument("reminderId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val reminderId = backStackEntry.arguments?.getString("reminderId")
                ReminderFormScreen(
                    navController = navController,
                    viewModel = reminderViewModel,
                    reminderId = reminderId
                )
            }

            composable(
                route = "reminder_detail/{reminderId}",
                arguments = listOf(navArgument("reminderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val reminderId = backStackEntry.arguments?.getString("reminderId") ?: ""
                ReminderDetailScreen(
                    navController = navController,
                    reminderId = reminderId,
                    viewModel = reminderViewModel
                )
            }

            composable(
                "weight_height/{petId}",
                arguments = listOf(navArgument("petId") { type = NavType.StringType })
            ) { backStackEntry ->
                val petId = backStackEntry.arguments?.getString("petId")
                WeightHeightScreen(
                    navController = navController,
                    petId = petId
                )
            }

            composable("medical_records") { TiemThuocListScreen(navController) }

            composable("add_record") {
                AddRecordScreen(
                    navController = navController,
                    petId = ""
                )
            }

            composable(
                route = "record_detail/{vaccineId}",
                arguments = listOf(navArgument("vaccineId") { type = NavType.StringType })
            ) { backStackEntry ->
                val vaccineId = backStackEntry.arguments?.getString("vaccineId") ?: ""
                RecordDetailScreen(
                    navController = navController,
                    vaccineId = vaccineId
                )
            }

            composable("notification") { NotificationScreen(navController) }

            composable("account") {
                AccountManagementScreen(
                    navController = navController,
                    userRepository = userRepository,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("account_actions") {
                AccountActionsScreen(
                    onBack = { navController.popBackStack() },
                    onUpdateInfo = {
                        navController.navigate("update_info")
                    },
                    onUpdateAvatar = { navController.navigate("choose_avatar") },
                    onChangePassword = {
                        navController.navigate("change_password")
                    },
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo("account") { inclusive = true }
                        }
                    }
                )
            }

            composable("change_password") {
                ChangePasswordScreen(
                    onBack = { navController.popBackStack() },
                    onPasswordChanged = {
                        navController.popBackStack()
                    }
                )
            }

            composable("update_info") {
                UpdateInfoScreen(
                    onBack = { navController.popBackStack() },
                    onSave = {
                        navController.popBackStack()
                    }
                )
            }

            composable("choose_avatar") {
                ChooseAvatarScreen(
                    onBack = { navController.popBackStack() },
                    onSelect = { uri ->
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}