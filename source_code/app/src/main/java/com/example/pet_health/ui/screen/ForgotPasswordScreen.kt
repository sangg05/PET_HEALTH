package com.example.pet_health.ui.screen

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pet_health.R

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

        Spacer(Modifier.height(30.dp))

        // ------ LOGO HÌNH TRÒN (LẤY Y CHANG REGISTER SCREEN) ------
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

        // ------ TIÊU ĐỀ ------
        Text(
            "QUÊN MẬT KHẨU",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(Modifier.height(20.dp))

        // ------------ EMAIL FIELD (GIỐNG REGISTER + CÓ ICON) ------------
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Nhập email khôi phục") },
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

        Spacer(Modifier.height(22.dp))

        // ------------ BUTTON GỬI MÃ ------------
        Button(
            onClick = { onSendClick(email) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(buttonColor),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                "Gửi mã khôi phục",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        Spacer(Modifier.height(18.dp))

        // ------------ QUAY LẠI ĐĂNG NHẬP ------------
        TextButton(onClick = onNavigateLogin) {
            Text(
                "Quay lại đăng nhập",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
