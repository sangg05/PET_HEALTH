package com.example.pet_health.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

@Composable
fun NotificationScreen(navController: NavController) {

    Scaffold(
        bottomBar = {
            BottomNavigationBarNotification(navController)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFF7C8E0), Color(0xFFF9E6F2))
                    )
                )
                .padding(16.dp)
        ) {
            // ===== Thanh tiêu đề =====
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF4A004A))
                }
                Text(
                    "Thông báo",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A004A)
                )
            }

            Spacer(Modifier.height(10.dp))

            // ===== Danh sách thông báo =====
            val notifications = listOf(
                NotificationItem("Mũi FVRCP 9:00 - Hôm nay", "2 phút trước"),
                NotificationItem("Mũi FVRCP 9:00 - Hôm nay", "2 phút trước"),
                NotificationItem("Mũi FVRCP 9:00 - Ngày mai", "2 phút trước")
            )

            notifications.forEach {
                NotificationCard(it.title, it.timeAgo, navController)
            }

            Spacer(Modifier.height(12.dp))
        }
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
                color = Color(0xFF4A004A),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun BottomNavigationBarNotification(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // HOME
        IconButton(onClick = { navController.navigate("home") }) {
            Icon(
                Icons.Default.Home,
                contentDescription = "Trang chủ",
                tint = Color.LightGray,   // Không active
                modifier = Modifier.size(32.dp)
            )
        }

        // NOTIFICATIONS (active)
        IconButton(onClick = { }) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = "Thông báo",
                tint = Color(0xFF6200EE),   // Active
                modifier = Modifier.size(32.dp)
            )
        }

        // ACCOUNT
        IconButton(onClick = { navController.navigate("account") }) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Tài khoản",
                tint = Color.LightGray,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
