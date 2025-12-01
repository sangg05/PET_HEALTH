
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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.pet_health.ui.screens.lightPink
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(navController: NavController) {
    var selectedType by remember { mutableStateOf("Tiêm") }
    var petName by remember { mutableStateOf("") }

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

    val context = LocalContext.current

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
                    listOf(
                        Color(0xFFFFDD00),
                        Color(0xFFFFD6EC),
                        Color(0xFFEAD6FF)
                    )
                )
            )
    ) {
        Scaffold(
            topBar = {
                var backgroundColor = Color.Transparent
                TopAppBar(
                    title = { Text("Thêm bản ghi", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = lightPink)
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
        )
        { padding ->
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))
                        )
                    )
                    .padding(padding)
                    .padding(10.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                OutlinedTextField(
                    value = petName,
                    onValueChange = { petName = it; errorPetName = "" },
                    label = { Text("Tên thú cưng") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )
                if (errorPetName.isNotEmpty()) Text(
                    errorPetName,
                    color = Color.Red,
                    fontSize = 12.sp
                )

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
                    if (errorVaccineName.isNotEmpty()) Text(
                        errorVaccineName,
                        color = Color.Red,
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    // ✅ Fix DatePicker không mở
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
                    // ✅ Fix DatePicker cho Thuốc
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
                        shape = RoundedCornerShape(20.dp)
                    ) { Text("Hủy", color = Color.White) }

                    Button(
                        onClick = {
                            var valid = true
                            if (petName.isBlank()) {
                                errorPetName = "Vui lòng nhập tên thú cưng"; valid = false
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
                                val route = if (selectedType == "Tiêm")
                                    "record_detail/${encodeForNav(petName)}/${
                                        encodeForNav(
                                            selectedType
                                        )
                                    }/${encodeForNav(vaccineName)}/${encodeForNav(ngayTiem)}/${
                                        encodeForNav(
                                            ghiChuTiem
                                        )
                                    }"
                                else
                                    "record_detail/${encodeForNav(petName)}/${
                                        encodeForNav(
                                            selectedType
                                        )
                                    }/${encodeForNav(tenThuoc)}/${encodeForNav(ngayBatDau)}/${
                                        encodeForNav(
                                            ghiChuThuoc
                                        )
                                    }"

                                navController.navigate(route)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9CFF9C)),
                        shape = RoundedCornerShape(20.dp)
                    ) { Text("Lưu", color = Color.Black) }
                }
            }
        }
    }
}

private fun encodeForNav(value: String): String {
    return try {
        java.net.URLEncoder.encode(value, "UTF-8")
    } catch (e: Exception) {
        ""
    }
}