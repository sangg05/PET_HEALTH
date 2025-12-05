package com.example.pet_health.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pet_health.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.pet_health.data.repository.UserRepository
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    userRepository: UserRepository,
    onNavigateLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val background = Color(0xFFF3CCE4)
    val buttonColor = Color(0xFFDB91D6)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
            .imePadding(), // đẩy layout khi bàn phím xuất hiện
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(30.dp))

        // LOGO
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
                        scaleX = 1.3f
                        scaleY = 1.3f
                        translationX = -20f
                        translationY = 20f
                    }
                    .clip(CircleShape)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "ĐĂNG KÝ",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(Modifier.height(20.dp))

        // EMAIL
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email") },
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
            }
        )

        Spacer(Modifier.height(14.dp))

        // TÊN HIỂN THỊ
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Tên hiển thị") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_name),
                    contentDescription = "name",
                    modifier = Modifier.size(22.dp)
                )
            }
        )

        Spacer(Modifier.height(14.dp))

        // MẬT KHẨU
        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            placeholder = { Text("Mật khẩu") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_key),
                    contentDescription = "password",
                    modifier = Modifier.size(22.dp)
                )
            }
        )

        Spacer(Modifier.height(14.dp))

        // XÁC NHẬN MẬT KHẨU
        OutlinedTextField(
            value = confirmPass,
            onValueChange = { confirmPass = it },
            placeholder = { Text("Xác nhận mật khẩu") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_key),
                    contentDescription = "confirm password",
                    modifier = Modifier.size(22.dp)
                )
            }
        )

        Spacer(Modifier.height(22.dp))

        Button(
            onClick = {
                if (pass != confirmPass) {
                    Toast.makeText(context, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                loading = true
                scope.launch {
                    val success = try {
                        userRepository.registerUser(name, email, pass)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Đăng ký thất bại: ${e.message}", Toast.LENGTH_LONG).show()
                        false
                    }
                    loading = false

                    if (success) {
                        Toast.makeText(context, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                        kotlinx.coroutines.delay(500) // delay để toast hiển thị
                        onNavigateLogin() // trở về màn hình login
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (loading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Đăng ký", color = Color.White, fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Đã có tài khoản? ")
            Text(
                text = "Đăng nhập",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateLogin() },
                color = Color.Black
            )
        }
    }
}

