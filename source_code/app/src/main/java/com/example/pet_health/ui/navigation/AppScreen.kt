package com.example.pet_health.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
//import com.example.pet_health.repository.PetRepository
//import com.example.pet_health.ui.screen.HealthTrackingScreen
import com.example.pet_health.ui.screen.ReminderFormScreen
import com.example.pet_health.ui.screen.ReminderScreen
import com.example.pet_health.ui.screen.TiemThuocListScreen
import com.example.pet_health.ui.screen.WeightHeightScreen
import com.example.pet_health.ui.screen.AddRecordScreen
import com.example.pet_health.ui.screen.LoginScreen
import com.example.pet_health.ui.screens.*
//import com.example.pet_health.ui.viewmodel.PetViewModel
//import com.example.pet_health.ui.viewmodel.PetViewModelFactory
import androidx.navigation.compose.navigation
import com.example.pet_health.data.repository.PetRepository
import com.example.pet_health.data.repository.UserRepository
import com.example.pet_health.data.repository.UserViewModelFactory
import com.example.pet_health.repository.CloudinaryRepository
import com.example.pet_health.ui.screen.AccountActionsScreen
import com.example.pet_health.ui.screen.ChangePasswordScreen
import com.example.pet_health.ui.screen.EditPetScreen
import com.example.pet_health.ui.screen.ForgotPasswordScreen
import com.example.pet_health.ui.screen.HealthTrackingScreen
import com.example.pet_health.ui.screen.NotificationScreen
import com.example.pet_health.ui.screen.RegisterScreen
import com.example.pet_health.ui.screen.ResetPasswordScreen
import com.example.pet_health.ui.screen.UpdateInfoScreen
import com.example.pet_health.ui.viewmodel.HealthRecordViewModel
import com.example.pet_health.ui.viewmodel.HealthRecordViewModelFactory
import com.example.pet_health.ui.viewmodel.HealthTrackingViewModel
import com.example.pet_health.ui.viewmodel.HealthTrackingViewModelFactory
import com.example.pet_health.ui.viewmodel.PetViewModel
import com.example.pet_health.ui.viewmodel.PetViewModelFactory
import com.example.pet_health.ui.viewmodel.UserViewModel
import com.example.pet_health.ui.viewmodel.YourViewModel
import com.example.pet_health.ui.viewmodel.YourViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import pet_health.data.local.AppDatabase

@Composable
fun HealthRecordsNav(navController: NavController) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = PetRepository(database)
    val cloudinaryRepo = CloudinaryRepository()


    // Tạo 1 lần, share cho cả 2 màn hình
    val petViewModel: PetViewModel = viewModel(
        factory = PetViewModelFactory(
            petRepository = repository,
            cloudinaryRepository = cloudinaryRepo
        )
    )
    val healthRecordViewModel: HealthRecordViewModel = viewModel()

}
@Composable
fun AppScreen() {
    val cloudinaryRepository = CloudinaryRepository()
    val navController = rememberNavController()
    val context = LocalContext.current
    val userRepository = remember { UserRepository(context) }
    val scope = rememberCoroutineScope()
    val database = AppDatabase.getDatabase(context)
    val repository = PetRepository(database)
    val auth = FirebaseAuth.getInstance()
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(auth, userRepository)
    )

    val cloudinaryRepo = CloudinaryRepository()
    val petViewModel: PetViewModel = viewModel(
        factory = PetViewModelFactory(petRepository = repository, cloudinaryRepository = cloudinaryRepo)
    )
    val healthTrackingViewModel: HealthTrackingViewModel = viewModel(
        key = "HealthTrackingVM",
        factory = HealthTrackingViewModelFactory(repository, Firebase.auth.currentUser?.uid ?: "")
    )
    NavHost(
        navController = navController,
        startDestination = "auth"
    ) {

        navigation(startDestination = "login", route = "auth") {

            composable("login") {
                LoginScreen(
                    navController = navController,
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
                        // Điều hướng khi người dùng click "Đã có tài khoản? Đăng nhập"
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true } // xóa register khỏi back stack
                        }
                    },
                    onRegisterSuccess = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
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
//            composable("main") {
//                HomeScreen(navController)
//            }


            composable("home") {
                HomeScreen(
                    navController = navController,
                    userRepository = userRepository // truyền vào đây
                )
            }
            composable("account") {
                AccountManagementScreen(
                    navController = navController,
                    userViewModel = userViewModel,
                    onBack = { navController.popBackStack() },
                    userRepository = userRepository
                )
            }
            composable("pet_list") { backStackEntry ->
                val petVM: PetViewModel = viewModel(
                    backStackEntry,
                    factory = PetViewModelFactory(
                        petRepository = repository,
                        cloudinaryRepository = cloudinaryRepo
                    )
                )

                LaunchedEffect(Unit) {
                    petVM.fetchPetsFromFirebaseToRoom()
                }

                PetListScreen(navController, petVM)
            }

            composable("add_pet") {
                AddPetScreen(navController = navController, petViewModel = petViewModel)
            }
            composable(
                route = "edit_pet?editMode={editMode}&petId={petId}&initName={initName}&initType={initType}&initAge={initAge}&initColor={initColor}&initWeight={initWeight}&initHeight={initHeight}&initAdoptionDate={initAdoptionDate}&initImageUri={initImageUri}",
                arguments = listOf(
                    navArgument("editMode") { type = NavType.BoolType; defaultValue = true },
                    navArgument("petId") { type = NavType.StringType; defaultValue = "" },
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
                EditPetScreen(
                    navController = navController,
                    petViewModel = petViewModel,
                    editMode = backStackEntry.arguments?.getBoolean("editMode") ?: true,
                    petId = backStackEntry.arguments?.getString("petId"),
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
            composable(
                route = "pet_profile?petId={petId}",
                arguments = listOf(navArgument("petId") { type = NavType.StringType })
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("pet_list") }
                val petVM: PetViewModel = viewModel(
                    parentEntry,
                    factory = PetViewModelFactory(
                        petRepository = repository,
                        cloudinaryRepository = cloudinaryRepo
                    )
                )

                val petId = backStackEntry.arguments?.getString("petId")
                val pet = petVM.pets.value.find { it.petId == petId }

                if (pet != null) {
                    PetProfileScreen(
                        pet = pet,
                        navController = navController,
                        repository = repository
                    )
                }
            }


            composable("health_records") {
                val healthRecordViewModel: HealthRecordViewModel = viewModel(factory = HealthRecordViewModelFactory())
                HealthRecordScreen(
                    navController = navController,
                    petViewModel = petViewModel,
                    healthRecordViewModel = healthRecordViewModel
                )
            }
            composable("health_records") {
                val healthRecordViewModel: HealthRecordViewModel = viewModel(factory = HealthRecordViewModelFactory())
                HealthRecordScreen(navController, petViewModel, healthRecordViewModel)
            }

            composable(
                route = "add_health_record?petId={petId}",
                arguments = listOf(navArgument("petId") {
                    type = NavType.StringType
                    defaultValue = ""
                })
            ) { backStackEntry ->
                val petId = backStackEntry.arguments?.getString("petId") ?: ""
                val healthRecordViewModel: HealthRecordViewModel = viewModel(factory = HealthRecordViewModelFactory())
                AddHealthRecordScreen(navController, petViewModel, healthRecordViewModel, petId)
            }


            composable("reminder") { ReminderScreen(navController) }
            composable("reminder_form") { ReminderFormScreen(navController) }

            composable("health_dashboard") {
                // Chỉ dùng viewModel đã tạo sẵn, không tạo lại
                HealthTrackingScreen(
                    petViewModel = petViewModel,  // share chung
                    viewModel = healthTrackingViewModel,
                    navController = navController
                )
            }

            composable(
                "weight_height/{petId}",
                arguments = listOf(navArgument("petId") { type = NavType.StringType })
            ) { backStackEntry ->
                val petId = backStackEntry.arguments?.getString("petId")
                WeightHeightScreen(
                    navController = navController, // thêm dòng này
                    petId = petId
                )
            }
            composable("account_actions") { backStackEntry ->
                val auth = FirebaseAuth.getInstance()
                val viewModel: YourViewModel = viewModel(factory = YourViewModelFactory(auth))

                AccountActionsScreen(
                    navController = navController,
                    viewModel = viewModel,
                    onUpdateInfo = {navController.navigate("update_info") },
                    onUpdateAvatar = {},
                    onChangePassword = { navController.navigate("change_password")}
                )
            }
            composable("update_info") {
                UpdateInfoScreen(
                    navController = navController,
                    userViewModel = userViewModel,
                    onSave = { /* hành động sau khi lưu */ }
                )}
            composable("change_password") {
                ChangePasswordScreen(
                    onBack = { navController.popBackStack() },
                    onPasswordChanged = {
                        // hành động sau khi đổi mật khẩu
                    }
                )
            }
            composable("forgot_password") {
                ForgotPasswordScreen(
                    onNavigateLogin = { navController.popBackStack() }
                )
            }
            composable("medical_records") { TiemThuocListScreen(navController) }
            composable("add_record") { AddRecordScreen(navController) }
            composable("note") { NotificationScreen(navController) }
        }
    }
}




