package com.example.pet_health.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.shape.RoundedCornerShape
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReminderFormScreen(navController: NavController? = null) {

    // ==== FORM STATES ====
    var petName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var repeat by remember { mutableStateOf("Không") }
    var earlyNotify by remember { mutableStateOf("Không") }
    var note by remember { mutableStateOf("") }

    // ==== ERROR STATES ====
    var petError by remember { mutableStateOf("") }
    var typeError by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf("") }
    var timeError by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Date Picker
    val datePickerDialog = DatePickerDialog(
        context,
        { _, y, m, d -> date = "%02d/%02d/%04d".format(d, m + 1, y) },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Time Picker
    val timePickerDialog = TimePickerDialog(
        context,
        { _, h, min -> time = "%02d:%02d".format(h, min) },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF7C8E0), Color(0xFFF9E6F2))
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // HEADER
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController?.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Tạo nhắc", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(10.dp))

        // ==== THÚ CƯNG ====
        Text("Thú cưng", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        OutlinedTextField(
            value = petName,
            onValueChange = {
                petName = it
                petError = ""
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(13.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )
        if (petError.isNotEmpty()) Text(petError, color = Color.Red, fontSize = 13.sp)

        Spacer(Modifier.height(10.dp))

        // ==== LOẠI NHẮC ====
        Text("Loại nhắc", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        val types = listOf("Tiêm phòng", "Tẩy giun", "Tái khám", "Thuốc", "Khác")

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            types.forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = {
                        selectedType = type
                        typeError = ""
                    },
                    label = { Text(type, color = Color.Black) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White,
                        selectedContainerColor = Color(0xFFD5B4F7)
                    ),
                    shape = RoundedCornerShape(15.dp)
                )
            }
        }
        if (typeError.isNotEmpty()) Text(typeError, color = Color.Red, fontSize = 13.sp)

        Spacer(Modifier.height(10.dp))

        // ==== NGÀY ====
        Text("Ngày", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Box(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { datePickerDialog.show() }
        ) {
            OutlinedTextField(
                value = date,
                onValueChange = {},
                enabled = false,
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(13.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledContainerColor = Color.White,
                    disabledBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
        if (dateError.isNotEmpty()) Text(dateError, color = Color.Red, fontSize = 13.sp)

        Spacer(Modifier.height(10.dp))

        // ==== GIỜ ====
        Text("Giờ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Box(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { timePickerDialog.show() }
        ) {
            OutlinedTextField(
                value = time,
                onValueChange = {},
                enabled = false,
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(13.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledContainerColor = Color.White,
                    disabledBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
        if (timeError.isNotEmpty()) Text(timeError, color = Color.Red, fontSize = 13.sp)

        Spacer(Modifier.height(10.dp))

        // ==== CHU KỲ LẶP ====
        Text("Chu kì lặp", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        val repeats = listOf("Không", "Tháng", "Tuần", "Tùy chỉnh")

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeats.forEach { item ->
                FilterChip(
                    selected = repeat == item,
                    onClick = { repeat = item },
                    label = { Text(item, color = Color.Black) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White,
                        selectedContainerColor = Color(0xFFD5B4F7)
                    ),
                    shape = RoundedCornerShape(15.dp)
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // ==== NHẮC SỚM ====
        Text("Nhắc sớm", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        val earlyOptions = listOf("Không", "1 giờ", "1 ngày", "3 ngày")

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            earlyOptions.forEach { item ->
                FilterChip(
                    selected = earlyNotify == item,
                    onClick = { earlyNotify = item },
                    label = { Text(item, color = Color.Black) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White,
                        selectedContainerColor = Color(0xFFD5B4F7)
                    ),
                    shape = RoundedCornerShape(15.dp)
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // ==== GHI CHÚ ====
        Text("Ghi chú", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(13.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        Spacer(Modifier.height(20.dp))

        // ==== BUTTONS ====
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) { Text("Xóa nhắc", color = Color.White) }

            Button(
                onClick = { navController?.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3FFB3))
            ) { Text("Hủy", color = Color.Black) }

            Button(
                onClick = {
                    // ==== VALIDATION ====
                    var isValid = true

                    if (petName.isBlank()) {
                        petError = "Vui lòng nhập tên thú cưng"
                        isValid = false
                    }
                    if (selectedType.isBlank()) {
                        typeError = "Vui lòng chọn loại nhắc"
                        isValid = false
                    }
                    if (date.isBlank()) {
                        dateError = "Vui lòng chọn ngày"
                        isValid = false
                    }
                    if (time.isBlank()) {
                        timeError = "Vui lòng chọn giờ"
                        isValid = false
                    }

                    if (!isValid) return@Button

                    fun enc(text: String) =
                        URLEncoder.encode(text, StandardCharsets.UTF_8.toString())

                    val route =
                        "reminder_detail/" +
                                enc(petName) + "/" +
                                enc(selectedType) + "/" +
                                enc(date) + "/" +
                                enc(time) + "/" +
                                enc(repeat) + "/" +
                                enc(earlyNotify) + "/" +
                                enc(note)

                    navController?.navigate(route)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Lưu", color = Color.White)
            }
        }
    }
}