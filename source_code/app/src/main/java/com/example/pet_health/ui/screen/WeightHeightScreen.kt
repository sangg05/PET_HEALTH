package com.example.pet_health.ui.screen

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .padding(horizontal = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
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
fun WeightHeightScreen() {
    var selectedTab by remember { mutableStateOf("Ngày") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val filteredData = mockData.filter { record ->
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
                title = {
                    Text(
                        "Cân nặng và chiều cao",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkPink
                )
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
                .background(lightPink)
                .padding(paddingValues)
        ) {
            // --- Chọn khoảng thời gian ---
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Chọn khoảng thời gian",
                    color = TextDark,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                DatePickerField(
                    label = "Từ ngày",
                    selectedDate = startDate,
                    onDateSelected = { startDate = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                DatePickerField(
                    label = "Đến ngày",
                    selectedDate = endDate,
                    onDateSelected = { endDate = it }
                )
            }

            // --- Bộ lọc ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterTab(
                    text = "Ngày",
                    modifier = Modifier.weight(1f)
                ) { selectedTab = "Ngày" }

                FilterTab(
                    text = "Cân nặng",
                    modifier = Modifier.weight(1f)
                ) { selectedTab = "Cân nặng" }

                FilterTab(
                    text = "Chiều Cao",
                    modifier = Modifier.weight(1f)
                ) { selectedTab = "Chiều Cao" }
            }

            // --- Danh sách dữ liệu ---
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
            ) {
                items(filteredData) { record ->
                    RecordItem(record)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeightHeightScreenPreview() {
    MaterialTheme {
        WeightHeightScreen()
    }
}
