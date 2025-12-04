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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    navController: NavController? = null,
    viewModel: ReminderViewModel
) {
    val reminderList by viewModel.reminders

    // 1. State cho Tabs
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Sắp tới", "Quá hạn", "Đã xong")

    // State cho bộ lọc và tìm kiếm cũ
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    var searchText by remember { mutableStateOf("") }

    val defaultTypes = listOf("Tiêm phòng", "Tẩy giun", "Tái khám", "Thuốc")
    val filterOptions = listOf("Tất cả") + defaultTypes + "Khác"

    // 2. Logic lọc danh sách (QUAN TRỌNG)
    val filteredList = remember(reminderList, selectedTab, selectedFilter, searchText) {
        // Lấy ngày hiện tại (đặt giờ về 00:00:00 để so sánh chính xác theo ngày)
        val today = getStartOfDay(Date())

        reminderList.filter { reminder ->
            // --- A. Lọc theo Tab (Logic thời gian) ---
            val reminderDate = parseDate(reminder.date) ?: Date() // Parse ngày từ String

            val isTabMatch = when (selectedTab) {
                0 -> { // Sắp tới: Chưa xong VÀ (Ngày >= Hôm nay)
                    reminder.status != "Hoàn thành" && (reminderDate.after(today) || isSameDay(reminderDate, today))
                }
                1 -> { // Quá hạn: Chưa xong VÀ (Ngày < Hôm nay)
                    reminder.status != "Hoàn thành" && reminderDate.before(today)
                }
                2 -> { // Đã xong: Chỉ cần trạng thái là Hoàn thành (bất kể ngày)
                    reminder.status == "Hoàn thành"
                }
                else -> true
            }

            // --- B. Lọc theo Loại (Chip) ---
            val isTypeMatch = when (selectedFilter) {
                "Tất cả" -> true
                "Khác" -> reminder.type !in defaultTypes
                else -> reminder.type == selectedFilter
            }

            // --- C. Lọc theo Tìm kiếm ---
            val isSearchMatch = searchText.isBlank() || reminder.title.contains(searchText, ignoreCase = true)

            // Kết hợp cả 3 điều kiện
            isTabMatch && isTypeMatch && isSearchMatch
        }.sortedBy {
            // Sắp xếp: Nếu tab là Sắp tới -> Ngày gần nhất lên đầu. Các tab khác -> Ngày mới nhất lên đầu
            parseDate(it.date)?.time
        }.let { list ->
            if (selectedTab == 0) list else list.reversed()
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text("Nhắc lịch", fontWeight = FontWeight.Bold, color = Color.Black)
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController?.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFC0CB))
                )

                // 3. Giao diện Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFFFFC0CB),
                    contentColor = Color(0xFF6A1B9A),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF6A1B9A) // Màu thanh gạch dưới
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            }
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Thanh Bottom Bar cũ
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

                // ===== Filter Chips =====
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
                if (filteredList.isEmpty()) {
                    // Hiển thị thông báo nếu danh sách trống
                    Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Không có lịch nhắc nào",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                } else {
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


// ==================== HELPER FUNCTIONS (XỬ LÝ NGÀY THÁNG) ====================
fun parseDate(dateStr: String): Date? {
    return try {
        // Định dạng ngày phải khớp với lúc bạn lưu (dd/MM/yyyy)
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateStr)
    } catch (e: Exception) {
        null
    }
}

fun getStartOfDay(date: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun isSameDay(date1: Date, date2: Date): Boolean {
    val fmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return fmt.format(date1) == fmt.format(date2)
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

    // Hiển thị nhãn trạng thái khác nhau tùy Tab (để người dùng dễ hiểu)
    // Nếu chưa xong mà ngày < hôm nay => Hiển thị "Quá hạn" màu đỏ
    val today = getStartOfDay(Date())
    val rDate = parseDate(reminder.date)
    val isOverdue = reminder.status != "Hoàn thành" && rDate != null && rDate.before(today)

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

                // Logic hiển thị text trạng thái góc phải
                if (reminder.status == "Hoàn thành") {
                    Text("Đã xong", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                } else if (isOverdue) {
                    Text("Quá hạn", fontSize = 12.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                } else if (reminder.status == "Hoãn lại") {
                    Text("Hoãn lại", fontSize = 12.sp, color = Color.Red)
                } else {
                    Text("Sắp tới", fontSize = 12.sp, color = Color.Gray)
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