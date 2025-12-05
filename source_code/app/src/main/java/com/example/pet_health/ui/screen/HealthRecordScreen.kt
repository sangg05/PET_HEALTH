package com.example.pet_health.ui.screens

import BottomBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pet_health.data.entity.HealthRecordEntity
import com.example.pet_health.ui.screen.lightPink
import com.example.pet_health.ui.viewmodel.HealthRecordViewModel
import com.example.pet_health.ui.viewmodel.PetViewModel
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.flow.filter
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordScreen(
    navController: NavController,
    petViewModel: PetViewModel,
    healthRecordViewModel: HealthRecordViewModel
) {

    val pets = petViewModel.pets
    val healthRecords = healthRecordViewModel.healthRecords.collectAsState()

    val petOptions = remember(pets.value) {
        listOf("Tất cả") + pets.value.map { it.name }.distinct()
    }
    val selectedPet = remember { mutableStateOf("Tất cả") }

    val filteredRecords = remember(healthRecords.value, selectedPet.value) {
        if (selectedPet.value == "Tất cả") healthRecords.value
        else healthRecords.value.filter { record ->
            pets.value.firstOrNull { it.petId == record.petId }?.name == selectedPet.value
        }
    }

    val newRecord = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<HealthRecordEntity>("new_record")

    LaunchedEffect(newRecord) {
        if (newRecord != null) {
            healthRecordViewModel.addRecord(newRecord)
            navController.currentBackStackEntry?.savedStateHandle?.remove<HealthRecordEntity>("new_record")
        }
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = lightPink)
            )
        },
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))
                    )
                )
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)

        ) {
            // Filter chip
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                HealthRecordFilterChips(
                    petOptions = petOptions,
                    selectedPet = selectedPet
                )
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
                filteredRecords.forEach { record ->
                    // Lấy tên thú cưng tương ứng với petId của record
                    val petName = pets.value.firstOrNull { it.petId == record.petId }?.name ?: "Không xác định"

                    HealthTimelineItem(
                        petName = petName,  // thêm tên thú cưng
                        date = record.date.toDate(),
                        diagnosis = record.diagnosis ?: "",
                        prescription = record.prescription ?: "",
                        onDelete = { healthRecordViewModel.deleteRecord(record.recordId) }
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

private fun Long.toDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

@Composable
fun AppFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) Color(0xFFCE3CCB).copy(alpha = 0.2f) else Color.White
    val borderColor = Color(0xFFCE3CCB)
    val textColor = if (selected) Color(0xFFCE3CCB) else Color.Black

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .padding(4.dp)
            .height(36.dp)  // giống PetTypeChip
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 14.dp)
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@Composable
fun HealthRecordFilterChips(
    petOptions: List<String>,
    selectedPet: MutableState<String>,
    maxLines: Int = 2 // Hiển thị tối đa 2 dòng trước
) {
    var expanded by remember { mutableStateOf(false) }

    val approxChipsPerLine = 4 // giả định mỗi dòng ~4 chip
    val maxVisible = maxLines * approxChipsPerLine
    val visibleChips = if (expanded) petOptions else petOptions.take(maxVisible)

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        mainAxisSpacing = 6.dp,
        crossAxisSpacing = 6.dp
    ) {
        visibleChips.forEach { pet ->
            AppFilterChip(
                text = pet,
                selected = selectedPet.value == pet,
                onClick = { selectedPet.value = pet }
            )
        }

        val buttonIcon = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
        IconButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .height(44.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFE0C4F0))
        ) {
            Icon(
                imageVector = buttonIcon,
                contentDescription = if (expanded) "Thu gọn" else "Mở rộng",
                tint = Color(0xFFCE3CCB)
            )
        }
    }
}
@Composable
fun HealthTimelineItem(
    petName: String,
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
            // Row ngày + xóa
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(petName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF6200EE))
                    Text(date, fontSize = 16.sp, color = Color.Gray)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Xóa bệnh án", tint = Color.Red)
                }
            }

            // Hàng dưới: timeline
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFCE3CCB))
                )
                Divider(
                    color = Color(0xFFCE3CCB),
                    thickness = 2.dp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Triệu chứng", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = diagnosis, fontSize = 16.sp, color = Color.Black)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Đơn thuốc", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = prescription, fontSize = 16.sp, color = Color.Black)
        }
    }
}

