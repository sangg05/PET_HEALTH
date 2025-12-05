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

    val vaccineList by viewModel.vaccines.collectAsState()
    val petList by viewModel.pets  // ✅ FIX: Thêm petList

    var selectedPetId by remember { mutableStateOf("all") }

    // State cho việc xóa
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<VaccineEntity?>(null) }

    // Filter vaccine theo pet được chọn
    val filteredVaccineList = if (selectedPetId == "all") {
        vaccineList
    } else {
        vaccineList.filter { it.petId == selectedPetId }
    }

    // Group theo năm
    val grouped = filteredVaccineList.groupBy {
        SimpleDateFormat("yyyy", Locale.getDefault()).format(Date(it.date))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Sổ tiêm và thuốc",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFC0CB)
                )
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
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Trang chủ",
                    tint = Color(0xFF7B1FA2),
                    modifier = Modifier.size(32.dp)
                )
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Thông báo",
                    tint = Color.LightGray,
                    modifier = Modifier.size(32.dp)
                )
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Hồ sơ",
                    tint = Color.LightGray,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
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
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Filter Chips - Hiển thị danh sách pet để filter
                FlowRow(
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PetFilterChip(
                        text = "Tất cả",
                        selected = selectedPetId == "all",
                        onClick = { selectedPetId = "all" }
                    )
                    petList.forEach { pet ->
                        PetFilterChip(
                            text = pet.name,
                            selected = selectedPetId == pet.petId,
                            onClick = { selectedPetId = pet.petId }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Danh sách vaccine
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
                                val petName = petList.find { it.petId == item.petId }?.name ?: "Unknown"

                                TiemThuocCard(
                                    item = item,
                                    petName = petName,
                                    navController = navController,
                                    onDeleteClick = {
                                        itemToDelete = item
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Hộp thoại xác nhận xóa
            if (showDeleteDialog && itemToDelete != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Xác nhận xóa") },
                    text = { Text("Bạn có chắc chắn muốn xóa bản ghi này không?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.deleteVaccine(itemToDelete!!)
                                showDeleteDialog = false
                                itemToDelete = null
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.Red
                            )
                        ) {
                            Text("Xóa", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Hủy")
                        }
                    },
                    containerColor = Color.White
                )
            }
        }
    }
}

@Composable
fun PetFilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color(0xFF7B1FA2) else Color.Gray,
                shape = RoundedCornerShape(20.dp)
            )
            .background(Color.White, RoundedCornerShape(20.dp))
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
    petName: String,
    navController: NavController?,
    onDeleteClick: () -> Unit
) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateStr = sdf.format(Date(item.date))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable {
                // ✅ FIX: Navigate với vaccineId
                navController?.navigate("record_detail/${item.vaccineId}")
            },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFF7B1FA2)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFFF3B30)
                    )
                }
            }

            Text("Thú cưng: $petName", fontSize = 14.sp, color = Color.Gray)
            Text("Ngày: $dateStr", fontSize = 14.sp, color = Color.Gray)

            if (!item.clinic.isNullOrEmpty()) {
                Text("Cơ sở: ${item.clinic}", fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    "Tạo nhắc",
                    color = Color(0xFFFF9100),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        // TODO: Navigate to reminder with vaccineId
                        navController?.navigate("reminder_form/${item.vaccineId}")
                    }
                )
            }
        }
    }
}