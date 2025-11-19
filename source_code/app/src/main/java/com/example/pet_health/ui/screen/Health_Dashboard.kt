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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.alpha



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

    val scrollState = rememberScrollState() // Scroll state cho Column

    var showForm by remember { mutableStateOf(false) }
    var newWeight by remember { mutableStateOf("") }
    var newHeight by remember { mutableStateOf("") }

    var currentWeight by remember { mutableStateOf("5.2 kg") }
    var currentHeight by remember { mutableStateOf("35 cm") }

    var showForm2 by remember { mutableStateOf(false) }
    var newSymptomName by remember { mutableStateOf("") }
    var newSymptomDesc by remember { mutableStateOf("") }
    var newSymptomDate by remember { mutableStateOf("") }
    var newSymptomTime by remember { mutableStateOf("") }


    var symptoms by remember {
        mutableStateOf(
            listOf(
                "Nôn mửa" to "Ngày: 10/11/2025 Giờ: 08:00 Mô Tả: Nôn ra thức ăn",
                "Sốt cao" to "Ngày: 11/11/2025 Giờ: 12:00 Mô Tả: 39°C",
                "Ho nhiều" to "Ngày: 12/11/2025 Giờ: 15:00 Mô Tả: Ho khan"
            )
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sức khỏe của tên", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { /* Navigate back */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkPink)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(White),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Home, contentDescription = "Trang chủ", tint = AccentColor, modifier = Modifier.size(32.dp))
                Icon(Icons.Default.Notifications, contentDescription = "Thông báo", tint = Color.LightGray, modifier = Modifier.size(32.dp))
                Icon(Icons.Default.Person, contentDescription = "Hồ sơ", tint = Color.LightGray, modifier = Modifier.size(32.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState) // cho phép cuộn
                .background(lightPink)
                .padding(paddingValues)
                .padding(16.dp)
                .alpha(if (showForm || showForm2) 0.3f else 1f)
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
            Spacer(modifier = Modifier.height(16.dp))

            // --- Add button ---
            Button(
                onClick = { showForm = true },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .align(Alignment.CenterHorizontally),
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
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                symptoms.forEach { (name, description) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                description,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(8.dp))

            // --- Add symptom button ---
            Box(modifier = Modifier
                .fillMaxSize()
                .background(lightPink)
                .padding(16.dp) // padding tổng thể
            ) {
                // --- Add symptom button ở ---
                IconButton(
                    onClick = { showForm2 = true },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF00AA00), CircleShape)
                        .align(Alignment.Center) // căn giữa Box
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add symptom",
                        tint = Color.White
                    )
                }

                // --- Bottom navigation ở dưới ---
            }
        }
        if (showForm) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showForm = false }, // nhấn ngoài để đóng
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Cập nhật dữ liệu", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newWeight,
                            onValueChange = { newWeight = it },
                            label = { Text("Cân nặng (kg)") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newHeight,
                            onValueChange = { newHeight = it },
                            label = { Text("Chiều cao (cm)") }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(onClick = { showForm = false }) { Text("Hủy") }
                            Button(onClick = {
                                if (newWeight.isNotBlank()) currentWeight = "${newWeight.trim()} kg"
                                if (newHeight.isNotBlank()) currentHeight = "${newHeight.trim()} cm"
                                showForm = false
                            }) { Text("Lưu") }
                        }
                    }
                }
            }
        }
        if (showForm2) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showForm2 = false },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Thêm triệu chứng", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                        OutlinedTextField(
                            value = newSymptomName,
                            onValueChange = { newSymptomName = it },
                            label = { Text("Tên triệu chứng") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newSymptomTime,
                            onValueChange = { newSymptomTime = it },
                            label = { Text("Giờ (HH:mm)") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newSymptomDesc,
                            onValueChange = { newSymptomDesc = it },
                            label = { Text("Mô tả") },
                            singleLine = false,
                            maxLines = 3
                        )


                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(onClick = { showForm2 = false }) {
                                Text("Hủy")
                            }
                            Button(onClick = {
                                if (newSymptomName.isNotBlank() && newSymptomDesc.isNotBlank()) {
                                    symptoms = symptoms + (newSymptomName to newSymptomDesc)
                                    newSymptomName = ""
                                    newSymptomDesc = ""
                                    showForm2 = false
                                }
                            }) {
                                Text("Lưu")
                            }
                        }
                    }
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