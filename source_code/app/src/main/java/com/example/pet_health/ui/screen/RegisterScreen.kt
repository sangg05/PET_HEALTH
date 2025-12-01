package com.example.pet_health.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pet_health.R
import androidx.compose.ui.text.font.FontWeight

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateLogin: () -> Unit
) {
    val background = Color(0xFFF3CCE4)
    val buttonColor = Color(0xFFDB91D6)

    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(30.dp))

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

        Spacer(Modifier.height(20.dp))

        Text(
            "ĐĂNG KÝ",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(Modifier.height(20.dp))

        // ------------ EMAIL ------------
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
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

        // ------------ Tên hiện thị ------------
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Tên hiển thị") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
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

        // ------------ PASSWORD ------------
        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            placeholder = { Text("Mật khẩu") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_key),
                    contentDescription = "password",
                    modifier = Modifier.size(22.dp)
                )
            }
        )

        Spacer(Modifier.height(14.dp))

        // ------------ CONFIRM PASSWORD ------------
        OutlinedTextField(
            value = confirmPass,
            onValueChange = { confirmPass = it },
            placeholder = { Text("Xác nhận mật khẩu") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
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
            onClick = { onRegisterSuccess() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(buttonColor),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("ĐĂNG KÝ", color = Color.White, fontSize = 18.sp)
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
