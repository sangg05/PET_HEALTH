package com.example.pet_health.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.foundation.shape.RoundedCornerShape
import com.google.accompanist.flowlayout.FlowRow
import java.util.Calendar
import java.util.UUID

// Import đúng ViewModel và Entity
import com.example.pet_health.ui.viewmodel.ReminderViewModel
import com.example.pet_health.data.entity.Reminder
import com.example.pet_health.data.repository.PetRepository
import com.example.pet_health.ui.viewmodel.PetViewModel
import com.example.pet_health.ui.viewmodel.PetViewModelFactory
import com.example.pet_health.data.local.AppDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReminderFormScreen(
    navController: NavController? = null,
    viewModel: ReminderViewModel,
    reminderId: String? = null
) {
    val context = LocalContext.current

    // ==== KHỞI TẠO PET VIEWMODEL ĐỂ LẤY DANH SÁCH THÚ CƯNG ====
    val db = AppDatabase.getDatabase(context)
    val petRepo = PetRepository(db)
    val petViewModel: PetViewModel = viewModel(factory = PetViewModelFactory(petRepo))

    // Load danh sách pet
    LaunchedEffect(Unit) {
        petViewModel.fetchPetsFromFirebaseToRoom()
    }
    val pets by petViewModel.pets

    // ==== FORM STATES ====
    var petName by remember { mutableStateOf("") } // Biến này giờ sẽ lưu TÊN thú cưng đã chọn
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }
    var otherTypeInput by remember { mutableStateOf("") }

    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var repeat by remember { mutableStateOf("Không") }
    var customRepeat by remember { mutableStateOf("") }
    var earlyNotify by remember { mutableStateOf("Không") }
    var note by remember { mutableStateOf("") }

    // State cho Dropdown
    var expanded by remember { mutableStateOf(false) }

    // ==== ERROR STATES ====
    var petError by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf("") }
    var typeError by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf("") }
    var timeError by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()

    // ==== LOGIC ĐIỀN DỮ LIỆU CŨ (EDIT) ====
    LaunchedEffect(reminderId) {
        if (reminderId != null) {
            val existing = viewModel.getReminderById(reminderId)
            if (existing != null) {
                petName = existing.petName // Điền tên cũ
                title = existing.title
                date = existing.date
                time = existing.time
                earlyNotify = existing.earlyNotify
                note = existing.note

                val defaultTypes = listOf("Tiêm phòng", "Tẩy giun", "Tái khám", "Thuốc")
                if (existing.type in defaultTypes) {
                    selectedType = existing.type
                } else {
                    selectedType = "Khác"
                    otherTypeInput = existing.type
                }

                if (existing.repeat.contains("(Tùy chỉnh)")) {
                    repeat = "Tùy chỉnh"
                    customRepeat = existing.repeat.substringBefore(" ngày")
                } else {
                    repeat = existing.repeat
                }
            }
        }
    }

    // <--- THEO DÕI ID MỚI TẠO ĐỂ CHUYỂN TRANG --->
    val newId by viewModel.createdReminderId

    LaunchedEffect(newId) {
        if (newId != null) {
            // Chuyển sang màn hình chi tiết
            navController?.navigate("reminder_detail/$newId") {
                // === SỬA LỖI Ở ĐÂY: Dùng đúng chuỗi route đã khai báo trong AppScreen ===
                // Điều này đảm bảo màn hình Form bị xóa khỏi stack
                popUpTo("reminder_form?reminderId={reminderId}") { inclusive = true }
            }
            // Reset lại để tránh lỗi lần sau
            viewModel.createdReminderId.value = null
        }
    }

    // Date Picker Logic
    val datePickerDialog = DatePickerDialog(
        context,
        { _, y, m, d ->
            val selectedDateStr = "%02d/%02d/%04d".format(d, m + 1, y)
            date = selectedDateStr
            dateError = ""
            if (time.isNotEmpty()) {
                if (isPastTime(selectedDateStr, time)) {
                    timeError = "Giờ đã chọn nằm trong quá khứ so với ngày mới"
                } else {
                    timeError = ""
                }
            }
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

    // Time Picker Logic
    val timePickerDialog = TimePickerDialog(
        context,
        { _, h, min ->
            val selectedTimeStr = "%02d:%02d".format(h, min)
            val checkDate = if (date.isNotEmpty()) date else SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            if (isPastTime(checkDate, selectedTimeStr)) {
                time = selectedTimeStr
                timeError = "Không thể chọn thời gian trong quá khứ"
                Toast.makeText(context, "Thời gian đã chọn không hợp lệ", Toast.LENGTH_SHORT).show()
            } else {
                time = selectedTimeStr
                timeError = ""
            }
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (reminderId == null) "Tạo nhắc" else "Cập nhật nhắc", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFC0CB))
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)

            ) {
                // ==== 1. THÚ CƯNG (DROPDOWN MỚI) ====
                Text("Tên thú cưng", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = petName,
                        onValueChange = {}, // Read only
                        readOnly = true,
                        placeholder = { Text("Chọn thú cưng") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(), // Quan trọng: neo menu vào đây
                        shape = RoundedCornerShape(13.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
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
                                        petName = pet.name
                                        petError = ""
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (petError.isNotEmpty()) Text(petError, color = Color.Red, fontSize = 13.sp)

                Spacer(Modifier.height(10.dp))

                // ==== 2. TIÊU ĐỀ ====
                Text("Tiêu đề", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = "" },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(13.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
                if (titleError.isNotEmpty()) Text(titleError, color = Color.Red, fontSize = 13.sp)

                Spacer(Modifier.height(20.dp))

                // ==== 3. LOẠI NHẮC ====
                Text("Loại nhắc", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                val types = listOf("Tiêm phòng", "Tẩy giun", "Tái khám", "Thuốc", "Khác")

                FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
                    types.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type; typeError = "" },
                            label = { Text(type, color = Color.Black) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White,
                                selectedContainerColor = Color(0xFFD5B4F7)
                            ),
                            shape = RoundedCornerShape(15.dp)
                        )
                    }
                }

                if (selectedType == "Khác") {
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = otherTypeInput,
                        onValueChange = { otherTypeInput = it },
                        placeholder = { Text("Nhập loại nhắc cụ thể") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(13.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )
                }
                if (typeError.isNotEmpty()) Text(typeError, color = Color.Red, fontSize = 13.sp)

                Spacer(Modifier.height(10.dp))

                // ==== 4. NGÀY ====
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
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledTextColor = Color.Black,
                            disabledTrailingIconColor = Color.Black
                        )
                    )
                }
                if (dateError.isNotEmpty()) Text(dateError, color = Color.Red, fontSize = 13.sp)

                Spacer(Modifier.height(10.dp))

                // ==== 5. GIỜ ====
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
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledTextColor = Color.Black,
                            disabledTrailingIconColor = Color.Black
                        )
                    )
                }
                if (timeError.isNotEmpty()) Text(timeError, color = Color.Red, fontSize = 13.sp)

                Spacer(Modifier.height(10.dp))

                // ==== 6. CHU KỲ LẶP ====
                Text("Chu kì lặp", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                val repeats = listOf("Không", "Tháng", "Tuần", "Tùy chỉnh")

                FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
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

                if (repeat == "Tùy chỉnh") {
                    Spacer(Modifier.height(10.dp))
                    Text("Nhập chu kì lặp (số ngày)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    OutlinedTextField(
                        value = customRepeat,
                        onValueChange = { customRepeat = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(13.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // ==== 7. NHẮC SỚM ====
                Text("Nhắc sớm", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                val earlyOptions = listOf("Không", "1 giờ", "1 ngày", "3 ngày")

                FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
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

                // ==== 8. GHI CHÚ ====
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

                // ==== 9. BUTTONS ====
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            petName = ""; title = ""; note = ""
                            selectedType = ""; otherTypeInput = ""
                            repeat = "Không"; customRepeat = ""
                            earlyNotify = "Không"
                            date = ""; time = ""
                            petError = ""; titleError = ""; typeError = ""; dateError = ""; timeError = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) { Text("Xóa trắng", color = Color.White) }

                    Button(
                        onClick = { navController?.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3FFB3))
                    ) { Text("Hủy", color = Color.Black) }

                    Button(
                        onClick = {
                            // ==== VALIDATION ====
                            var isValid = true
                            if (petName.isBlank()) { petError = "Vui lòng chọn thú cưng"; isValid = false }
                            if (title.isBlank()) { titleError = "Vui lòng nhập tiêu đề"; isValid = false }
                            if (date.isBlank()) { dateError = "Vui lòng chọn ngày"; isValid = false }
                            if (time.isBlank()) { timeError = "Vui lòng chọn giờ"; isValid = false }

                            if (date.isNotEmpty() && time.isNotEmpty() && isPastTime(date, time)) {
                                timeError = "Thời gian không hợp lệ (quá khứ)"
                                isValid = false
                            }

                            if (selectedType.isBlank()) {
                                typeError = "Vui lòng chọn loại nhắc"; isValid = false
                            } else if (selectedType == "Khác" && otherTypeInput.isBlank()) {
                                typeError = "Vui lòng nhập loại nhắc cụ thể"; isValid = false
                            }

                            if (!isValid) return@Button

                            // ==== SAVE ====
                            val finalType = if (selectedType == "Khác") otherTypeInput.trim() else selectedType
                            val finalRepeat = if (repeat == "Tùy chỉnh") "$customRepeat ngày (Tùy chỉnh)" else repeat
                            val idToSave = reminderId ?: UUID.randomUUID().toString()

                            val reminderToSave = Reminder(
                                id = idToSave,
                                petName = petName, // Lưu tên thú cưng
                                title = title,
                                type = finalType,
                                date = date,
                                time = time,
                                repeat = finalRepeat,
                                earlyNotify = earlyNotify,
                                note = note,
                                status = "Sắp tới"
                            )

                            if (reminderId != null) {
                                viewModel.updateReminder(reminderToSave)
                            } else {
                                viewModel.addReminder(reminderToSave)
                            }
                            // Không gọi popBackStack ở đây nữa, LaunchedEffect ở trên sẽ lo việc chuyển trang
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text(if(reminderId != null) "Cập nhật" else "Lưu", color = Color.White)
                    }
                }
            }
        }
    }
}

// ==== HÀM HELPER GIỮ NGUYÊN ====
fun isPastTime(dateStr: String, timeStr: String): Boolean {
    return try {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateTime = format.parse("$dateStr $timeStr")
        val now = Date()
        dateTime != null && dateTime.before(now)
    } catch (e: Exception) {
        false
    }
}