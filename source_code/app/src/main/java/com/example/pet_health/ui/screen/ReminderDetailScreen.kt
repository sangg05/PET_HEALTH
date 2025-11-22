package com.example.pet_health.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.foundation.BorderStroke
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

data class ReminderItem(
    val time: String,
    val date: String,
    var status: String, // Đổi thành 'var' để có thể cập nhật
    var color: Color // Đổi thành 'var' để có thể cập nhật
)

@Composable
fun ReminderDetailScreen(
    navController: NavController,
    pet: String,
    type: String,
    date: String,
    time: String,
    repeat: String,
    early: String,
    note: String
) {
    // Giải mã text đã encode
    val petName = URLDecoder.decode(pet, StandardCharsets.UTF_8.toString())
    val displayType = URLDecoder.decode(type, StandardCharsets.UTF_8.toString())
    val displayDate = URLDecoder.decode(date, StandardCharsets.UTF_8.toString())
    val displayTime = URLDecoder.decode(time, StandardCharsets.UTF_8.toString())
    val displayRepeat = URLDecoder.decode(repeat, StandardCharsets.UTF_8.toString())
    val displayEarly = URLDecoder.decode(early, StandardCharsets.UTF_8.toString())
    val displayNote = URLDecoder.decode(note, StandardCharsets.UTF_8.toString())

    // Fake history (Giả định lịch sử nhắc lịch có thể liên quan đến petName)
    // SỬ DỤNG remember { mutableStateListOf } để có thể cập nhật UI khi thay đổi list
    val initialReminders = remember {
        mutableStateListOf(
            ReminderItem("9:00", "25/10/2024", "Hoàn thành", Color(0xFFB6F2B8)),
            ReminderItem("9:00", "25/04/2025", "Hoàn thành", Color(0xFFB6F2B8)),
            ReminderItem(displayTime, displayDate, "Sắp tới", Color.White)
        )
    }

    // Luôn chọn mục "Sắp tới" hoặc mục cuối cùng để hiển thị nút hành động
    var selectedReminder by remember { mutableStateOf(initialReminders.last()) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Dùng LaunchedEffect để đảm bảo selectedReminder luôn là mục cuối cùng khi list thay đổi
    LaunchedEffect(initialReminders.size, initialReminders.last().status) {
        selectedReminder = initialReminders.last()
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

        // ---------- TOP BAR ----------
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF4A004A))
            }
            Text(
                text = "Chi tiết nhắc lịch",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A004A)
            )
        }

        Spacer(Modifier.height(16.dp))

        // ---------- MAIN INFO ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFF4A148C), RoundedCornerShape(10.dp))
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {

            Text(displayType, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))

            Spacer(Modifier.height(4.dp))
            // Sử dụng tên thú cưng (đã giải mã)
            Text("Thú cưng: $petName", fontWeight = FontWeight.SemiBold)

            Spacer(Modifier.height(4.dp))
            Text("Mũi: $displayNote", fontWeight = FontWeight.SemiBold)

            Spacer(Modifier.height(4.dp))
            Text("Ngày: $displayDate")
            Text("Giờ: $displayTime")

            Spacer(Modifier.height(4.dp))
            Text("Lặp lại: $displayRepeat")

            Spacer(Modifier.height(4.dp))
            Text("Nhắc sớm: $displayEarly")
        }

        Spacer(Modifier.height(16.dp))

        // ---------- HISTORY ----------
        initialReminders.forEach { reminder ->
            val isSelected = reminder == selectedReminder

            ReminderHistoryItem(
                time = "${reminder.time}    ${reminder.date}",
                status = reminder.status,
                // Đảm bảo item hiện tại sử dụng màu và trạng thái cập nhật
                backgroundColor = if (reminder.status == "Hoàn thành") Color(0xFFB6F2B8) else if (reminder.status == "Hoãn lại") Color(0xFFFFCCCC) else Color.White,
                textColor = if (reminder.status == "Hoàn thành") Color.Black else if (reminder.status == "Hoãn lại") Color.Red else Color.Red,
                border = isSelected,
                // Chỉ cho phép chỉnh sửa nếu trạng thái là "Sắp tới"
                showEdit = (reminder.status == "Sắp tới" || reminder.status == "Hoãn lại") && isSelected,
                onClick = {
                    selectedReminder = reminder
                },
                onEditClick = {
                    navController.navigate("reminder_form")
                }
            )
        }

        Spacer(Modifier.height(30.dp))

        // ---------- ACTION BUTTONS ----------
        // Chỉ hiển thị nút Hoãn lại và Hoàn thành nếu trạng thái đang là "Sắp tới" hoặc "Hoãn lại"
        if (selectedReminder.status == "Sắp tới" || selectedReminder.status == "Hoãn lại") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                // Nút HOÃN LẠI
                Button(
                    // Logic: Cập nhật trạng thái item hiện tại thành "Hoãn lại" và đổi màu
                    onClick = {
                        val index = initialReminders.indexOf(selectedReminder)
                        if (index != -1) {
                            initialReminders[index] = initialReminders[index].copy(
                                status = "Hoãn lại",
                                color = Color(0xFFFFCCCC) // Màu đỏ nhạt cho Hoãn lại
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30))
                ) {
                    Text("Hoãn lại", color = Color.White, fontSize = 16.sp)
                }

                // Nút HOÀN THÀNH
                Button(
                    onClick = { showConfirmDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
                ) {
                    Text("Hoàn thành", color = Color.Black, fontSize = 16.sp)
                }
            }
        }

        // ---------- DIALOG ----------
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Xác nhận hoàn thành") },
                text = { Text("Bạn xác nhận đã hoàn thành chứ?") },
                confirmButton = {
                    TextButton(onClick = {
                        // Logic: Cập nhật trạng thái item hiện tại thành "Hoàn thành" và đổi màu xanh
                        val index = initialReminders.indexOf(selectedReminder)
                        if (index != -1) {
                            initialReminders[index] = initialReminders[index].copy(
                                status = "Hoàn thành",
                                color = Color(0xFFB6F2B8)
                            )
                        }
                        showConfirmDialog = false
                    }) {
                        Text("Đồng ý", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Hủy", color = Color.Gray)
                    }
                }
            )
        }
    }
}

// Hàm ReminderHistoryItem không cần thay đổi nhiều
@Composable
fun ReminderHistoryItem(
    time: String,
    status: String,
    backgroundColor: Color,
    textColor: Color,
    border: Boolean = false,
    showEdit: Boolean = false,
    onClick: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { onClick?.invoke() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (border) BorderStroke(2.dp, Color(0xFF4A148C)) else null
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(time, fontWeight = FontWeight.Bold)
                Text(status, color = textColor)
            }

            if (showEdit && onEditClick != null) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.Black,
                    modifier = Modifier.clickable { onEditClick() }
                )
            }
        }
    }
}