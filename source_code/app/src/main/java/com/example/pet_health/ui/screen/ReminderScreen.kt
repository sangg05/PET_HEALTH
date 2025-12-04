package com.example.pet_health.ui.screen

import androidx.compose.foundation.BorderStroke
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
import com.google.accompanist.flowlayout.FlowRow

// Import đúng Entity và ViewModel
import com.example.pet_health.data.entity.Reminder
import com.example.pet_health.ui.viewmodel.ReminderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    navController: NavController? = null,
    viewModel: ReminderViewModel
) {
    val reminderList by viewModel.reminders

    var selectedFilter by remember { mutableStateOf("Tất cả") }
    var searchText by remember { mutableStateOf("") }

    // 1. Định nghĩa danh sách các loại mặc định để so sánh
    val defaultTypes = listOf("Tiêm phòng", "Tẩy giun", "Tái khám", "Thuốc")

    // Danh sách hiển thị trên Filter Chip
    val filterOptions = listOf("Tất cả") + defaultTypes + "Khác"

    // 2. Cập nhật Logic lọc thông minh hơn
    val filteredList = reminderList.filter { reminder ->
        // Logic lọc theo Loại (Type)
        val isTypeMatch = when (selectedFilter) {
            "Tất cả" -> true
            "Khác" -> reminder.type !in defaultTypes // <--- QUAN TRỌNG: Nếu type không nằm trong danh sách mặc định thì coi là "Khác"
            else -> reminder.type == selectedFilter
        }

        // Logic lọc theo Tìm kiếm (Search)
        val isSearchMatch = searchText.isBlank() || reminder.title.contains(searchText, ignoreCase = true)

        isTypeMatch && isSearchMatch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Nhắc lịch",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFC0CB))
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
                Icon(Icons.Default.Home, contentDescription = "Trang chủ", tint = Color(0xFF6200EE), modifier = Modifier.size(32.dp))
                Icon(Icons.Default.Notifications, contentDescription = "Thông báo", tint = Color.LightGray, modifier = Modifier.size(32.dp))
                Icon(Icons.Default.Person, contentDescription = "Hồ sơ", tint = Color.LightGray, modifier = Modifier.size(32.dp))
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFF7C8E0), Color(0xFFF9E6F2))
                    )
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                // ===== Search Bar =====
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Tìm kiếm...") },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Gray,
                        focusedBorderColor = Color(0xFF9C27B0),
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                // ===== Filter Chips (Dùng danh sách filterOptions đã tạo ở trên) =====
                FlowRow(
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    filterOptions.forEach { item ->
                        FilterChipStyled(
                            text = item,
                            selected = item == selectedFilter,
                            onClick = { selectedFilter = item }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ===== List of reminders =====
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredList, key = { it.id }) { reminder ->

                        ReminderCardStyled(
                            reminder = reminder,
                            onClick = {
                                navController?.navigate("reminder_detail/${reminder.id}")
                            },
                            onDelete = {
                                viewModel.deleteReminder(reminder.id)
                            }
                        )
                    }
                }
            }

            // ===== Floating Action Button =====
            FloatingActionButton(
                onClick = { navController?.navigate("reminder_form") },
                containerColor = Color(0xFFB6F2B8),
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm", tint = Color.Black)
            }
        }
    }
}


// ==================== FILTER CHIP ====================
@Composable
fun FilterChipStyled(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (selected) Color(0xFFCE93D8) else Color.White,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFF9C27B0)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            color = if (selected) Color.White else Color.Black
        )
    }
}

// ==================== CARD ====================
@Composable
fun ReminderCardStyled(
    reminder: Reminder,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val cardColor = when(reminder.status) {
        "Hoàn thành" -> Color(0xFFB6F2B8)
        "Hoãn lại" -> Color(0xFFFFCCCC)
        else -> Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.5.dp, Color(0xFF8A2BE2))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(reminder.type, color = Color(0xFF6A1B9A), fontWeight = FontWeight.Bold)
                if (reminder.status != "Sắp tới") {
                    Text(reminder.status, fontSize = 12.sp, color = Color.Gray)
                }
            }

            Text(reminder.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("${reminder.time}   ${reminder.date}")
                    Text("Lặp: ${reminder.repeat}", color = Color.DarkGray, fontSize = 13.sp)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF3B30))
                }
            }
        }
    }
}