package com.example.pet_health.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.pet_health.data.entity.PetEntity
import com.google.accompanist.flowlayout.FlowRow
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pet_health.data.repository.PetRepository
import com.example.pet_health.ui.viewmodel.PetViewModel
import com.example.pet_health.ui.viewmodel.PetViewModelFactory
import pet_health.data.local.AppDatabase
import java.util.concurrent.TimeUnit
import androidx.compose.foundation.lazy.items
import java.text.Normalizer




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(navController: NavController, petViewModel: PetViewModel) {
    val context = LocalContext.current


    // State từ ViewModel
    val pets by petViewModel.pets
    val isLoading by petViewModel.isLoading

    // Filter & Search
    val petTypes = listOf("Tất cả", "Chó", "Mèo", "Thỏ","Khác")
    var selectedType by remember { mutableStateOf("Tất cả") }
    var searchQuery by remember { mutableStateOf("") }

    val petsFiltered = pets.filter { pet ->
        val petNameNormalized = pet.name.removeDiacritics().lowercase()
        val petSpeciesNormalized = pet.species.removeDiacritics().lowercase()
        val searchQueryNormalized = searchQuery.removeDiacritics().lowercase()
        val selectedTypeNormalized = selectedType.removeDiacritics().lowercase()

        // Lọc theo loại: "tat ca" nghĩa là tất cả
        val typeMatch = selectedTypeNormalized == "tat ca" || petSpeciesNormalized == selectedTypeNormalized

        // Lọc theo tên
        val searchMatch = searchQueryNormalized.isEmpty() ||
                petNameNormalized.contains(searchQueryNormalized) ||
                petSpeciesNormalized.contains(searchQueryNormalized)
        typeMatch && searchMatch
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Danh sách thú cưng", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFD2FC))
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().height(60.dp).background(Color.White),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Home, contentDescription = "Trang chủ", tint = Color(0xFF6200EE), modifier = Modifier.size(32.dp))
                Icon(Icons.Default.Notifications, contentDescription = "Thông báo", tint = Color.LightGray, modifier = Modifier.size(32.dp))
                Icon(Icons.Default.Person, contentDescription = "Hồ sơ", tint = Color.LightGray, modifier = Modifier.size(32.dp))
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))))
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(12.dp))

                // Filter Chips
                FlowRow(modifier = Modifier.fillMaxWidth(), mainAxisSpacing = 10.dp, crossAxisSpacing = 10.dp) {
                    petTypes.forEach { type ->
                        PetTypeChip(text = type, selected = selectedType == type) { selectedType = type }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Box
                Box(
                    modifier = Modifier.fillMaxWidth().height(45.dp)
                        .border(1.dp, Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, color = Color.Black),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) androidx.compose.material3.Text("Tìm kiếm...", fontSize = 15.sp, color = Color.Gray)
                            innerTextField()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), contentPadding = PaddingValues(bottom = 120.dp)) {
                        items(petsFiltered) { pet ->
                            PetCard(pet = pet, navController = navController)
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        item {
                            Spacer(Modifier.height(16.dp))
                            FloatingActionButton(
                                onClick = { navController.navigate("add_pet") },
                                containerColor = Color(0xFFB2F0C0),
                                shape = CircleShape,
                                modifier = Modifier.padding(horizontal = 120.dp).size(60.dp)
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
}

fun String.removeDiacritics(): String {
    val normalized = java.text.Normalizer.normalize(this, java.text.Normalizer.Form.NFD)
    return normalized.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
}
@Composable
fun PetCard(pet: PetEntity, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5F5F5))
            .border(2.dp, Color(0xFFA69E9E), RoundedCornerShape(16.dp))
            .clickable { navController.navigate("pet_profile?petId=${pet.petId}") }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(pet.imageUrl ?: ""),
            contentDescription = pet.name,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .border(2.dp, Color(0xFFCE3CCB), CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = pet.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Loài: ${pet.species}")
            Text("Tuổi: ${calculateAge(pet.birthDate)}")
        }
    }
}

@Composable
fun PetTypeChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selected) Color(0xFFCE3CCB).copy(alpha = 0.2f) else Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCE3CCB)),
        modifier = Modifier.height(36.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 14.dp)) {
            Text(text, fontSize = 16.sp, color = Color.Black)
        }
    }
}

