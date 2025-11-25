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
fun ForgotPasswordScreen(
    onSendClick: (String) -> Unit,
    onNavigateLogin: () -> Unit
) {
    val background = Color(0xFFF3CCE4)
    val buttonColor = Color(0xFFDB91D6)

    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("QUÊN MẬT KHẨU", fontSize = 26.sp)

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Nhập email khôi phục") }
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { onSendClick(email) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(buttonColor)
        ) {
            Text("Gửi mã khôi phục", color = Color.White)
        }

        TextButton(onClick = onNavigateLogin) {
            Text("Quay lại đăng nhập")
        }
    }
}
