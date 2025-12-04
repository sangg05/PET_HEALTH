package com.example.pet_health.ui.screen

import android.util.Log
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pet_health.data.entity.PetEntity
import com.example.pet_health.data.entity.SymptomLogEntity
import com.example.pet_health.ui.screens.AppFilterChip
import com.example.pet_health.ui.screens.lightPink
import com.example.pet_health.ui.viewmodel.HealthTrackingViewModel
import com.example.pet_health.ui.viewmodel.PetViewModel
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthTrackingScreen(
    petViewModel: PetViewModel,
    viewModel: HealthTrackingViewModel,
    navController: NavController
) {
    val scrollState = rememberScrollState()
    val pets = petViewModel.pets.value
    val selectedPet = viewModel.selectedPet // dùng trực tiếp ViewModel

    var showMetricForm by remember { mutableStateOf(false) }
    var newWeight by remember { mutableStateOf("") }
    var newHeight by remember { mutableStateOf("") }

    var showSymptomForm by remember { mutableStateOf(false) }
    var newSymptomName by remember { mutableStateOf("") }
    var newSymptomDesc by remember { mutableStateOf("") }

    val petOptions = listOf<PetEntity?>(null) + pets

    val symptoms by selectedPet?.petId?.let { petId ->
        viewModel.getSymptomsFlow(petId).collectAsState(initial = emptyList())
    } ?: remember { mutableStateOf(emptyList()) }

    LaunchedEffect(selectedPet?.petId) {
        selectedPet?.let { viewModel.syncSymptoms(it.petId) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sức khỏe thú cưng", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = lightPink)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // --- Pet Chips ---
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisSpacing = 6.dp,
                crossAxisSpacing = 6.dp
            ) {
                petOptions.forEach { pet ->
                    val petName = pet?.name ?: "Tất cả"
                    AppFilterChip(
                        text = petName,
                        selected = selectedPet?.petId == pet?.petId || (selectedPet == null && pet == null),
                        onClick = { viewModel.selectPet(pet) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Health Metrics ---
            val selected = selectedPet
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                HealthMetricCard(
                    value = selected?.weightKg?.let { "$it kg" } ?: "--",
                    label = "Cân nặng",
                    modifier = Modifier.weight(1f)
                ) {
                    selected?.let {
                        showMetricForm = true
                        newWeight = if (it.weightKg > 0f) it.weightKg.toString() else ""
                        newHeight = it.sizeCm?.toString() ?: ""
                    }
                }

                HealthMetricCard(
                    value = selected?.sizeCm?.let { "$it cm" } ?: "--",
                    label = "Chiều cao",
                    modifier = Modifier.weight(1f)
                ) {
                    selected?.let {
                        showMetricForm = true
                        newWeight = if (it.weightKg > 0f) it.weightKg.toString() else ""
                        newHeight = it.sizeCm?.toString() ?: ""
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showMetricForm = true },
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

            // --- Symptoms Section ---
            Text("Nhật ký triệu chứng:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            symptoms.forEach { symptom ->
                SymptomItem(symptom) { viewModel.deleteSymptom(it) }
            }

            Spacer(modifier = Modifier.height(16.dp))
            IconButton(
                onClick = { showSymptomForm = true },
                modifier = Modifier.size(48.dp).background(Color(0xFF00AA00), CircleShape),
                content = {
                    Icon(Icons.Default.Add, contentDescription = "Add symptom", tint = Color.White)
                }
            )
        }
    }

    // --- Metric Form ---
    if (showMetricForm) {
        MetricForm(
            weight = newWeight,
            onWeightChange = { newWeight = it },
            height = newHeight,
            onHeightChange = { newHeight = it },
            onClose = { showMetricForm = false },
            onSave = {
                selectedPet?.let { pet ->
                    val updatedPet = pet.copy(
                        weightKg = newWeight.toFloatOrNull() ?: pet.weightKg,
                        sizeCm = newHeight.toFloatOrNull() ?: pet.sizeCm
                    )
                    viewModel.updatePet(updatedPet)
                    newWeight = ""
                    newHeight = ""
                    showMetricForm = false
                }
            }
        )
    }

    // --- Symptom Form ---
    if (showSymptomForm) {
        SymptomForm(
            name = newSymptomName,
            onNameChange = { newSymptomName = it },
            desc = newSymptomDesc,
            onDescChange = { newSymptomDesc = it },
            onClose = { showSymptomForm = false },
            viewModel = viewModel, // truyền viewModel vào
            resetForm = {
                newSymptomName = ""
                newSymptomDesc = ""
                showSymptomForm = false
            }
        )
    }
}
// --- Composable Conventions ---
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
fun HealthMetricCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .border(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFF60F8),
                        Color(0xFFFFF6F9)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold,   color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                label,
                fontSize = 14.sp,
                color = Color.Black,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun MetricForm(
    weight: String, onWeightChange: (String) -> Unit,
    height: String, onHeightChange: (String) -> Unit,
    onClose: () -> Unit, onSave: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.8f)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(20.dp)
                .clickable(enabled = false) {}
        ) {
            Text("Cập nhật dữ liệu", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = weight,
                onValueChange = onWeightChange,
                label = { Text("Cân nặng (kg)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = height,
                onValueChange = onHeightChange,
                label = { Text("Chiều cao (cm)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onClose, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                    Text("Hủy", color = Color.Black)
                }
                Button(onClick = onSave, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD896D8))) {
                    Text("Lưu")
                }
            }
        }
    }
}

@Composable
fun SymptomForm(
    name: String,
    onNameChange: (String) -> Unit,
    desc: String,
    onDescChange: (String) -> Unit,
    onClose: () -> Unit,
    viewModel: HealthTrackingViewModel,
    resetForm: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(20.dp)
                .clickable(enabled = false) {}
        ) {
            Text("Thêm triệu chứng", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Tên triệu chứng") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = desc,
                onValueChange = onDescChange,
                label = { Text("Mô tả") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { resetForm() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Hủy", color = Color.Black)
                }

                Button(
                    onClick = {
                        val trimmedName = name.trim()
                        val trimmedDesc = desc.trim()
                        val selected = viewModel.selectedPet
                        if (trimmedName.isNotBlank() && trimmedDesc.isNotBlank() && selected != null) {
                            val symptom = SymptomLogEntity(
                                id = UUID.randomUUID().toString(),
                                petId = selected.petId,
                                name = trimmedName,
                                description = trimmedDesc,
                                timestamp = System.currentTimeMillis()
                            )

                            // gọi hàm saveSymptom của ViewModel
                            viewModel.saveSymptom(symptom) {
                                resetForm() // reset form sau khi lưu xong
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD896D8))
                ) {
                    Text("Lưu")
                }
            }
        }
    }
}
@Composable
fun SymptomItem(symptom: SymptomLogEntity, onDelete: (SymptomLogEntity) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(symptom.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(symptom.description, fontSize = 12.sp, color = Color.Gray)
            }
            IconButton(onClick = { onDelete(symptom) }) {
                Icon(Icons.Default.Delete, contentDescription = "Xóa triệu chứng", tint = Color.Red)
            }
        }
    }
}
