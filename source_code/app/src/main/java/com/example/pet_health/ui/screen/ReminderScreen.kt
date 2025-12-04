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
        val early: String = ""
    )

    // ==================== MAIN SCREEN ====================
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ReminderScreen(navController: NavController? = null) {

        var reminderList by remember {
            mutableStateOf(
                mutableStateListOf(
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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFC0CB)) // lightPink
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

                    // ===== Tabs, Search, Filters =====
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                    color = if (selectedTab == index) Color(0xFF5D2C02) else Color.Gray,
                                    fontSize = 20.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .height(3.dp)
                                        .width(if (selectedTab == index) 50.dp else 0.dp)
                                        .background(Color(0xFF5D2C02))
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

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

                    val filters = listOf("Tất cả", "Tiêm phòng", "Tẩy giun", "Tái khám", "Thuốc", "Khác")
                    FlowRow(
                        mainAxisSpacing = 8.dp,
                        crossAxisSpacing = 8.dp
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

                    // ===== List of reminders =====
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredList, key = { it.id }) { reminder ->

                            ReminderCardStyled(
                                reminder = reminder,
                                onClick = {
                                    val encodedType = URLEncoder.encode(reminder.type, "UTF-8")
                                    val encodedTitle = URLEncoder.encode(reminder.title, "UTF-8")
                                    val encodedDate = URLEncoder.encode(reminder.date, "UTF-8")
                                    val encodedTime = URLEncoder.encode(reminder.time, "UTF-8")
                                    val encodedRepeat = URLEncoder.encode(reminder.repeat, "UTF-8")
                                    val encodedEarly = URLEncoder.encode(reminder.early, "UTF-8")

                                    navController?.navigate(
                                        "reminder_detail/${reminder.id}/$encodedType/$encodedTitle/$encodedDate/$encodedTime/$encodedRepeat/$encodedEarly"
                                    )
                                },
                                onDelete = {
                                    reminderList.remove(reminder)
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, Color(0xFF8A2BE2))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(reminder.type, color = Color(0xFF6A1B9A), fontWeight = FontWeight.Bold)
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
