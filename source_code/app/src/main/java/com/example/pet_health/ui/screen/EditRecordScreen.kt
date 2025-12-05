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
import com.example.pet_health.ui.viewmodel.VaccineViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecordScreen(
    navController: NavController,
    vaccineId: String
) {
    val context = LocalContext.current
    val viewModel = remember { VaccineViewModel(context) }

    val vaccineList by viewModel.vaccines.collectAsState()
    val petList by viewModel.pets

    val vaccine = vaccineList.find { it.vaccineId == vaccineId }

    if (vaccine == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Không tìm thấy bản ghi")
        }
        return
    }

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    var selectedPetId by remember { mutableStateOf(vaccine.petId) }
    var showPetDropdown by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(vaccine.name) }
    var date by remember { mutableStateOf(sdf.format(Date(vaccine.date))) }
    var clinic by remember { mutableStateOf(vaccine.clinic ?: "") }
    var notes by remember { mutableStateOf(vaccine.note ?: "") }
    var doseNumber by remember { mutableStateOf(vaccine.doseNumber?.toString() ?: "") }
    var endDate by remember { mutableStateOf(
        vaccine.endDate?.let { sdf.format(Date(it)) } ?: ""
    ) }
    var imageUri by remember { mutableStateOf<Uri?>(
        vaccine.photoUrl?.let { Uri.parse(it) }
    ) }

    var errorName by remember { mutableStateOf("") }
    var errorDate by remember { mutableStateOf("") }

    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> imageUri = uri }
    )

    fun openDatePicker(currentDate: String, onDateSelected: (String) -> Unit) {
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

    fun dateStringToTimestamp(dateStr: String): Long {
        return try {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa bản ghi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = lightPink)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))
                    )
                )
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Pet Dropdown
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
                                showPetDropdown = false
                            }
                        )
                    }
                }
            }

            // Tên vaccine/thuốc
            if (vaccine.type == "Tiêm") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; errorName = "" },
                        label = { Text("Tên vắc-xin") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )
                    OutlinedTextField(
                        value = doseNumber,
                        onValueChange = { doseNumber = it },
                        label = { Text("Mũi số") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )
                }
            } else {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorName = "" },
                    label = { Text("Tên thuốc") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )
            }
            if (errorName.isNotEmpty()) Text(errorName, color = Color.Red, fontSize = 12.sp)

            // Ngày
            if (vaccine.type == "Tiêm") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { openDatePicker(date) { date = it; errorDate = "" } }
                ) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = {},
                        label = { Text("Ngày tiêm") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        enabled = false,
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { openDatePicker(date) { date = it } }
                    ) {
                        OutlinedTextField(
                            value = date,
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
                            ) { openDatePicker(endDate) { endDate = it } }
                    ) {
                        OutlinedTextField(
                            value = endDate,
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
            }
            if (errorDate.isNotEmpty()) Text(errorDate, color = Color.Red, fontSize = 12.sp)

            // Cơ sở tiêm (chỉ cho vaccine)
            if (vaccine.type == "Tiêm") {
                OutlinedTextField(
                    value = clinic,
                    onValueChange = { clinic = it },
                    label = { Text("Cơ sở tiêm") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )
            }

            // Ghi chú
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Ghi chú") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors
            )

            // Ảnh
            Text("Chứng nhận ảnh", fontWeight = FontWeight.Medium)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
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

            Spacer(Modifier.height(8.dp))

            // Buttons
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
                        if (name.isBlank()) {
                            errorName = "Vui lòng nhập tên"; valid = false
                        }
                        if (date.isBlank()) {
                            errorDate = "Vui lòng chọn ngày"; valid = false
                        }

                        if (valid) {
                            val updatedVaccine = vaccine.copy(
                                petId = selectedPetId,
                                name = name,
                                date = dateStringToTimestamp(date),
                                clinic = if (vaccine.type == "Tiêm") clinic else null,
                                note = notes,
                                doseNumber = if (vaccine.type == "Tiêm" && doseNumber.isNotBlank())
                                    doseNumber.toIntOrNull() else null,
                                endDate = if (vaccine.type == "Thuốc" && endDate.isNotBlank())
                                    dateStringToTimestamp(endDate) else null,
                                photoUrl = imageUri?.toString()
                            )

                            viewModel.updateVaccine(updatedVaccine)
                            navController.popBackStack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9CFF9C)),
                    shape = RoundedCornerShape(20.dp)
                ) { Text("Cập nhật", color = Color.Black) }
            }
        }
    }
}