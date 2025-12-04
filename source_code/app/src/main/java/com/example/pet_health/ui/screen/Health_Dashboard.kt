//package com.example.pet_health.ui.screen
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.google.accompanist.flowlayout.FlowRow
//import com.example.pet_health.data.entity.PetEntity
////import com.example.pet_health.ui.viewmodel.PetViewModel
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.TextFieldDefaults
//import com.example.pet_health.ui.screens.lightPink
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HealthTrackingScreen(viewModel: PetViewModel ,navController: NavController) {
//    val lightPinkGradient = Brush.verticalGradient(
//        colors = listOf(
//            Color(0xFFFFF6C2),
//            Color(0xFFFFD6EC),
//            Color(0xFFEAD6FF)
//        )
//    )
//    val darkPink = Color(0xFFD896D8)
//    val pets by viewModel.pets
//    val isExpanded by viewModel.isExpanded
//    val scrollState = rememberScrollState()
//
//    // Form nhập liệu cân nặng/chiều cao
//    var showForm by remember { mutableStateOf(false) }
//    var newWeight by remember { mutableStateOf("") }
//    var newHeight by remember { mutableStateOf("") }
//
//    // Form triệu chứng
//    var showForm2 by remember { mutableStateOf(false) }
//    var newSymptomName by remember { mutableStateOf("") }
//    var newSymptomDesc by remember { mutableStateOf("") }
//
//    val symptoms = viewModel.getSymptomsOfSelectedPet()
//    val selectedPet by viewModel.selectedPet
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Sức khỏe thú cưng", fontWeight = FontWeight.Bold, color = Color.Black) },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = lightPink)
//            )
//        },
//        bottomBar = {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(60.dp)
//                    .background(Color.White),
//                horizontalArrangement = Arrangement.SpaceAround,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(Icons.Default.Home, contentDescription = "Trang chủ", tint = Color(0xFF6200EE), modifier = Modifier.size(32.dp))
//                Icon(Icons.Default.Notifications, contentDescription = "Thông báo", tint = Color.LightGray, modifier = Modifier.size(32.dp))
//                Icon(Icons.Default.Person, contentDescription = "Hồ sơ", tint = Color.LightGray, modifier = Modifier.size(32.dp))
//            }
//        }
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(
//                    Brush.verticalGradient(
//                        colors = listOf(
//                            Color(0xFFFFF6C2),
//                            Color(0xFFFFD6EC),
//                            Color(0xFFEAD6FF)
//                        )
//                    )
//                )
//                .padding(paddingValues)
//        ) {
//            // Nội dung scrollable
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .verticalScroll(scrollState)
//                    .padding(16.dp)
//                    .alpha(if (showForm || showForm2) 0.3f else 1f)
//            ) {
//                // --- Pets FlowRow ---
//                val chipsPerRow = 3
//                val allChips = listOf(null) + pets
//                val displayedChips = if (isExpanded) allChips else allChips.take(6)
//                val chipRows = displayedChips.chunked(chipsPerRow)
//
//                Column(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalArrangement = Arrangement.spacedBy(10.dp)
//                ) {
//                    // Hàng đầu tiên + nút
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                            chipRows.firstOrNull()?.forEach { pet ->
//                                if (pet == null) {
//                                    CategoryChip(
//                                        text = "Tất cả",
//                                        isSelected = selectedPet == null
//                                    ) { viewModel.selectPet(null) }
//                                } else {
//                                    CategoryChip(
//                                        text = pet.name,
//                                        isSelected = selectedPet == pet
//                                    ) { viewModel.selectPet(pet) }
//                                }
//                            }
//                        }
//
//                        if (allChips.size > 6) {
//                            IconButton(onClick = { viewModel.toggleExpand() }) {
//                                Icon(
//                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
//                                    contentDescription = "Toggle"
//                                )
//                            }
//                        }
//                    }
//
//                    // Các dòng chip còn lại
//                    chipRows.drop(1).forEach { rowChips ->
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            rowChips.forEach { pet ->
//                                if (pet == null) {
//                                    CategoryChip(
//                                        text = "Tất cả",
//                                        isSelected = selectedPet == null
//                                    ) { viewModel.selectPet(null) }
//                                } else {
//                                    CategoryChip(
//                                        text = pet.name,
//                                        isSelected = selectedPet == pet
//                                    ) { viewModel.selectPet(pet) }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // --- Health metrics ---
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(16.dp),
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    selectedPet?.let { pet ->
//                        HealthMetricCard("${pet.weightKg} kg", "Cân nặng", Modifier.weight(1f)) {
//                            navController.navigate("weight_height/${pet.petId}")
//                        }
//                        HealthMetricCard(
//                            "${pet.sizeCm ?: 0} cm",
//                            "Chiều cao",
//                            Modifier.weight(1f)
//                        ) {
//                            navController.navigate("weight_height/${pet.petId}")
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // --- Add pet metric button ---
//                Button(
//                    onClick = { showForm = true },
//                    modifier = Modifier
//                        .fillMaxWidth(0.5f)
//                        .align(Alignment.CenterHorizontally),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
//                    shape = RoundedCornerShape(16.dp)
//                ) {
//                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color(0xFF00AA00))
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text("Cập nhật", color = Color(0xFF00AA00), fontWeight = FontWeight.Bold)
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // --- Symptoms section ---
//                Text("Nhật ký triệu chứng:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                    symptoms.forEach { symptom ->
//                        Card(
//                            modifier = Modifier.fillMaxWidth(),
//                            colors = CardDefaults.cardColors(containerColor = Color.White),
//                            shape = RoundedCornerShape(16.dp)
//                        ) {
//                            Column(modifier = Modifier.padding(16.dp)) {
//                                Row(
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    horizontalArrangement = Arrangement.SpaceBetween,
//                                    modifier = Modifier.fillMaxWidth()
//                                ) {
//                                    Column {
//                                        Text(symptom.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
//                                        Spacer(modifier = Modifier.height(4.dp))
//                                        Text(symptom.description, fontSize = 12.sp, color = Color.Gray)
//                                    }
//                                    IconButton(
//                                        onClick = {  }, // Gọi hàm xóa trong ViewModel
//                                        modifier = Modifier.size(24.dp)
//                                    ) {
//                                        Icon(
//                                            imageVector = Icons.Default.Delete,
//                                            contentDescription = "Xóa triệu chứng",
//                                            tint = Color.Red
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//                // --- Add symptom button ---
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 16.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    IconButton(
//                        onClick = { showForm2 = true },
//                        modifier = Modifier
//                            .size(48.dp)
//                            .background(Color(0xFF00AA00), CircleShape)
//                    ) {
//                        Icon(
//                            Icons.Default.Add,
//                            contentDescription = "Add symptom",
//                            tint = Color.White
//                        )
//                    }
//                }
//            }
//
//            // --- Overlay Form: MetricForm ---
//            if (showForm) {
//                MetricForm(
//                    weight = newWeight,
//                    onWeightChange = { newWeight = it },
//                    height = newHeight,
//                    onHeightChange = { newHeight = it },
//                    onClose = { showForm = false },
//                    onSave = {
//                        selectedPet?.let { pet ->
//                            val updatedPet = pet.copy(
//                                weightKg = newWeight.toFloatOrNull() ?: pet.weightKg,
//                                sizeCm = newHeight.toFloatOrNull() ?: pet.sizeCm
//                            )
//                            viewModel.updatePet(updatedPet)
//                            showForm = false
//                        }
//                    }
//                )
//            }
//
//            // --- Overlay Form: SymptomForm ---
//            if (showForm2) {
//                SymptomForm(
//                    name = newSymptomName,
//                    onNameChange = { newSymptomName = it },
//                    desc = newSymptomDesc,
//                    onDescChange = { newSymptomDesc = it },
//                    onClose = { showForm2 = false },
//                    onSave = {
//                        if (newSymptomName.isNotBlank() && newSymptomDesc.isNotBlank()) {
//                            viewModel.addSymptomForSelectedPet(newSymptomName, newSymptomDesc)
//                            newSymptomName = ""
//                            newSymptomDesc = ""
//                            showForm2 = false
//                        }
//                    }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun CategoryChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
//    Box(
//        modifier = Modifier
//            .background(
//                if (isSelected) Color(0xFFD896D8) else Color.White,
//                RoundedCornerShape(24.dp)
//            )
//            .border(1.dp, Color.Black, RoundedCornerShape(24.dp))
//            .padding(horizontal = 16.dp, vertical = 8.dp)
//            .clickable { onClick() }
//    ) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Box(
//                modifier = Modifier.size(12.dp)
//                    .background(Color(0xFFFF69B4), CircleShape)
//            )
//            Spacer(modifier = Modifier.width(8.dp))
//            Text(text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
//        }
//    }
//}
//
//@Composable
//fun HealthMetricCard(
//    value: String,
//    label: String,
//    modifier: Modifier = Modifier,
//    onClick: () -> Unit = {}
//) {
//    Card(
//        modifier = modifier
//            .clickable { onClick() }
//            .border(
//                width = 2.dp,
//                brush = Brush.horizontalGradient(
//                    colors = listOf(
//                        Color(0xFFFF60F8),
//                        Color(0xFFFFF6F9)
//                    )
//                ),
//                shape = RoundedCornerShape(16.dp)
//            ),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        shape = RoundedCornerShape(16.dp)
//    ) {
//        Column(
//            modifier = Modifier.fillMaxWidth().padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold,   color = Color.Black)
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(
//                label,
//                fontSize = 14.sp,
//                color = Color.Black,
//                textAlign = androidx.compose.ui.text.style.TextAlign.Center
//            )
//        }
//    }
//}
//
//@Composable
//fun MetricForm(
//    weight: String,
//    onWeightChange: (String) -> Unit,
//    height: String,
//    onHeightChange: (String) -> Unit,
//    onClose: () -> Unit,
//    onSave: () -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black.copy(alpha = 0.5f))
//            .clickable { onClose() },
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth(0.8f)
//                .background(Color.White, RoundedCornerShape(16.dp))
//                .padding(20.dp)
//                .clickable(enabled = false) { /* Ngăn click đóng form */ }
//        ) {
//            Text("Cập nhật dữ liệu", fontWeight = FontWeight.Bold, fontSize = 18.sp)
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Weight Field - Viền gradient đẹp
//            OutlinedTextField(
//                value = weight,
//                onValueChange = onWeightChange,
//                label = { Text("Cân nặng (kg)") },
//                singleLine = true,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .border(
//                        width = 2.dp,
//                        brush = Brush.horizontalGradient(
//                            colors = listOf(
//                                Color(0xFFD896D8),
//                                Color(0xFFFF69B4)
//                            )
//                        ),
//                        shape = RoundedCornerShape(12.dp)
//                    ),
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = Color.Transparent,
//                    unfocusedBorderColor = Color.Transparent
//                ),
//                shape = RoundedCornerShape(12.dp)
//            )
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Height Field - Viền gradient đẹp
//            OutlinedTextField(
//                value = height,
//                onValueChange = onHeightChange,
//                label = { Text("Chiều cao (cm)") },
//                singleLine = true,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .border(
//                        width = 2.dp,
//                        brush = Brush.horizontalGradient(
//                            colors = listOf(
//                                Color(0xFFD896D8),
//                                Color(0xFFFF69B4)
//                            )
//                        ),
//                        shape = RoundedCornerShape(12.dp)
//                    ),
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = Color.Transparent,
//                    unfocusedBorderColor = Color.Transparent
//                ),
//                shape = RoundedCornerShape(12.dp)
//            )
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                Button(
//                    onClick = onClose,
//                    modifier = Modifier.weight(1f),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
//                ) {
//                    Text("Hủy", color = Color.Black)
//                }
//                Button(
//                    onClick = onSave,
//                    modifier = Modifier.weight(1f),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD896D8))
//                ) {
//                    Text("Lưu")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun SymptomForm(
//    name: String,
//    onNameChange: (String) -> Unit,
//    desc: String,
//    onDescChange: (String) -> Unit,
//    onClose: () -> Unit,
//    onSave: () -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black.copy(alpha = 0.5f))
//            .clickable { onClose() },
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth(0.85f)
//                .background(Color.White, RoundedCornerShape(16.dp))
//                .padding(20.dp)
//                .clickable(enabled = false) { /* Ngăn click đóng form */ }
//        ) {
//            Text("Thêm triệu chứng", fontWeight = FontWeight.Bold, fontSize = 18.sp)
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Name Field - Viền gradient
//            OutlinedTextField(
//                value = name,
//                onValueChange = onNameChange,
//                label = { Text("Tên triệu chứng") },
//                singleLine = true,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .border(
//                        width = 2.dp,
//                        brush = Brush.horizontalGradient(
//                            colors = listOf(
//                                Color(0xFFD896D8),
//                                Color(0xFFFF69B4)
//                            )
//                        ),
//                        shape = RoundedCornerShape(12.dp)
//                    ),
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = Color.Transparent,
//                    unfocusedBorderColor = Color.Transparent
//                ),
//                shape = RoundedCornerShape(12.dp)
//            )
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Description Field - Viền gradient
//            OutlinedTextField(
//                value = desc,
//                onValueChange = onDescChange,
//                label = { Text("Mô tả") },
//                maxLines = 3,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(100.dp)
//                    .border(
//                        width = 2.dp,
//                        brush = Brush.horizontalGradient(
//                            colors = listOf(
//                                Color(0xFFD896D8),
//                                Color(0xFFFF69B4)
//                            )
//                        ),
//                        shape = RoundedCornerShape(12.dp)
//                    ),
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = Color.Transparent,
//                    unfocusedBorderColor = Color.Transparent
//                ),
//                shape = RoundedCornerShape(12.dp)
//            )
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                Button(
//                    onClick = onClose,
//                    modifier = Modifier.weight(1f),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
//                ) {
//                    Text("Hủy", color = Color.Black)
//                }
//                Button(
//                    onClick = onSave,
//                    modifier = Modifier.weight(1f),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD896D8))
//                ) {
//                    Text("Lưu")
//                }
//            }
//        }
//    }
//}