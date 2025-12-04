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

    // Lấy list để tìm kiếm (vì ViewModel dùng StateFlow)
    val vaccines by viewModel.vaccines.collectAsState()
    val record = vaccines.find { it.vaccineId == vaccineId }

    if (record == null) {
        // Xử lý khi loading hoặc không tìm thấy (ví dụ show loading)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return // Dừng render phần dưới
    }

    // Format ngày
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateStr = sdf.format(Date(record.date))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết bản ghi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color.Black)
                    }
                },
                // Cập nhật màu TopBar chuẩn (Light Pink)
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFC0CB))
            )
        }
        // Bỏ containerColor ở đây vì ta sẽ dùng Box gradient bên trong
    ) { padding ->

        // Thêm Box chứa nền Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding) // Padding từ Scaffold xuống
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFF6C2), // Vàng nhạt
                            Color(0xFFFFD6EC), // Hồng nhạt
                            Color(0xFFEAD6FF)  // Tím nhạt
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
                        Text(
                            text = if (record.doseNumber != null) "Tiêm phòng" else "Dùng thuốc",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = record.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6A1B9A)
                        )

                        Divider(color = Color.LightGray)

                        Text("Thú cưng ID: ${record.petId}") // Cần map ID sang tên
                        Text("Ngày: $dateStr")

                        if (!record.clinic.isNullOrEmpty()) {
                            Text("Cơ sở: ${record.clinic}")
                        }

                        if (record.doseNumber != null) {
                            Text("Mũi số: ${record.doseNumber}")
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