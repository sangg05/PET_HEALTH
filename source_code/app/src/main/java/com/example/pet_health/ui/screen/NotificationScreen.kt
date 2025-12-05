package com.example.pet_health.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pet_health.data.entity.NotificationEntity
import com.example.pet_health.ui.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel
) {
    val notifications by viewModel.notifications.collectAsState()

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
            // ===== Title =====
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

            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Chưa có thông báo nào.", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(notifications, key = { it.id }) { noti ->
                        NotificationCardItem(
                            notification = noti,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCardItem(
    notification: NotificationEntity,
    navController: NavController
) {
    val dateStr = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
        .format(Date(notification.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable {
                // Mở màn hình chi tiết nhắc nhở
                navController.navigate("reminder_detail/${notification.reminderId}")
            },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Column(Modifier.padding(12.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("PetHealth - Nhắc nhở", fontWeight = FontWeight.Bold)
                Text(dateStr, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(Modifier.height(4.dp))

            Text(
                notification.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(2.dp))

            Text(
                notification.message,
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(Modifier.height(4.dp))

            Text(
                "Nhấn để xem chi tiết",
                fontSize = 13.sp,
                color = Color(0xFF4A004A),
                fontWeight = FontWeight.Medium
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
                tint = Color.LightGray,
                modifier = Modifier.size(32.dp)
            )
        }

        // NOTIFICATIONS (active)
        IconButton(onClick = { navController.navigate("notification") }) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = "Thông báo",
                tint = Color(0xFF6200EE),
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