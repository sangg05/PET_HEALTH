package com.example.pet_health.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import com.google.accompanist.flowlayout.FlowRow


@Composable
fun PetListScreen(navController: NavController) {

    val petTypes = listOf("Tất cả", "Chó", "Mèo", "Thỏ", "Rùa", "Chuột")
    var selectedType by remember { mutableStateOf("Tất cả") }
    var searchQuery by remember { mutableStateOf("") }

    val pets = listOf(
        Pet("Doggy", "Poodle", 3, R.drawable.pet_dog, "Chó"),
        Pet("Doggy", "Poodle", 3, R.drawable.pet_dog, "Chó"),
        Pet("Doggy", "Poodle", 3, R.drawable.pet_dog, "Chó"),
        Pet("Doggy", "Poodle", 3, R.drawable.pet_dog, "Chó"),

        Pet("Nhum", "Anh lông ngắn", 2, R.drawable.pet_cat, "Mèo"),
        Pet("Sú", "Thỏ tai cụp", 2, R.drawable.pet_rabbit, "Thỏ")
        // ... thêm pet khác nếu muốn
    )

    val petsFiltered = pets.filter {
        (selectedType == "Tất cả" || it.type == selectedType) &&
                (searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true))
    }
    Scaffold(
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
                    tint = Color(0xFF6200EE),
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))
                    )
                )
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Column(modifier = Modifier.fillMaxSize()) {

                // HEADER
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.Black)
                    }
                    Text(
                        "Danh sách thú cưng",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // FILTER CHIPS 2 HÀNG
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    mainAxisSpacing = 10.dp,
                    crossAxisSpacing = 10.dp
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

                // SEARCH BOX
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .border(1.dp, Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = TextStyle(fontSize = 15.sp, color = Color.Black),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Tìm kiếm...",
                                    fontSize = 15.sp,
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // LAZY COLUMN
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(petsFiltered) { pet ->
                        PetCard(pet = pet, navController = navController)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    // FAB nằm cuối LazyColumn, sẽ cuộn theo nội dung
                    item {
                        Spacer(Modifier.height(16.dp))
                        FloatingActionButton(
                            onClick = { navController.navigate("add_pet") },
                            containerColor = Color(0xFFB2F0C0),
                            shape = CircleShape,
                            modifier = Modifier
                                .padding(horizontal = 120.dp) // chỉnh vị trí
                                .size(60.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Thêm", tint = Color.Black)
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun PetCard(pet: Pet, navController: NavController) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, Color(0xFFCE3CCB), RoundedCornerShape(18.dp))
            .background(Color.White)
            .clickable {
                navController.navigate(
                    "pet_profile?name=${pet.name}&breed=${pet.breed}&age=${pet.age}&imageRes=${pet.imageRes}"
                )
            }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = painterResource(id = pet.imageRes),
                contentDescription = pet.name,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = pet.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text("Loài: ${pet.breed}")
                Text("Tuổi: ${pet.age}")
            }
        }
    }
}

data class Pet(
    val name: String,
    val breed: String,
    val age: Int,
    val imageRes: Int,
    val type: String
)

@Composable
fun PetTypeChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selected) Color(0xFFCE3CCB).copy(alpha = 0.2f) else Color.White,
        border = BorderStroke(1.dp, Color(0xFFCE3CCB)),
        modifier = Modifier.height(36.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 14.dp)) {
            Text(text, fontSize = 16.sp, color = Color.Black)
        }
    }
}
