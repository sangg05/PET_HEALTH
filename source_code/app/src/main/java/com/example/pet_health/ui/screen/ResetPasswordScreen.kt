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

@Composable
fun ResetPasswordScreen(
    email: String,
    onResetSuccess: () -> Unit
) {
    val background = Color(0xFFF3CCE4)
    val buttonColor = Color(0xFFDB91D6)

    var code by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("ĐẶT LẠI MẬT KHẨU", fontSize = 26.sp)

        Spacer(Modifier.height(10.dp))
        Text("Email: $email")

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Nhập mã xác minh") }
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = newPass,
            onValueChange = { newPass = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Mật khẩu mới") }
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { onResetSuccess() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(buttonColor)
        ) {
            Text("Xác nhận", color = Color.White)
        }
    }
}
