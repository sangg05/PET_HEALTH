package com.example.pet_health.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pet_health.R
import androidx.navigation.NavController
import com.example.pet_health.data.repository.UserRepository


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountManagementScreen(
    navController: NavController,
    onBack: () -> Unit = {},
    onNavigateMore: () -> Unit = {},
    userRepository: UserRepository,
) {
    val background = Color(0xFFF3CCE4)
    val cardColor = Color.White

    var note by remember { mutableStateOf("") }
    val user = userRepository.currentUser.value


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý tài khoản", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateMore) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_more),
                            contentDescription = "More",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = lightPink)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color.White),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* xử lý Home */ }) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Trang chủ",
                        tint = Color(0xFF6200EE),
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(onClick = { /* xử lý Notifications */ }) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Thông báo",
                        tint = Color.LightGray,
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(onClick = {navController.navigate("account")
                }) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Hồ sơ",
                        tint = Color.LightGray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    ){ padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ---------------- Avatar ----------------
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                // đổi bằng avatar bạn có
                Image(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "Avatar",
                    modifier = Modifier.size(110.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ---------------- THÔNG TIN CHUNG ----------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        "Thông tin chung",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(Modifier.height(12.dp))
                    Text("Tên: ${user?.name ?: "Chưa có"}")
                    Text("Tài khoản: ${user?.email ?: "Chưa có"}")
                    Text("Điện thoại: ${user?.phone ?: "Chưa có"}")
                    Spacer(Modifier.height(4.dp))
                    Text("Giới tính: ${user?.gender ?: "Chưa cập nhật"}")
                    Spacer(Modifier.height(4.dp))
                    Text("Ngày sinh: ${user?.birthDate ?: "Chưa cập nhật"}")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ---------------- GHI CHÚ ----------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text("Ghi chú", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF8F8F8), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        BasicTextField(
                            value = note,
                            onValueChange = { note = it },
                            modifier = Modifier
                                .fillMaxSize(),
                            textStyle = TextStyle(color = Color.Black)
                        ) { innerTextField ->
                            // decorationBox body
                            if (note.isEmpty()) {
                                Text(
                                    text = "Nhập các ghi chú cá nhân",
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        }
                    }
                }
            }
        }
    }
}
