package com.example.pet_health.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pet_health.R
import com.example.pet_health.data.repository.UserRepository
import com.example.pet_health.ui.viewmodel.LoginViewModel
import com.example.pet_health.ui.viewmodel.LoginViewModelFactory
import com.example.pet_health.ui.viewmodel.PetViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.format.TextStyle

@Composable
fun LoginScreen(
    navController: NavController,
    onLoginClick: (String, String) -> Unit,
    onNavigateRegister: () -> Unit,
    userRepository: UserRepository,
    petViewModel: PetViewModel,
    onNavigateForgot: () -> Unit
) {
    val background = Color(0xFFF3CCE4)
    val buttonColor = Color(0xFFDB91D6)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showPassword by remember { mutableStateOf(false) }



    val viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = LoginViewModelFactory(userRepository)
    )
    val email by viewModel.savedEmail
    val password by viewModel.savedPassword
    val rememberMe by viewModel.rememberMe


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(40.dp))

        // ------ LOGO HÌNH TRÒN ------
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.pet_logo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = 1.3f // phóng to ảnh theo chiều ngang
                        scaleY = 1.3f // phóng to ảnh theo chiều dọc
                        translationX = -20f
                        translationY = 20f
                    }
                    .clip(CircleShape)
            )
        }


        Spacer(Modifier.height(24.dp))

        Text(
            "ĐĂNG NHẬP",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(Modifier.height(20.dp))

        // ------ EMAIL ------
        OutlinedTextField(
            value = email,
            onValueChange = {  viewModel.savedEmail.value = it},
            placeholder = { Text("Nhập email") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_email),
                    contentDescription = "email",
                    modifier = Modifier.size(22.dp)
                )
            },
        )

        Spacer(Modifier.height(16.dp))

// ------ PASSWORD ------
        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.savedPassword.value = it},
            placeholder = { Text("Nhập mật khẩu") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(14.dp)),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_key),
                    contentDescription = "password",
                    modifier = Modifier.size(22.dp)
                )
            },
            visualTransformation = if (showPassword) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                        contentDescription = "Toggle Password"
                    )
                }
            }
        )

        Spacer(Modifier.height(8.dp))

        // ------ GHI NHỚ ĐĂNG NHẬP ------
        val rememberMe by viewModel.rememberMe
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { viewModel.rememberMe.value = it },
                colors = CheckboxDefaults.colors(
                    checkmarkColor = Color.White,
                    checkedColor = buttonColor,
                    uncheckedColor = Color.Black
                ),

            )
            Text(
                "Ghi nhớ đăng nhập",
                color = Color.Black,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                scope.launch {
                    val (success, message) = viewModel.login(email, password, rememberMe)

                    if (success) {
                        petViewModel.refreshPetsForCurrentUser()

                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("ĐĂNG NHẬP", color = Color.White, fontSize = 18.sp)
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = { navController.navigate("forgot_password") }) {
            Text(
                "Quên mật khẩu?",
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(6.dp))

        // ------ CĂN HÀNG ĐÚNG CHUẨN ------
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Bạn chưa có tài khoản?", color = Color.Black)
            TextButton(onClick = onNavigateRegister) {
                Text(
                    "Đăng ký",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
            }
        }
    }
}
