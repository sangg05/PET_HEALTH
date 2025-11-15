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

@Composable
fun ReminderDetailScreen(
    navController: NavController,
    pet: String,
    type: String,
    date: String,
    time: String,
    repeat: String,
    note: String,
    early: String
) {

    // --- FIX TYPE ---
    val displayType = type.replace("+", " ")

    // Fake data history (sau này API trả về)
    val reminders = listOf(
        ReminderItem("9:00", "25/10/2024", "Hoàn thành", Color(0xFFB6F2B8)),
        ReminderItem("9:00", "25/04/2025", "Hoàn thành", Color(0xFFB6F2B8)),
        ReminderItem(time, date, "Sắp tới", Color.White)
    )

    var selectedReminder by remember { mutableStateOf(reminders.last()) }
    var status by remember { mutableStateOf(selectedReminder.status) }
    var showConfirmDialog by remember { mutableStateOf(false) }

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

        // ---------- MAIN INFOMATION CARD ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFF4A148C), RoundedCornerShape(10.dp))
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {

            Text(displayType, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
            Spacer(Modifier.height(4.dp))

            Text("Mũi: $note", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))

            Text("Giờ: $time")
            Text("Ngày: $date")
            Text("Lặp lại: $repeat")
        }

        Spacer(Modifier.height(16.dp))

        // ---------- HISTORY ----------
        reminders.forEach { reminder ->
            val isSelected = reminder == selectedReminder

            ReminderHistoryItem(
                time = "${reminder.time}   ${reminder.date}",
                status = reminder.status,
                backgroundColor = reminder.color,
                textColor = if (reminder.status == "Hoàn thành") Color.Black else Color.Red,
                border = isSelected,
                showEdit = reminder.status != "Hoàn thành",
                onClick = {
                    selectedReminder = reminder
                    status = reminder.status
                },
                onEditClick = {
                    navController.navigate("reminder_form")
                }
            )
        }

        Spacer(Modifier.height(30.dp))

        // ---------- ACTION BUTTONS ----------
        if (status != "Hoàn thành") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Button(
                    onClick = { status = "Hoãn" },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30))
                ) {
                    Text("Hoãn lại", color = Color.White, fontSize = 16.sp)
                }

                Button(
                    onClick = { showConfirmDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
                ) {
                    Text("Hoàn thành", color = Color.Black, fontSize = 16.sp)
                }
            }
        }

        // ---------- CONFIRM DIALOG ----------
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Xác nhận hoàn thành") },
                text = { Text("Bạn xác nhận đã hoàn thành chứ?") },
                confirmButton = {
                    TextButton(onClick = {
                        status = "Hoàn thành"
                        selectedReminder = selectedReminder.copy(
                            status = "Hoàn thành",
                            color = Color(0xFFB6F2B8)
                        )
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

data class ReminderItem(
    val time: String,
    val date: String,
    val status: String,
    val color: Color
)
