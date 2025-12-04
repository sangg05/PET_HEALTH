package com.example.pet_health.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pet_health.R

@Composable
fun ResetPasswordScreen(
    email: String,
    onResetSuccess: () -> Unit
) {
    val background = Color(0xFFF3CCE4)
    val buttonColor = Color(0xFFDB91D6)

    var code by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

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
            "ĐẶT LẠI MẬT KHẨU",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(Modifier.height(10.dp))

        Text(
            "Email: $email",
            color = Color.Black,
            fontSize = 15.sp
        )

        Spacer(Modifier.height(22.dp))

        // ------------ CODE VERIFY ------------
        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            placeholder = { Text("Nhập mật khẩu mới") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_key),
                    contentDescription = "verify code",
                    modifier = Modifier.size(22.dp)
                )
            }
        )

        Spacer(Modifier.height(16.dp))

        // ------------ NEW PASSWORD ------------
        OutlinedTextField(
            value = newPass,
            onValueChange = { newPass = it },
            placeholder = { Text("Xác nhận lại mật khẩu") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_password_access),
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

        Spacer(Modifier.height(22.dp))

        Button(
            onClick = { onResetSuccess() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(buttonColor),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                "XÁC NHẬN",
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}
