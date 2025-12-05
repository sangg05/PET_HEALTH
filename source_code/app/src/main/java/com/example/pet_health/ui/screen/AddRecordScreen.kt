package com.example.pet_health.ui.screen

import BottomBar
import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.pet_health.data.entity.VaccineEntity
import com.example.pet_health.data.entity.PetEntity
import com.example.pet_health.ui.viewmodel.VaccineViewModel
import com.example.pet_health.ui.viewmodel.VaccineViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: VaccineViewModel = viewModel(
        factory = VaccineViewModelFactory(context)
    )
    val petList by viewModel.pets

    var selectedType by remember { mutableStateOf("Tiêm") }
    var selectedPetId by remember { mutableStateOf("") }
    var showPetDropdown by remember { mutableStateOf(false) }

    var vaccineName by remember { mutableStateOf("") }
    var muiSo by remember { mutableStateOf("") }
    var ngayTiem by remember { mutableStateOf("") }
    var coSoTiem by remember { mutableStateOf("") }
    var ghiChuTiem by remember { mutableStateOf("") }

    var tenThuoc by remember { mutableStateOf("") }
    var ngayBatDau by remember { mutableStateOf("") }
    var ngayKetThuc by remember { mutableStateOf("") }
    var ghiChuThuoc by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var errorPet by remember { mutableStateOf("") }
    var errorVaccineName by remember { mutableStateOf("") }
    var errorNgayTiem by remember { mutableStateOf("") }
    var errorCoSoTiem by remember { mutableStateOf("") }
    var errorTenThuoc by remember { mutableStateOf("") }
    var errorNgayBatDau by remember { mutableStateOf("") }
    var errorNgayKetThuc by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> imageUri = uri }
    )

    fun openDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                onDateSelected(String.format("%02d/%02d/%04d", day, month + 1, year))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Hàm convert date string sang timestamp
    fun dateStringToTimestamp(dateStr: String): Long {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.parse(dateStr)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor = Color.White,
        focusedBorderColor = Color(0xFF7B1FA2),
        unfocusedBorderColor = Color.Gray
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFDD00), Color(0xFFFFD6EC), Color(0xFFEAD6FF))
                )
            )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Thêm bản ghi", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = lightPink)
                )
            },
            bottomBar = { BottomBar(navController = navController) }
        ){ padding ->
            Column(
                modifier = Modifier
                    .background(Brush.verticalGradient(listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))))
                    .padding(padding)
                    .padding(10.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                // Dropdown chọn pet
                ExposedDropdownMenuBox(
                    expanded = showPetDropdown,
                    onExpandedChange = { showPetDropdown = it }
                ) {
                    OutlinedTextField(
                        value = petList.find { it.petId == selectedPetId }?.name ?: "Chọn thú cưng",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Thú cưng") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPetDropdown) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )
                    ExposedDropdownMenu(
                        expanded = showPetDropdown,
                        onDismissRequest = { showPetDropdown = false }
                    ) {
                        petList.forEach { pet ->
                            DropdownMenuItem(
                                text = { Text(pet.name) },
                                onClick = {
                                    selectedPetId = pet.petId
                                    errorPet = ""
                                    showPetDropdown = false
                                }
                            )
                        }
                    }
                }
                if (errorPet.isNotEmpty()) Text(errorPet, color = Color.Red, fontSize = 12.sp)

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { selectedType = "Tiêm" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedType == "Tiêm") Color(0xFFB76EFF) else Color.LightGray
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) { Text("Tiêm", color = Color.White) }

                    Button(
                        onClick = { selectedType = "Thuốc" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedType == "Thuốc") Color(0xFFD284FF) else Color.LightGray
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) { Text("Thuốc", color = Color.White) }
                }

                Spacer(Modifier.height(18.dp))

                if (selectedType == "Tiêm") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = vaccineName,
                            onValueChange = { vaccineName = it; errorVaccineName = "" },
                            label = { Text("Tên vắc-xin") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors
                        )
                        OutlinedTextField(
                            value = muiSo,
                            onValueChange = { muiSo = it },
                            label = { Text("Mũi số") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors
                        )
                    }
                    if (errorVaccineName.isNotEmpty()) Text(errorVaccineName, color = Color.Red, fontSize = 12.sp)

                    Spacer(Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { openDatePicker { ngayTiem = it; errorNgayTiem = "" } }
                    ) {
                        OutlinedTextField(
                            value = ngayTiem,
                            onValueChange = {},
                            label = { Text("Ngày tiêm") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            enabled = false,
                            shape = RoundedCornerShape(12.dp),
                            colors = fieldColors
                        )
                    }
                    if (errorNgayTiem.isNotEmpty()) Text(errorNgayTiem, color = Color.Red, fontSize = 12.sp)

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = coSoTiem,
                        onValueChange = { coSoTiem = it; errorCoSoTiem = "" },
                        label = { Text("Cơ sở tiêm") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )
                    if (errorCoSoTiem.isNotEmpty()) Text(errorCoSoTiem, color = Color.Red, fontSize = 12.sp)

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = ghiChuTiem,
                        onValueChange = { ghiChuTiem = it },
                        label = { Text("Ghi chú") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )

                    Spacer(Modifier.height(12.dp))

                    Text("Chứng nhận ảnh", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .size(width = 280.dp, height = 110.dp)
                            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(12.dp))
                            .clickable { pickImage.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text("Thêm ảnh", color = Color.Gray)
                        }
                    }

                } else {
                    OutlinedTextField(
                        value = tenThuoc,
                        onValueChange = { tenThuoc = it; errorTenThuoc = "" },
                        label = { Text("Tên thuốc") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )
                    if (errorTenThuoc.isNotEmpty()) Text(errorTenThuoc, color = Color.Red, fontSize = 12.sp)

                    Spacer(Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { openDatePicker { ngayBatDau = it; errorNgayBatDau = "" } }
                        ) {
                            OutlinedTextField(
                                value = ngayBatDau,
                                onValueChange = {},
                                label = { Text("Ngày bắt đầu") },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                enabled = false,
                                shape = RoundedCornerShape(12.dp),
                                colors = fieldColors
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { openDatePicker { ngayKetThuc = it; errorNgayKetThuc = "" } }
                        ) {
                            OutlinedTextField(
                                value = ngayKetThuc,
                                onValueChange = {},
                                label = { Text("Ngày kết thúc") },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                enabled = false,
                                shape = RoundedCornerShape(12.dp),
                                colors = fieldColors
                            )
                        }
                    }
                    if (errorNgayBatDau.isNotEmpty()) Text(errorNgayBatDau, color = Color.Red, fontSize = 12.sp)
                    if (errorNgayKetThuc.isNotEmpty()) Text(errorNgayKetThuc, color = Color.Red, fontSize = 12.sp)

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = ghiChuThuoc,
                        onValueChange = { ghiChuThuoc = it },
                        label = { Text("Ghi chú") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(20.dp)
                    ) { Text("Hủy", color = Color.White) }

                    Button(
                        onClick = {
                            var valid = true

                            if (selectedPetId.isBlank()) {
                                errorPet = "Vui lòng chọn thú cưng"
                                valid = false
                            }

                            if (!valid) return@Button

                            // chạy upload + lưu bằng coroutine
                            coroutineScope.launch {

                                // 1. Upload ảnh lên Cloudinary nếu có chọn ảnh
                                val url = if (imageUri != null) {
                                    suspendCancellableCoroutine<String?> { cont ->
                                        viewModel.uploadVaccineImage(
                                            context = context,
                                            uri = imageUri!!,
                                            userId = selectedPetId  // hoặc lấy userId thật
                                        ) { result ->
                                            cont.resume(result)
                                        }
                                    }
                                } else null

                                // 2. Tạo object vaccine
                                val vaccine = VaccineEntity(
                                    vaccineId = UUID.randomUUID().toString(),
                                    petId = selectedPetId,
                                    name = vaccineName,
                                    date = dateStringToTimestamp(ngayTiem),
                                    type = "Tiêm",
                                    clinic = coSoTiem,
                                    doseNumber = muiSo.toIntOrNull(),
                                    endDate = null,
                                    note = ghiChuTiem,
                                    photoUrl = url,
                                    createdAt = System.currentTimeMillis(),
                                    nextDoseDate = null
                                )

                                // 3. Lưu vào DB
                                viewModel.addVaccine(vaccine)

                                navController.popBackStack()
                            }
                        }
                    ) {
                        Text("Lưu")
                    }

                }
            }
        }
    }
}