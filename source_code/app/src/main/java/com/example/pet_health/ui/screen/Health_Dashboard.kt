package com.example.pet_health.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthTrackingScreen() {
    val lightPink = Color(0xFFE8B4E8)
    val darkPink = Color(0xFFD896D8)

    var selectedCategory by remember { mutableStateOf("Tất cả") }
    var searchText by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    val categoriesCollapsed = listOf("Tất cả", "Nâu", "Mỹ Diệu")
    val categoriesExpanded = listOf("Cọp", "Đâu", "Mỳ Lem")
    val allCategories = if (isExpanded) categoriesCollapsed + categoriesExpanded else categoriesCollapsed

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Sức khỏe của tên",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Navigate back */ }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkPink)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(lightPink)
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            // --- Categories row ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FlowRow(
                    modifier = Modifier.weight(1f),
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    allCategories.forEach { category ->
                        CategoryChip(category, selectedCategory == category) {
                            selectedCategory = category
                        }
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle",
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Search bar ---
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Tìm kiếm...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(24.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                ),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Warning card ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Color(0xFFFFCC00),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Cảnh báo giảm cân :",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Health metrics ---
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                HealthMetricCard(
                    value = "5.2 kg",
                    label = "Cân nặng mới nhất",
                    modifier = Modifier.weight(1f)
                ) {
                    // Xử lý nhấn: ví dụ điều hướng sang màn hình cân nặng
                    println("Đi tới chi tiết cân nặng")
                }

                HealthMetricCard(
                    value = "35 cm",
                    label = "Kích thước mới nhất",
                    modifier = Modifier.weight(1f)
                ) {
                    // Xử lý nhấn: ví dụ điều hướng sang màn hình kích thước
                    println("Đi tới chi tiết kích thước")
                }
            }

            // --- Add button ---
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color(0xFF00AA00),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Đổi liệu mới",
                    color = Color(0xFF00AA00),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Symptoms section ---
            Text(
                "Nhật ký triệu chứng bất thường:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Nôn mửa",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Ngày: Giờ: Mô Tả",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- Add symptom button ---
            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF00AA00), CircleShape)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add symptom",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- Bottom navigation ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Home",
                        tint = Color(0xFF4DB8E8),
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color(0xFFFFCC00),
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color(0xFF4DB8E8),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                if (isSelected) Color(0xFFD896D8) else Color.White,
                RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color(0xFFFF69B4), CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun HealthMetricCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                label,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
@Composable
fun HealthMetricCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {} // thêm callback
) {
    Card(
        modifier = modifier
            .clickable { onClick() }, // Card có thể nhấn
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                label,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}