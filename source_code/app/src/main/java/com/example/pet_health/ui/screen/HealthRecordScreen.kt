package com.example.pet_health.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.get
import com.example.pet_health.data.HealthRecordDataStore
import kotlinx.coroutines.launch

@Composable
fun HealthRecordScreen(navController: NavController) {
    val context = LocalContext.current
    val dataStore = remember { HealthRecordDataStore(context) }
    val scope = rememberCoroutineScope()

    val selectedPet = remember { mutableStateOf("Tất cả") }
    val petOptions = listOf("Tất cả", "Nâu", "Cọp", "Đậu", "Mỹ Diệu", "Mỹ Lem")

    // Danh sách bệnh án
    var healthRecords by remember { mutableStateOf(listOf<Triple<String, String, String>>()) }

    // Đọc dữ liệu từ DataStore
    LaunchedEffect(Unit) {
        dataStore.getRecords.collect { stored ->
            healthRecords = stored.map { record ->
                val parts = record.split("|")
                Triple(parts.getOrNull(0) ?: "", parts.getOrNull(1) ?: "", parts.getOrNull(2) ?: "")
            }
        }
    }

    // Nhận dữ liệu mới từ AddHealthRecordScreen
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val newRecord = navBackStackEntry?.savedStateHandle?.get<Map<String, String>>("new_record")

    LaunchedEffect(newRecord) {
        newRecord?.let {
            val triple = Triple(it["date"] ?: "", it["symptoms"] ?: "", it["prescription"] ?: "")
            healthRecords = healthRecords + triple

            // Lưu vào DataStore
            scope.launch {
                dataStore.saveRecord("${triple.first}|${triple.second}|${triple.third}")
            }

            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<Map<String, String>>("new_record")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // === Thanh tiêu đề ===
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
                Text(
                    text = "Hồ sơ sức khỏe",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 8.dp),
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // === Bộ lọc thú cưng ===
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                petOptions.forEach { pet ->
                    AppFilterChip( // đổi tên hàm để không trùng Material3
                        text = pet,
                        selected = selectedPet.value == pet,
                        onClick = { selectedPet.value = pet }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // === Khung hồ sơ bệnh án ===
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFD8D8D8), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Hồ sơ bệnh án",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Hiển thị danh sách bệnh án động
                    healthRecords.forEach { record ->
                        HealthTimelineItem(record.first, record.second, record.third)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { navController.navigate("add_health_record") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB2F0C0)),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .height(45.dp)
                            .width(180.dp)
                    ) {
                        Text("Thêm bệnh án", color = Color.Black, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// Đổi tên hàm để tránh trùng FilterChip của Material3
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
fun HealthTimelineItem(date: String, diagnosis: String, prescription: String) {
    Column(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth()
    ) {
        Divider(thickness = 1.dp, color = Color(0xFFD8D8D8))
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = date, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF333333))
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = diagnosis, fontSize = 14.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "Đơn thuốc", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(text = prescription, fontSize = 14.sp, color = Color.Black)
    }
}
