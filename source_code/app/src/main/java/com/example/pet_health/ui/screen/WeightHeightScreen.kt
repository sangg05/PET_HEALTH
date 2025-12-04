package com.example.pet_health.ui.screen

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pet_health.ui.screens.lightPink
import java.text.SimpleDateFormat
import java.util.*


data class WeightHeightRecord(
    val date: String, // dd/MM/yyyy
    val weight: String,
    val height: String
)

val mockData = listOf(
    WeightHeightRecord("01/01/2024", "2.5kg", "35cm"),
    WeightHeightRecord("02/01/2024", "2.6kg", "36cm"),
    WeightHeightRecord("03/01/2024", "2.7kg", "36cm"),
    WeightHeightRecord("04/01/2024", "2.8kg", "37cm"),
    WeightHeightRecord("05/01/2024", "2.9kg", "38cm"),
    WeightHeightRecord("06/01/2024", "3.0kg", "38cm")
)

val PrimaryBackground = Color(0xFFF4ECF7)
val AccentColor = Color(0xFF9B59B6)
val TextDark = Color(0xFF333333)
val White = Color.White
val lightPink = Color(0xFFE8B4E8)
val darkPink = Color(0xFFD896D8)

@Composable
fun FilterTab(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, Color.Black.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFF7A5FA7),
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp
        )
    }
}


@Composable
fun RecordItem(record: WeightHeightRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                record.date,
                color = TextDark,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            Text(
                record.weight,
                color = TextDark,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            Text(
                record.height,
                color = TextDark,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    if (selectedDate.isNotEmpty()) {
        val parts = selectedDate.split("/")
        calendar.set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            onDateSelected(String.format("%02d/%02d/%04d", day, month + 1, year))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(White)
            .clickable { datePickerDialog.show() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (selectedDate.isEmpty()) label else selectedDate,
                color = if (selectedDate.isEmpty()) Color.Gray else TextDark
            )
            Icon(Icons.Default.CalendarToday, contentDescription = "Chọn ngày", tint = AccentColor)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightHeightScreen(navController: NavController, petId: String?, onAddRecord: () -> Unit = {}) {
    var selectedTab by remember { mutableStateOf("Ngày") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Dùng mutableStateListOf để có thể xóa
    val records = remember { mutableStateListOf(*mockData.toTypedArray()) }

    // Lọc dữ liệu
    val filteredData = records.filter { record ->
        val recordDate = sdf.parse(record.date) ?: return@filter false
        val start = if (startDate.isNotEmpty()) sdf.parse(startDate) else null
        val end = if (endDate.isNotEmpty()) sdf.parse(endDate) else null

        when {
            start != null && end != null -> !recordDate.before(start) && !recordDate.after(end)
            start != null -> !recordDate.before(start)
            end != null -> !recordDate.after(end)
            else -> true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cân nặng và chiều cao", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = lightPink)
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
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))
                    )
                )
        ) {
            // --- Khoảng thời gian ---
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Chọn khoảng thời gian", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                DatePickerField("Từ ngày", startDate) { startDate = it }
                Spacer(modifier = Modifier.height(8.dp))
                DatePickerField("Đến ngày", endDate) { endDate = it }
            }

            // --- Bộ lọc ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterTab("Ngày", modifier = Modifier.weight(1f)) { selectedTab = "Ngày" }
                FilterTab("Cân nặng", modifier = Modifier.weight(1f)) { selectedTab = "Cân nặng" }
                FilterTab("Chiều cao", modifier = Modifier.weight(1f)) { selectedTab = "Chiều Cao" }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- Danh sách bản ghi với nút xóa ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(horizontal = 16.dp)) {
                filteredData.forEach { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF6FF)), // pastel nhẹ
                        elevation = CardDefaults.cardElevation(4.dp),
                        border = BorderStroke(1.dp, Color(0xFFCE3CCB).copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(record.date, fontWeight = FontWeight.Bold, color = TextDark)
                            }
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(record.weight, fontWeight = FontWeight.Bold, color = TextDark)
                            }
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(record.height, fontWeight = FontWeight.Bold, color = TextDark)
                            }
                            IconButton(
                                onClick = { records.remove(record) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color(0xFFE74C3C))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun WeightHeightScreenPreview() {
    val navController = rememberNavController() // tạo NavController giả
    MaterialTheme {
        WeightHeightScreen(
            navController = navController,
            petId = "p1"
        )
    }
}
