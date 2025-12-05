package com.example.pet_health.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.pet_health.ui.viewmodel.VaccineViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordDetailScreen(
    navController: NavController,
    vaccineId: String // Chỉ nhận ID
) {
    val context = LocalContext.current
    val viewModel = remember { VaccineViewModel(context) }

    // 1. Lấy danh sách Vaccine và Pet từ ViewModel
    val vaccines by viewModel.vaccines.collectAsState()
    val pets by viewModel.pets.collectAsState() // <--- Lấy danh sách Pet

    val record = vaccines.find { it.vaccineId == vaccineId }

    if (record == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // 2. Tìm tên thú cưng dựa trên petId
    val petName = pets.find { it.petId == record.petId }?.name ?: "Không xác định"

    // 3. Xác định loại bản ghi (Nếu có số mũi -> Tiêm, ngược lại -> Thuốc)
    val isVaccine = record.doseNumber != null

    // 4. Format ngày tháng
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val startDateStr = sdf.format(Date(record.date))
    val endDateStr = record.nextDoseDate?.let { sdf.format(Date(it)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết bản ghi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFC0CB))
            )
        },
        // Bỏ containerColor để dùng Box gradient
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFF6C2),
                            Color(0xFFFFD6EC),
                            Color(0xFFEAD6FF)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Tiêu đề loại bản ghi
                        Text(
                            text = if (isVaccine) "Tiêm phòng" else "Dùng thuốc",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        // Tên Vắc xin hoặc Tên Thuốc
                        Text(
                            text = record.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6A1B9A)
                        )

                        Divider(color = Color.LightGray)

                        // --- HIỂN THỊ TÊN THÚ CƯNG ---
                        Text("Thú cưng: $petName")

                        // --- HIỂN THỊ NGÀY THÁNG THEO LOẠI ---
                        if (isVaccine) {
                            Text("Ngày tiêm: $startDateStr")
                            if (!record.clinic.isNullOrEmpty()) {
                                Text("Cơ sở: ${record.clinic}")
                            }
                            Text("Mũi số: ${record.doseNumber}")
                        } else {
                            Text("Ngày bắt đầu: $startDateStr")
                            if (endDateStr != null) {
                                Text("Ngày kết thúc: $endDateStr")
                            }
                        }

                        if (!record.note.isNullOrEmpty()) {
                            Text("Ghi chú: ${record.note}")
                        }

                        if (!record.photoUrl.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Image(
                                painter = rememberAsyncImagePainter(record.photoUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(Color(0xFFF8F8F8), shape = RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}