package com.example.pet_health.ui.screen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowRow
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import com.example.pet_health.ui.screens.lightPink
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBar

// ----------------- DATA MODEL -----------------
data class TiemThuocItem(
    val year: Int,
    val title: String,
    val petName: String,
    val date: String,
    val place: String
)

// ----------------- SCREEN -----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiemThuocListScreen(navController: NavController? = null) {
    val allPets = listOf("Tất cả", "Nâu", "Mỹ Diệu", "Cọp", "Đậu", "Mỹ Lem")
    var selectedPet by remember { mutableStateOf("Tất cả") }

    val items = listOf(
        TiemThuocItem(2025, "Tiêm - FVRCP #2", "Đậu", "25/10/2025", "Bệnh viện thú y Procare"),
        TiemThuocItem(2025, "Thuốc - Amoxicilin 500mg", "Mập", "25/10/2025", "Bệnh viện thú y Procare"),
        TiemThuocItem(2024, "Tiêm - FVRCP #2", "Mập", "25/04/2024", "Bệnh viện thú y Procare"),
        TiemThuocItem(2024, "Tiêm - FVRCP #2", "Mập", "25/04/2024", "Bệnh viện thú y Procare"),
    )

    val grouped = items.groupBy { it.year }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Sổ tiêm và thuốc",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black)
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = lightPink
                )
            )
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
                Icon(Icons.Default.Home,
                    contentDescription = "Trang chủ",
                    tint = Color(0xFF7B1FA2),
                    modifier = Modifier.size(32.dp))
                Icon(Icons.Default.Notifications,
                    contentDescription = "Thông báo",
                    tint = Color.LightGray,
                    modifier = Modifier.size(32.dp))
                Icon(Icons.Default.Person,
                    contentDescription = "Hồ sơ",
                    tint = Color.LightGray,
                    modifier = Modifier.size(32.dp))
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
                // ==== Bộ lọc thú cưng ====
                FlowRow(
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    allPets.forEach { pet ->
                        PetFilterChip(
                            text = pet,
                            selected = pet == selectedPet,
                            onClick = { selectedPet = pet }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // ==== Danh sách chia theo năm ====
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp)
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
                            TiemThuocCard(item = item, navController = navController)
                        }
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            FloatingActionButton(
                                onClick = { navController?.navigate("add_record") },
                                containerColor = Color(0xFF00AA00)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
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
fun TiemThuocCard(item: TiemThuocItem, navController: NavController?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable {
                val encodedPetName = Uri.encode(item.petName)
                val encodedRecordType = Uri.encode(
                    if (item.title.startsWith("Tiêm")) "Tiêm" else "Thuốc"
                )
                val encodedRecordName = Uri.encode(item.title)
                val encodedDate = Uri.encode(item.date)
                val encodedPlace = Uri.encode(item.place)

                navController?.navigate(
                    "record_detail/$encodedPetName/$encodedRecordType/$encodedRecordName/$encodedDate/$encodedPlace"
                )
            },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFF7B1FA2)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text("Thú cưng: ${item.petName}")
            Text("Ngày: ${item.date}")
            Text("Cơ sở: ${item.place}")

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