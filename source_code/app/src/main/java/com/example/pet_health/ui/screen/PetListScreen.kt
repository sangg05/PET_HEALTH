package com.example.pet_health.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke // THÊM IMPORT NÀY
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pet_health.R

@Composable
fun PetListScreen(navController: NavController) {
    val petTypes = listOf("Tất cả", "Chó", "Mèo", "Thỏ", "Rùa", "Chuột")
    var selectedType by remember { mutableStateOf("Tất cả") }
    var searchQuery by remember { mutableStateOf("") }

    // Danh sách mẫu
    val pets = listOf(
        Pet("Doggy", "Poodle", 3, R.drawable.pet_dog),
        Pet("Nhum", "Anh lông ngắn", 2, R.drawable.pet_cat),
        Pet("Sú", "Thỏ tai cụp", 2, R.drawable.pet_rabbit)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // === Thanh tiêu đề ===
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
                Text(
                    text = "Danh sách thú cưng",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // === Bộ lọc loại thú ===
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                petTypes.forEach { type ->
                    PetTypeChip(
                        text = type,
                        selected = selectedType == type,
                        onClick = { selectedType = type }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // === Ô tìm kiếm ===
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .border(1.dp, Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Tìm kiếm...",
                                color = Color.Gray,
                                fontSize = 15.sp
                            )
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // === Danh sách thú cưng ===
            pets.filter {
                (selectedType == "Tất cả" || it.type == selectedType) &&
                        (searchQuery.isEmpty() || it.name.contains(searchQuery, true))
            }.forEach { pet ->
                PetCard(pet)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // === Nút thêm thú cưng ===
        FloatingActionButton(
            onClick = { navController.navigate("add_pet") },
            containerColor = Color(0xFFB2F0C0),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-120).dp) // Đưa lên trên thanh NavigationBar
                .size(65.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Thêm", tint = Color.Black, modifier = Modifier.size(30.dp))
        }

        // === Thanh điều hướng dưới cùng ===
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(navController)
        }
    }
}

// === Composable hiển thị từng thẻ thú cưng ===
@Composable
fun PetCard(pet: Pet) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, Color(0xFFCE3CCB), RoundedCornerShape(18.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = pet.imageRes),
                contentDescription = pet.name,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(pet.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                Text("Loài: ${pet.breed}", fontSize = 14.sp, color = Color.Black)
                Text("Tuổi: ${pet.age}", fontSize = 14.sp, color = Color.Black)
            }
        }
    }
}

// === Thanh điều hướng tách riêng ===
@Composable
fun BottomNavBar(navController: NavController) {
    NavigationBar(
        containerColor = Color(0xFFFFD1DC),
        tonalElevation = 6.dp,
        modifier = Modifier.height(100.dp)
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home") },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Trang chủ",
                    tint = Color.Black
                )
            },
            label = { Text("Trang chủ", fontSize = 13.sp) }
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bell),
                    contentDescription = "Nhắc lịch",
                    tint = Color.Black
                )
            },
            label = { Text("Nhắc lịch", fontSize = 13.sp) }
        )

        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_paw),
                    contentDescription = "Thú cưng",
                    tint = Color(0xFFCE3CCB)
                )
            },
            label = { Text("Thú cưng", fontSize = 13.sp) }
        )
    }
}

// === Data class lưu thông tin thú cưng ===
data class Pet(
    val name: String,
    val breed: String,
    val age: Int,
    val imageRes: Int,
    val type: String = "Tất cả"
)

// === Chip lọc loại thú — đổi tên để tránh trùng ===
@Composable
fun PetTypeChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selected) Color(0xFFCE3CCB).copy(alpha = 0.15f) else Color.White,
        border = BorderStroke(
            1.dp,
            if (selected) Color(0xFFCE3CCB) else Color.Gray.copy(alpha = 0.4f)
        ),
        modifier = Modifier.height(36.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 14.dp)) {
            Text(
                text = text,
                color = if (selected) Color(0xFFCE3CCB) else Color.Black,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
