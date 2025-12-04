package com.example.pet_health.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Định nghĩa màu chủ đạo (Primary Color) để dễ thay đổi
val PrimaryColor = Color(0xFF6200EE)

@Composable
fun NotificationScreen(navController: NavController) {

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color.White),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate("home")}) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Trang chủ",
                        tint = Color(0xFF6200EE),
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(onClick = {navController.navigate("note")}) {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF6C2),
                            Color(0xFFFFD6EC),
                            Color(0xFFEAD6FF)
                        )
                    )
                )
                .padding(paddingValues) // Áp dụng padding của Scaffold
                .padding(horizontal = 16.dp), // Thêm padding ngang cho nội dung chính
        ) {
            // ===== Thanh tiêu đề =====
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = AccentColor
                    )
                }
                Text(
                    "Thông báo",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentColor
                )
            }

            Spacer(Modifier.height(10.dp))

            // ===== Danh sách thông báo =====
            val notifications = listOf(
                NotificationItem("Mũi FVRCP 9:00 - Hôm nay", "2 phút trước"),
                NotificationItem("Mũi FVRCP 9:00 - Hôm nay", "2 phút trước"),
                NotificationItem("Mũi FVRCP 9:00 - Ngày mai", "2 phút trước")
            )

            // Dùng LazyColumn nếu danh sách dài, nhưng forEach cho ví dụ ngắn vẫn OK
            Column(modifier = Modifier.fillMaxWidth()) {
                notifications.forEach {
                    NotificationCard(it.title, it.timeAgo, navController)
                }
            }
        }
    }
}

// --- Hàm Composable chung cho Item trong BottomBar ---
@Composable
fun BottomBarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    route: String,
    currentRoute: String,
    navController: NavController
) {
    val isSelected = route == currentRoute
    val tintColor = if (isSelected) PrimaryColor else Color.LightGray

    IconButton(
        onClick = {
            if (!isSelected) {
                navController.navigate(route) {
                    // Để tránh tạo ra nhiều bản sao của cùng một màn hình
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    // Tránh tạo ra nhiều bản sao khi nhấn nhanh
                    launchSingleTop = true
                    // Khôi phục trạng thái đã lưu trước đó
                    restoreState = true
                }
            }
        }
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = tintColor,
            modifier = Modifier.size(32.dp)
        )
    }
}

data class NotificationItem(val title: String, val timeAgo: String)

@Composable
fun NotificationCard(title: String, timeAgo: String, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { navController.navigate("reminder_detail") },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("PetHealth - Nhắc lịch", fontWeight = FontWeight.Bold)
            Text(title)
            Spacer(Modifier.height(4.dp))
            Text(timeAgo, fontSize = 12.sp, color = Color.Gray)
            Text(
                "nhấn để xem chi tiết",
                fontWeight = FontWeight.Medium,
                color = AccentColor,
                fontSize = 13.sp
            )
        }
    }
}