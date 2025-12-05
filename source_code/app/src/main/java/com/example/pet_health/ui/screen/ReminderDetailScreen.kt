package com.example.pet_health.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
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

// Import ViewModel và Entity
import com.example.pet_health.ui.viewmodel.ReminderViewModel
import com.example.pet_health.data.entity.Reminder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDetailScreen(
    navController: NavController,
    reminderId: String, // Chỉ nhận ID
    viewModel: ReminderViewModel // Inject ViewModel
) {
    // 1. Lấy danh sách từ ViewModel
    val reminders by viewModel.reminders

    // 2. Tìm Reminder hiện tại dựa trên ID
    val currentReminder = reminders.find { it.id == reminderId }

    // Nếu không tìm thấy (ví dụ trường hợp hy hữu bị xóa), quay về màn hình trước
    if (currentReminder == null) {
        // Dùng SideEffect để tránh lỗi khi render
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    var showConfirmDialog by remember { mutableStateOf(false) }

    // Dùng Scaffold để có Top Bar chuẩn
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chi tiết nhắc lịch",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = lightPink)
            )
        }
    ) { innerPadding ->

        // Box chứa nền Gradient và nội dung chính
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Padding để nội dung không bị TopBar che mất
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFF6C2), // Vàng nhạt
                            Color(0xFFFFD6EC), // Hồng nhạt
                            Color(0xFFEAD6FF)  // Tím nhạt
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Cho phép cuộn
            ) {

                // ---------- MAIN INFO CARD (Thông tin chung) ----------
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color(0xFF4A148C), RoundedCornerShape(10.dp))
                        .background(Color.White, RoundedCornerShape(10.dp))
                        .padding(16.dp)
                ) {
                    Text(currentReminder.type, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A), fontSize = 20.sp)
                    Text(currentReminder.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    Spacer(Modifier.height(8.dp))
                    Divider(color = Color.LightGray)
                    Spacer(Modifier.height(8.dp))

                    Text("Thú cưng: ${currentReminder.petName}", fontWeight = FontWeight.SemiBold)

                    if(currentReminder.note.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text("Ghi chú: ${currentReminder.note}")
                    }

                    Spacer(Modifier.height(4.dp))
                    Text("Lặp lại: ${currentReminder.repeat}")

                    Spacer(Modifier.height(4.dp))
                    Text("Nhắc sớm: ${currentReminder.earlyNotify}")
                }

                Spacer(Modifier.height(20.dp))
                Text("Trạng thái hiện tại", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF4A004A))
                Spacer(Modifier.height(8.dp))

                // ---------- CURRENT STATUS ITEM ----------
                // Hiển thị trạng thái của lần nhắc này

                // Xác định màu sắc dựa trên trạng thái
                val statusColor = when (currentReminder.status) {
                    "Hoàn thành" -> Color(0xFFB6F2B8) // Xanh lá
                    "Hoãn lại" -> Color(0xFFFFCCCC)   // Đỏ
                    else -> Color.White               // Trắng (Sắp tới)
                }

                val statusTextColor = if (currentReminder.status == "Hoàn thành") Color(0xFF2E7D32) else Color.Red

                ReminderStatusCard(
                    time = "${currentReminder.time}   ${currentReminder.date}",
                    status = currentReminder.status,
                    backgroundColor = statusColor,
                    textColor = statusTextColor,
                    // Chỉ hiện nút sửa nếu chưa hoàn thành (Logic tùy bạn chọn)
                    showEdit = currentReminder.status == "Sắp tới",
                    onEditClick = {
                        // CHỨC NĂNG SỬA: Chuyển sang màn hình Form kèm theo ID
                        navController.navigate("reminder_form?reminderId=${currentReminder.id}")
                    }
                )

                Spacer(Modifier.height(30.dp))

                // ---------- ACTION BUTTONS ----------
                // Chỉ hiển thị nút khi chưa hoàn thành
                if (currentReminder.status != "Hoàn thành") {
                    // Nút "Hoàn thành" full width
                    Button(
                        onClick = { showConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.fillMaxWidth() // Chiếm hết chiều ngang
                    ) {
                        Text("Hoàn thành", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }

    // ---------- DIALOG CONFIRM ----------
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Xác nhận") },
            text = { Text("Đánh dấu lịch này là đã hoàn thành?") },
            confirmButton = {
                TextButton(onClick = {
                    // Gọi ViewModel cập nhật trạng thái
                    viewModel.updateReminderStatus(currentReminder.id, "Hoàn thành")
                    showConfirmDialog = false
                    navController.popBackStack()
                }) {
                    Text("Đồng ý", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Hủy", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }
}

// Composable con để hiển thị thẻ trạng thái (Thay thế cho ReminderHistoryItem cũ)
@Composable
fun ReminderStatusCard(
    time: String,
    status: String,
    backgroundColor: Color,
    textColor: Color,
    showEdit: Boolean,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(time, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(status, color = textColor, fontWeight = FontWeight.Medium)
            }

            if (showEdit) {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Black)
                }
            }
        }
    }
}