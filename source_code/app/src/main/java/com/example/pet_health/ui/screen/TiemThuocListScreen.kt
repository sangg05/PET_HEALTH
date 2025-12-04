package com.example.pet_health.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pet_health.data.entity.VaccineEntity
import com.example.pet_health.ui.viewmodel.VaccineViewModel
import com.google.accompanist.flowlayout.FlowRow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiemThuocListScreen(
    navController: NavController? = null
) {
    val context = LocalContext.current
    val viewModel = remember { VaccineViewModel(context) }

    // Lấy danh sách Vaccine và Pet từ ViewModel
    val vaccineList by viewModel.vaccines.collectAsState()
    val petList by viewModel.pets.collectAsState() // <--- Lấy danh sách Pet thật

    // State cho Filter: Dùng ID để lọc chính xác
    var selectedPetId by remember { mutableStateOf("all") }

    // Logic Lọc: Nếu chọn "all" thì lấy hết, ngược lại lọc theo petId
    val filteredVaccineList = if (selectedPetId == "all") {
        vaccineList
    } else {
        vaccineList.filter { it.petId == selectedPetId }
    }

    // Nhóm danh sách đã lọc theo năm
    val grouped = filteredVaccineList.groupBy {
        val sdf = SimpleDateFormat("yyyy", Locale.getDefault())
        sdf.format(Date(it.date))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Sổ tiêm và thuốc", fontWeight = FontWeight.Bold, color = Color.Black)
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFC0CB))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController?.navigate("add_record") },
                containerColor = Color(0xFF00AA00)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color.White),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Home, contentDescription = "Trang chủ", tint = Color(0xFF7B1FA2), modifier = Modifier.size(32.dp))
                Icon(Icons.Default.Notifications, contentDescription = "Thông báo", tint = Color.LightGray, modifier = Modifier.size(32.dp))
                Icon(Icons.Default.Person, contentDescription = "Hồ sơ", tint = Color.LightGray, modifier = Modifier.size(32.dp))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))
                    )
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                // ==== Bộ lọc thú cưng (ĐỘNG) ====
                FlowRow(
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Chip cố định "Tất cả"
                    PetFilterChip(
                        text = "Tất cả",
                        selected = selectedPetId == "all",
                        onClick = { selectedPetId = "all" }
                    )

                    // Render Chip cho từng thú cưng có trong danh sách
                    petList.forEach { pet ->
                        PetFilterChip(
                            text = pet.name,
                            selected = selectedPetId == pet.petId,
                            onClick = { selectedPetId = pet.petId }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // ==== Danh sách bản ghi ====
                if (filteredVaccineList.isEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Chưa có bản ghi nào", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        grouped.forEach { (year, list) ->
                            item {
                                Text(
                                    "Năm $year",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                            items(list) { item ->
                                // Tìm tên thú cưng tương ứng với ID bản ghi để hiển thị
                                val petName = petList.find { it.petId == item.petId }?.name ?: "Unknown"

                                TiemThuocCard(
                                    item = item,
                                    petName = petName, // Truyền tên thú cưng vào
                                    navController = navController
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------- COMPONENTS -----------------

@Composable
fun PetFilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color(0xFF7B1FA2) else Color.Gray,
                shape = RoundedCornerShape(20.dp)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text,
            fontSize = 14.sp,
            color = if (selected) Color(0xFF7B1FA2) else Color.Gray,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun TiemThuocCard(
    item: VaccineEntity,
    petName: String, // <--- Thêm tham số này
    navController: NavController?
) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateStr = sdf.format(Date(item.date))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable {
                navController?.navigate("record_detail/${item.vaccineId}")
            },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFF7B1FA2)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            // Hiển thị tên thú cưng
            Text("Thú cưng: $petName")

            Text("Ngày: $dateStr")
            if (!item.clinic.isNullOrEmpty()) {
                Text("Cơ sở: ${item.clinic}")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    "Tạo nhắc",
                    color = Color(0xFFFF9100),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        navController?.navigate("reminder_form")
                    }
                )
            }
        }
    }
}