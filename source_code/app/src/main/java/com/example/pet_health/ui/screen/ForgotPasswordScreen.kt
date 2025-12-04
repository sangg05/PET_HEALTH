package com.example.pet_health.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pet_health.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ForgotPasswordScreen(
    onNavigateLogin: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }

    val background = Color(0xFFF3CCE4)
    val buttonColor = Color(0xFFDB91D6)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(30.dp))

        // Logo
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

        Spacer(Modifier.height(20.dp))

        Text(
            "QUÊN MẬT KHẨU",
            fontSize = 26.sp
        )

        Spacer(Modifier.height(20.dp))

        // Email input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Nhập email khôi phục") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(Modifier.height(22.dp))

        // Button gửi link reset
        Button(
            onClick = {
                if (email.isBlank()) {
                    Toast.makeText(context, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Đã gửi email khôi phục! Vui lòng kiểm tra hộp thư.",
                                Toast.LENGTH_LONG
                            ).show()
                            onNavigateLogin() // quay lại login
                        } else {
                            Toast.makeText(
                                context,
                                task.exception?.message ?: "Có lỗi xảy ra",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Gửi link khôi phục")
        }

        Spacer(Modifier.height(18.dp))

        TextButton(onClick = onNavigateLogin) {
            Text("Quay lại đăng nhập", color = Color.Black)
        }
    }
}

