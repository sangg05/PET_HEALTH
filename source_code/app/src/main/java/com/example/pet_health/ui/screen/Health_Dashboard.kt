package com.example.pet_health.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowRow
import com.example.pet_health.data.entity.PetEntity
import com.example.pet_health.ui.viewmodel.PetViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthTrackingScreen(viewModel: PetViewModel ,navController: NavController) {
    val lightPink = Color(0xFFE8B4E8)
    val darkPink = Color(0xFFD896D8)

    val pets by viewModel.pets
    val isExpanded by viewModel.isExpanded
//    var selectedPet by remember { mutableStateOf<PetEntity?>(pets.firstOrNull()) }

    val scrollState = rememberScrollState()

    // Form nhập liệu cân nặng/chiều cao
    var showForm by remember { mutableStateOf(false) }
    var newWeight by remember { mutableStateOf("") }
    var newHeight by remember { mutableStateOf("") }

    // Form triệu chứng
    var showForm2 by remember { mutableStateOf(false) }
    var newSymptomName by remember { mutableStateOf("") }
    var newSymptomDesc by remember { mutableStateOf("") }

    val symptoms = viewModel.getSymptomsOfSelectedPet()
    val selectedPet by viewModel.selectedPet


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sức khỏe thú cưng", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { /* back */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkPink)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color.White),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Home, contentDescription = "Trang chủ", tint = Color(0xFF6200EE), modifier = Modifier.size(32.dp))
                Icon(Icons.Default.Notifications, contentDescription = "Thông báo", tint = Color.LightGray, modifier = Modifier.size(32.dp))
                Icon(Icons.Default.Person, contentDescription = "Hồ sơ", tint = Color.LightGray, modifier = Modifier.size(32.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(lightPink)
                .padding(paddingValues)
                .padding(16.dp)
                .alpha(if (showForm || showForm2) 0.3f else 1f)
        ) {
            // --- Pets FlowRow ---
            val chipsPerRow = 3
            val allChips = listOf(null) + pets
            val displayedChips = if (isExpanded) allChips else allChips.take(6)
            val chipRows = displayedChips.chunked(chipsPerRow)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp) // khoảng cách giữa các dòng chip
            ) {
                // Hàng đầu tiên + nút
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween, // chip về trái, nút về phải
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        chipRows.firstOrNull()?.forEach { pet ->
                            if (pet == null) {
                                CategoryChip(
                                    text = "Tất cả",
                                    isSelected = selectedPet == null
                                ) { viewModel.selectPet(null) }
                            } else {
                                CategoryChip(
                                    text = pet.name,
                                    isSelected = selectedPet == pet
                                ) { viewModel.selectPet(pet) }
                            }
                        }
                    }

                    if (allChips.size > 6) {
                        IconButton(onClick = { viewModel.toggleExpand() }) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Toggle"
                            )
                        }
                    }
                }

                // Các dòng chip còn lại
                chipRows.drop(1).forEach { rowChips ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowChips.forEach { pet ->
                            if (pet == null) {
                                CategoryChip(
                                    text = "Tất cả",
                                    isSelected = selectedPet == null
                                ) {  viewModel.selectPet(null) }
                            } else {
                                CategoryChip(
                                    text = pet.name,
                                    isSelected = selectedPet == pet
                                ) {  viewModel.selectPet(pet)  }
                            }
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            // --- Health metrics ---
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                selectedPet?.let { pet ->
                    HealthMetricCard("${pet.weightKg} kg", "Cân nặng", Modifier.weight(1f)){
                        navController.navigate("weight_height/${pet.petId}")
                    }
                    HealthMetricCard("${pet.sizeCm ?: 0} cm", "Chiều cao", Modifier.weight(1f)){
                        navController.navigate("weight_height/${pet.petId}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Add pet metric button ---
            Button(
                onClick = { showForm = true },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color(0xFF00AA00))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cập nhật", color = Color(0xFF00AA00), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Symptoms section ---
            Text(
                text = "Nhật ký triệu chứng:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                symptoms.forEach { symptom ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = symptom.name,        // từ SymptomLogEntity
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = symptom.description, // từ SymptomLogEntity
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Add symptom button ---
            // Nút thêm triệu chứng
            Box(
                modifier = Modifier
                    .fillMaxWidth()           // chỉ chiếm chiều ngang
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { showForm2 = true }, // khi nhấn sẽ bật form
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF00AA00), CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add symptom", tint = Color.White)
                }
            }
        }

        // --- Form thêm metric ---
        if (showForm) {
            MetricForm(
                newWeight, onWeightChange = { newWeight = it },
                newHeight, onHeightChange = { newHeight = it },
                onClose = { showForm = false },
                onSave = {
                    selectedPet?.let { pet ->
                        val updatedPet = pet.copy(
                            weightKg = newWeight.toFloatOrNull() ?: pet.weightKg,
                            sizeCm = newHeight.toFloatOrNull() ?: pet.sizeCm
                        )
                        viewModel.updatePet(updatedPet)
                        showForm = false
                    }
                }
            )
        }

//         --- Form thêm triệu chứng ---
        if (showForm2) {
            SymptomForm(
                name = newSymptomName,
                onNameChange = { newSymptomName = it },
                desc = newSymptomDesc,
                onDescChange = { newSymptomDesc = it },
                onClose = { showForm2 = false },
                onSave = {
                    if (newSymptomName.isNotBlank() && newSymptomDesc.isNotBlank()) {
                        viewModel.addSymptomForSelectedPet(newSymptomName, newSymptomDesc)
                        newSymptomName = ""
                        newSymptomDesc = ""
                        showForm2 = false
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(if (isSelected) Color(0xFFD896D8) else Color.White, RoundedCornerShape(24.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(12.dp).background(Color(0xFFFF69B4), CircleShape))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun HealthMetricCard(value: String, label: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 12.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
fun MetricForm(
    weight: String, onWeightChange: (String) -> Unit,
    height: String, onHeightChange: (String) -> Unit,
    onClose: () -> Unit,
    onSave: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(16.dp)
        ) {
            Column(modifier = Modifier.background(Color.White).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Cập nhật dữ liệu", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = weight, onValueChange = onWeightChange, label = { Text("Cân nặng (kg)") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = height, onValueChange = onHeightChange, label = { Text("Chiều cao (cm)") })
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = onClose) { Text("Hủy") }
                    Button(onClick = onSave) { Text("Lưu") }
                }
            }
        }
    }
}

@Composable
fun SymptomForm(
    name: String, onNameChange: (String) -> Unit,
    desc: String, onDescChange: (String) -> Unit,
    onClose: () -> Unit,
    onSave: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(16.dp)
        ) {
            Column(modifier = Modifier.background(Color.White).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Thêm triệu chứng", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Tên triệu chứng") }, singleLine = true)
                OutlinedTextField(value = desc, onValueChange = onDescChange, label = { Text("Mô tả") }, maxLines = 3)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = onClose) { Text("Hủy") }
                    Button(onClick = onSave) { Text("Lưu") }
                }
            }
        }
    }
}
