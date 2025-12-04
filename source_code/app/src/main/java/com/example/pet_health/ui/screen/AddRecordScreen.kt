package com.example.pet_health.ui.screen

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
import androidx.compose.material.icons.filled.ArrowDropDown
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
import com.example.pet_health.data.repository.PetRepository
import com.example.pet_health.ui.viewmodel.PetViewModel
import com.example.pet_health.ui.viewmodel.PetViewModelFactory
import com.example.pet_health.ui.viewmodel.VaccineViewModel
import pet_health.data.local.AppDatabase
import java.text.SimpleDateFormat
import java.util.*

// Định nghĩa màu lightPink ở đây (Top-level) để tránh xung đột
//private val lightPink = Color(0xFFFFC0CB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(
    navController: NavController,
    petId: String // ID được truyền vào (nếu từ màn hình chi tiết thú cưng)
) {
    val context = LocalContext.current

    // 1. Khởi tạo VaccineViewModel
    val viewModel = remember { VaccineViewModel(context) }

    // 2. Khởi tạo PetViewModel để lấy danh sách thú cưng
    // (Tạo thủ công factory vì ở đây không dùng Hilt injection)
    val db = AppDatabase.getDatabase(context)
    val petRepo = PetRepository(db)
    val petViewModel: PetViewModel = viewModel(factory = PetViewModelFactory(petRepo))

    // Lấy danh sách Pet và load dữ liệu
    LaunchedEffect(Unit) {
        petViewModel.fetchPetsFromFirebaseToRoom()
    }
    val pets by petViewModel.pets // Danh sách thú cưng từ Room/Firebase

    // State quản lý việc chọn Pet
    var selectedPetId by remember { mutableStateOf(petId) } // Lưu ID để gửi đi
    var petNameDisplay by remember { mutableStateOf("") } // Tên hiển thị trên ô nhập
    var expanded by remember { mutableStateOf(false) } // Trạng thái mở/đóng Dropdown

    // Nếu có petId truyền vào (từ màn hình Profile), tự động điền tên
    LaunchedEffect(petId, pets) {
        if (petId.isNotEmpty()) {
            val p = pets.find { it.petId == petId }
            if (p != null) {
                petNameDisplay = p.name
                selectedPetId = p.petId
            }
        }
    }

    var selectedType by remember { mutableStateOf("Tiêm") }
    // var petName -> Đã thay bằng petNameDisplay ở trên

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

    var errorPetName by remember { mutableStateOf("") }
    var errorVaccineName by remember { mutableStateOf("") }
    var errorNgayTiem by remember { mutableStateOf("") }
    var errorCoSoTiem by remember { mutableStateOf("") }
    var errorTenThuoc by remember { mutableStateOf("") }
    var errorNgayBatDau by remember { mutableStateOf("") }
    var errorNgayKetThuc by remember { mutableStateOf("") }

    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> imageUri = uri }
    )

    // Theo dõi trạng thái lưu từ ViewModel
    val isLoading by viewModel.isLoading
    val success by viewModel.success
    val newId by viewModel.createdVaccineId

    // === LOGIC CHUYỂN TRANG ===
    LaunchedEffect(success, newId) {
        if (success && newId != null) {
            navController.navigate("record_detail/$newId") {
                popUpTo("add_record") { inclusive = true }
            }
            viewModel.success.value = false
            viewModel.createdVaccineId.value = null
        }
    }

    fun dateStringToTimestamp(dateStr: String): Long {
        return try {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            format.parse(dateStr)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

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
                    listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))
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
            bottomBar = {
                // ... (Giữ nguyên BottomBar như cũ) ...
            },
            containerColor = Color.Transparent
        )
        { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {

                // === THAY THẾ OUTLINED TEXT FIELD BẰNG DROPDOWN ===
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        // Chỉ cho phép mở nếu không bị khóa cứng ID (tùy logic của bạn)
                        // Ở đây mình cho phép chọn lại kể cả khi có ID truyền vào
                        expanded = !expanded
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = petNameDisplay,
                        onValueChange = {}, // Read only, chỉ chọn từ list
                        readOnly = true,
                        label = { Text("Chọn thú cưng") },
                        placeholder = { Text("Chọn thú cưng từ danh sách") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(), // Bắt buộc phải có để menu bám vào
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (pets.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Chưa có thú cưng nào", color = Color.Gray) },
                                onClick = { expanded = false }
                            )
                        } else {
                            pets.forEach { pet ->
                                DropdownMenuItem(
                                    text = { Text(pet.name) },
                                    onClick = {
                                        petNameDisplay = pet.name
                                        selectedPetId = pet.petId // Cập nhật ID thật để lưu
                                        errorPetName = ""
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (errorPetName.isNotEmpty()) Text(
                    errorPetName,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
                // ==================================================

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

                // ... (Phần UI Tiêm/Thuốc giữ nguyên như cũ) ...
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
                    if (errorVaccineName.isNotEmpty()) Text(
                        errorVaccineName,
                        color = Color.Red,
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                openDatePicker { ngayTiem = it; errorNgayTiem = "" }
                            }
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
                    if (errorNgayTiem.isNotEmpty()) Text(
                        errorNgayTiem,
                        color = Color.Red,
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = coSoTiem,
                        onValueChange = { coSoTiem = it; errorCoSoTiem = "" },
                        label = { Text("Cơ sở tiêm") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )
                    if (errorCoSoTiem.isNotEmpty()) Text(
                        errorCoSoTiem,
                        color = Color.Red,
                        fontSize = 12.sp
                    )

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
                    if (errorTenThuoc.isNotEmpty()) Text(
                        errorTenThuoc,
                        color = Color.Red,
                        fontSize = 12.sp
                    )

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
                                ) {
                                    openDatePicker { ngayBatDau = it; errorNgayBatDau = "" }
                                }
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
                                ) {
                                    openDatePicker { ngayKetThuc = it; errorNgayKetThuc = "" }
                                }
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
                    if (errorNgayBatDau.isNotEmpty()) Text(
                        errorNgayBatDau,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                    if (errorNgayKetThuc.isNotEmpty()) Text(
                        errorNgayKetThuc,
                        color = Color.Red,
                        fontSize = 12.sp
                    )

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
                        shape = RoundedCornerShape(20.dp),
                        enabled = !isLoading
                    ) { Text("Hủy", color = Color.White) }

                    Button(
                        onClick = {
                            var valid = true
                            // Kiểm tra ID thay vì tên
                            if (selectedPetId.isBlank()) {
                                errorPetName = "Vui lòng chọn thú cưng"; valid = false
                            }

                            if (selectedType == "Tiêm") {
                                if (vaccineName.isBlank()) {
                                    errorVaccineName = "Vui lòng nhập tên vắc-xin"; valid = false
                                }
                                if (ngayTiem.isBlank()) {
                                    errorNgayTiem = "Vui lòng chọn ngày tiêm"; valid = false
                                }
                                if (coSoTiem.isBlank()) {
                                    errorCoSoTiem = "Vui lòng nhập cơ sở tiêm"; valid = false
                                }
                            } else {
                                if (tenThuoc.isBlank()) {
                                    errorTenThuoc = "Vui lòng nhập tên thuốc"; valid = false
                                }
                                if (ngayBatDau.isBlank()) {
                                    errorNgayBatDau = "Vui lòng chọn ngày bắt đầu"; valid = false
                                }
                                if (ngayKetThuc.isBlank()) {
                                    errorNgayKetThuc = "Vui lòng chọn ngày kết thúc"; valid = false
                                }
                            }

                            if (valid) {
                                if (selectedType == "Tiêm") {
                                    viewModel.addVaccine(
                                        petId = selectedPetId, // Dùng ID đã chọn
                                        name = vaccineName,
                                        date = dateStringToTimestamp(ngayTiem),
                                        clinic = coSoTiem.takeIf { it.isNotBlank() },
                                        doseNumber = muiSo.toIntOrNull(),
                                        note = ghiChuTiem.takeIf { it.isNotBlank() },
                                        imageUri = imageUri,
                                        nextDoseDate = null
                                    )
                                } else {
                                    viewModel.addVaccine(
                                        petId = selectedPetId, // Dùng ID đã chọn
                                        name = tenThuoc,
                                        date = dateStringToTimestamp(ngayBatDau),
                                        clinic = null,
                                        doseNumber = null,
                                        note = ghiChuThuoc.takeIf { it.isNotBlank() },
                                        imageUri = null,
                                        nextDoseDate = dateStringToTimestamp(ngayKetThuc)
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9CFF9C)),
                        shape = RoundedCornerShape(20.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Black
                            )
                        } else {
                            Text("Lưu", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}