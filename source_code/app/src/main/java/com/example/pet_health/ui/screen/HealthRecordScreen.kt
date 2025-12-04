package com.example.pet_health.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowRow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordScreen(navController: NavController) {

    val selectedPet = remember { mutableStateOf("Tất cả") }
    val petOptions = listOf("Tất cả", "Nâu", "Cọp", "Đậu", "Mỹ Diệu", "Mỹ Lem", "Mỹ Lem")

    // Dữ liệu bệnh án dùng state để cập nhật UI khi xóa
    val healthRecords = remember {
        mutableStateListOf(
            Triple("20/11/2025", "Ho, mệt", "Kháng sinh + Vitamin C"),
            Triple("12/10/2025", "Bỏ ăn", "Men tiêu hoá"),
            Triple("03/08/2025", "Ngứa da", "Kem bôi + thuốc dị ứng")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ sức khỏe", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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

                IconButton(onClick = { /* xử lý Profile */ }) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Hồ sơ",
                        tint = Color.LightGray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))
                    )
                )
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Filter chip
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisSpacing = 6.dp,
                crossAxisSpacing = 6.dp
            ) {
                petOptions.forEach { pet ->
                    AppFilterChip(
                        text = pet,
                        selected = selectedPet.value == pet,
                        onClick = { selectedPet.value = pet }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Hồ sơ bệnh án",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
            // Danh sách bệnh án cuộn
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                healthRecords.forEachIndexed { index, record ->
                    HealthTimelineItem(
                        date = record.first,
                        diagnosis = record.second,
                        prescription = record.third,
                        onDelete = { healthRecords.removeAt(index) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nút thêm bệnh án
                Button(
                    onClick = { navController.navigate("add_health_record") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB2F0C0)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .height(45.dp)
                        .width(180.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("Thêm bệnh án", color = Color.Black, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AppFilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selected) Color(0xFFCE3CCB).copy(alpha = 0.15f) else Color.White,
        border = BorderStroke(
            1.dp,
            if (selected) Color(0xFFCE3CCB) else Color.Gray.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .height(36.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 14.dp)
        ) {
            Text(
                text = text,
                color = if (selected) Color(0xFFCE3CCB) else Color.Black,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun HealthTimelineItem(
    date: String,
    diagnosis: String,
    prescription: String,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, Color(0xFFCE3CCB)), // Thêm viền màu tím nhạt
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = date,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF333333)
            )
            // Row ngày + xóa
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box( modifier = Modifier .size(10.dp) .clip(RoundedCornerShape(50)) .background(Color(0xFFCE3CCB)) )
                Divider( color = Color(0xFFCE3CCB), thickness = 1.dp, modifier = Modifier.weight(1f) )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Xóa bệnh án", tint = Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Triệu chứng", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = diagnosis, fontSize = 14.sp, color = Color.Black)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Đơn thuốc", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = prescription, fontSize = 14.sp, color = Color.Black)
        }
    }
}

