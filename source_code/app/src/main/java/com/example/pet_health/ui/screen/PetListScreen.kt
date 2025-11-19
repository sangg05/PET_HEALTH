package com.example.pet_health.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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

@Composable
fun PetListScreen(navController: NavController) {

    val petTypes = listOf("Tất cả", "Chó", "Mèo", "Thỏ", "Rùa", "Chuột")
    var selectedType by remember { mutableStateOf("Tất cả") }
    var searchQuery by remember { mutableStateOf("") }

    // Data mẫu
    val pets = listOf(
        Pet("Doggy", "Poodle", 3, R.drawable.pet_dog, "Chó"),
        Pet("Nhum", "Anh lông ngắn", 2, R.drawable.pet_cat, "Mèo"),
        Pet("Sú", "Thỏ tai cụp", 2, R.drawable.pet_rabbit, "Thỏ")
    )

    val petsFiltered = pets.filter {
        (selectedType == "Tất cả" || it.type == selectedType) &&
                (searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true))
    }

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
        Column {

            // TITLE
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.Black)
                }
                Text("Danh sách thú cưng", 20.sp, FontWeight.Bold, Color.Black)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // FILTER CHIPS
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
                    textStyle = TextStyle(fontSize = 15.sp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ============================
            // LAZY COLUMN — FIX APP CRASH
            // ============================
            LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(petsFiltered) { pet ->
                    PetCard(pet, navController)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("add_pet") },
            containerColor = Color(0xFFB2F0C0),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-100).dp)
                .size(65.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Thêm", tint = Color.Black)
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
                Text(pet.name, FontWeight.Bold, 18.sp)
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
            Text(text, fontSize = 14.sp, color = Color.Black)
        }
    }
}
