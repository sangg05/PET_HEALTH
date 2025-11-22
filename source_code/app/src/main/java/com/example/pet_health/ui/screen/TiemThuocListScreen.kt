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

// ----------------- DATA MODEL -----------------
data class TiemThuocItem(
    val year: Int,
    val title: String,
    val petName: String,
    val date: String,
    val place: String
)

// ----------------- SCREEN -----------------
@Composable
fun TiemThuocListScreen(navController: NavController? = null) {

    // 🐶 Sau này danh sách này sẽ đến từ API Backend
    val allPets = listOf("Tất cả", "Nâu", "Mỹ Diệu", "Cọp", "Đậu", "Mỹ Lem")

    var selectedPet by remember { mutableStateOf("Tất cả") }

    val items = listOf(
        TiemThuocItem(2025, "Tiêm - FVRCP #2", "Đậu", "25/10/2025", "Bệnh viện thú y Procare"),
        TiemThuocItem(2025, "Thuốc - Amoxicilin 500mg", "Mập", "25/10/2025", "Bệnh viện thú y Procare"),
        TiemThuocItem(2024, "Tiêm - FVRCP #2", "Mập", "25/04/2024", "Bệnh viện thú y Procare"),
    )

    val grouped = items.groupBy { it.year }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(Color(0xFFFAD3F5), Color(0xFFFCE6F9))))
            .padding(16.dp)
    ) {

        // ==== Thanh tiêu đề ====
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController?.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Text(
                text = "Sổ tiêm và thuốc",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(Modifier.height(8.dp))

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
            modifier = Modifier.weight(1f)
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
        }

        Spacer(Modifier.height(10.dp))

        // ==== Nút Thêm ====
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            FloatingActionButton(
                onClick = { navController?.navigate("add_record") },
                containerColor = Color(0xFFB6F2B8),
                shape = CircleShape,
                modifier = Modifier.size(58.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm", tint = Color.Black, modifier = Modifier.size(32.dp))
            }
        }

        Spacer(Modifier.height(8.dp))

        BottomNavigationBarStyled()
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
                color = Color.White, // ✔️ luôn trắng
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
                val encodedRecordType = Uri.encode(if (item.title.startsWith("Tiêm")) "Tiêm" else "Thuốc")
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
