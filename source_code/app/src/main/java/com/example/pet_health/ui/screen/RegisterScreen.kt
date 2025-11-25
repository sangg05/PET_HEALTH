package com.example.pet_health.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pet_health.R

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateLogin: () -> Unit
) {
    val background = Color(0xFFF3CCE4)
    val buttonColor = Color(0xFFDB91D6)

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("ĐĂNG KÝ", fontSize = 26.sp)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Tên người dùng") })
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Email") })
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(value = pass, onValueChange = { pass = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Mật khẩu") })
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(value = confirmPass, onValueChange = { confirmPass = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Xác nhận mật khẩu") })

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { onRegisterSuccess() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(buttonColor)
        ) {
            Text("ĐĂNG KÝ", color = Color.White)
        }

        TextButton(onClick = onNavigateLogin) {
            Text("Đã có tài khoản? Đăng nhập")
        }
    }
}
