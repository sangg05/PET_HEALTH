package com.example.pet_health.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.foundation.BorderStroke
import com.google.accompanist.flowlayout.FlowRow
import com.example.pet_health.FilterChipStyled
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// ==================== DATA MODEL ====================
data class Reminder(
    val id: Int,
    val type: String,
    val title: String,
    val date: String,
    val time: String,
    val repeat: String,
    val early: String = ""   // ★ thêm default để tránh crash
)

// ==================== MAIN SCREEN ====================
@Composable
fun ReminderScreen(navController: NavController? = null) {

    var reminderList by remember {
        mutableStateOf(
            mutableListOf(
                Reminder(1, "Tiêm phòng", "Mũi FVRCP #4", "25/10/2026", "9:00", "6 tháng", "30 phút"),
                Reminder(2, "Tiêm phòng", "Mũi FVRCP #3", "25/04/2025", "9:00", "6 tháng", "1 giờ")
            )
        )
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Sắp tới", "Quá hạn", "Đã xong")

    var selectedFilter by remember { mutableStateOf("Tất cả") }
    var searchText by remember { mutableStateOf("") }

    val filteredList = reminderList.filter {
        (selectedFilter == "Tất cả" || it.type == selectedFilter) &&
                (searchText.isBlank() || it.title.contains(searchText, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFF7C8E0), Color(0xFFF9E6F2))
                )
            )
            .padding(16.dp)
    ) {
        // ===== Header =====
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController?.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF4A004A))
            }
            Text(
                "Nhắc lịch",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A004A)
            )
        }

        Spacer(Modifier.height(8.dp))

        // ===== Tabs =====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEachIndexed { index, text ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { selectedTab = index }
                ) {
                    Text(
                        text,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 20.sp,
                        color = if (selectedTab == index) Color(0xFF5D2C02) else Color.Gray
                    )
                    if (selectedTab == index) {
                        Box(
                            modifier = Modifier
                                .height(3.dp)
                                .width(50.dp)
                                .background(Color(0xFF5D2C02))
                        )
                    } else {
                        Spacer(Modifier.height(3.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ===== Search Box =====
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Tìm kiếm...") },
            shape = RoundedCornerShape(13.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Gray,
                focusedBorderColor = Color(0xFF9C27B0),
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        // ===== Filter Chips =====
        val filters = listOf("Tất cả", "Tiêm phòng", "Tẩy giun", "Tái khám", "Thuốc", "Khác")

        FlowRow(
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            filters.forEach { item ->
                FilterChipStyled(
                    text = item,
                    selected = item == selectedFilter,
                    onClick = { selectedFilter = item }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // ===== LIST =====
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(filteredList, key = { it.id }) { reminder ->

                ReminderCardStyled(
                    reminder = reminder,
                    onClick = {
                        // =============== Encode Safe ===============
                        val encodedType = URLEncoder.encode(reminder.type, StandardCharsets.UTF_8)
                        val encodedTitle = URLEncoder.encode(reminder.title, StandardCharsets.UTF_8)
                        val encodedDate = URLEncoder.encode(reminder.date, StandardCharsets.UTF_8)
                        val encodedTime = URLEncoder.encode(reminder.time, StandardCharsets.UTF_8)
                        val encodedRepeat = URLEncoder.encode(reminder.repeat, StandardCharsets.UTF_8)
                        val encodedEarly = URLEncoder.encode(reminder.early, StandardCharsets.UTF_8)

                        navController?.navigate(
                            "reminder_detail/${reminder.id}/$encodedType/$encodedTitle/$encodedDate/$encodedTime/$encodedRepeat/$encodedEarly"
                        )
                    },

                    onDelete = {
                        reminderList = reminderList.filterNot { it.id == reminder.id }.toMutableList()
                    }
                )
            }
        }

        // ===== Vaccine Book Button =====
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Button(
                onClick = { navController?.navigate("tiem_thuoc_list") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD1B3F1)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Sổ tiêm & thuốc", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(10.dp))

        // ===== Add Button =====
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            FloatingActionButton(
                onClick = { navController?.navigate("reminder_form") },
                containerColor = Color(0xFFB6F2B8),
                shape = CircleShape,
                modifier = Modifier.size(58.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm", tint = Color.Black, modifier = Modifier.size(32.dp))
            }
        }

        Spacer(Modifier.height(12.dp))
        BottomNavigationBarStyled(navController)
    }
}

// ==================== CARD ====================
@Composable
fun ReminderCardStyled(
    reminder: Reminder,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.5.dp, Color(0xFF8A2BE2))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(reminder.type, color = Color(0xFF6A1B9A), fontWeight = FontWeight.Bold)
            Text(reminder.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("${reminder.time}   ${reminder.date}", color = Color.Black)
                    Text("Lặp: ${reminder.repeat}", color = Color.DarkGray, fontSize = 13.sp)
                }
                Row {
                    Icon(Icons.Default.Done, contentDescription = null, tint = Color(0xFF4CAF50))
                    Spacer(Modifier.width(10.dp))
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFFF3B30),
                        modifier = Modifier.clickable { onDelete() }
                    )
                }
            }
        }
    }
}

// ==================== BOTTOM NAV ====================
@Composable
fun BottomNavigationBarStyled(navController: NavController? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFAD3F5))
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🏠", fontSize = 22.sp, modifier = Modifier.clickable {
            navController?.navigate("reminder_screen")
        })
        Text("🔔", fontSize = 22.sp, modifier = Modifier.clickable {
            navController?.navigate("notification_screen")
        })
        Text("👤", fontSize = 22.sp)
    }
}
