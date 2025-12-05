package com.example.pet_health.ui.screens

import BottomBar
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.example.pet_health.ui.viewmodel.HealthRecordViewModel
import com.example.pet_health.ui.viewmodel.PetViewModel
import java.util.Calendar
import android.app.DatePickerDialog
import com.example.pet_health.data.entity.HealthRecordEntity
import java.util.Locale
import java.util.UUID

val lightPink = Color(0xFFFFD2FC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHealthRecordScreen(
    navController: NavController,
    petViewModel: PetViewModel,
    healthRecordViewModel: HealthRecordViewModel,
    defaultPetId: String? = null
) {

    val context = LocalContext.current
    val pets by remember { petViewModel.pets }
    var showDialog by remember { mutableStateOf(false) }
    var selectedPetName by remember { mutableStateOf("") }


    var selectedPetId by remember { mutableStateOf(defaultPetId ?: "") }
    var expanded by remember { mutableStateOf(false) } // dropdown
    var date by remember { mutableStateOf("") }
    var symptoms by remember { mutableStateOf("") }
    var prescription by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }

    // DatePicker
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            date = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor = Color.White,
    )
    val cornerShape = RoundedCornerShape(10.dp)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Cập nhật bệnh án mới",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
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
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Thông tin bệnh án",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
                Text("Chọn thú cưng", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = pets.firstOrNull { it.petId == selectedPetId }?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Thú cưng") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = customTextFieldColors,
                        shape = cornerShape
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        pets.forEach { pet ->
                            DropdownMenuItem(
                                text = { Text(pet.name) },
                                onClick = {
                                    selectedPetId = pet.petId
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // === Các trường nhập liệu ===
                PetInputField(
                    label = "Ngày khám",
                    value = date,
                    onValueChange = { date = it },
                    trailingIcon = Icons.Default.DateRange,
                    isDateField = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                PetInputField(
                    label = "Triệu chứng",
                    value = symptoms,
                    onValueChange = { symptoms = it },
                    trailingIcon = Icons.Default.Edit
                )
                Spacer(modifier = Modifier.height(16.dp))
                PetInputField(
                    label = "Đơn thuốc",
                    value = prescription,
                    onValueChange = { prescription = it },
                    trailingIcon = Icons.Default.Edit
                )

                Spacer(modifier = Modifier.height(16.dp))

                // === Nút hành động ===
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Button(
                        onClick = {
                            // Validate dữ liệu
                            if (selectedPetId.isEmpty()) {
                                Toast.makeText(context, "Vui lòng chọn thú cưng", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (date.isEmpty()) {
                                Toast.makeText(context, "Vui lòng chọn ngày khám", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val record = HealthRecordEntity(
                                recordId = UUID.randomUUID().toString(),
                                petId = selectedPetId,
                                date = parseDateToMillis(date),
                                symptom = symptoms,
                                diagnosis = symptoms,
                                prescription = prescription,
                                weight = weight.toFloatOrNull(),
                                height = height.toFloatOrNull(),
                                alert = false
                            )

                            healthRecordViewModel.addRecord(record)

                            // Hiển thị thông báo
                            Toast.makeText(context, "Đã lưu bệnh án", Toast.LENGTH_SHORT).show()

                            // Quay lại màn hình trước
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB2F0C0)),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.width(130.dp)
                    ) {
                        Text("Lưu", color = Color.Black)
                    }

                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B)),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.width(130.dp)
                    ) {
                        Text("Hủy", color = Color.White)
                    }
                }
            }
        }
    }
}
fun parseDateToMillis(date: String): Long {
    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.parse(date)?.time ?: 0L
}
@Composable
fun HealthInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = Icons.Default.Edit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        trailingIcon = {
            Icon(trailingIcon!!, contentDescription = "Edit", tint = Color.Gray)
        },
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFCE3CCB),
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color(0xFFCE3CCB)
        )
    )
}
