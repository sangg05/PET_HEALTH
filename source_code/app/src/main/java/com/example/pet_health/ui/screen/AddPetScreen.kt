package com.example.pet_health.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    navController: NavController,
    editMode: Boolean = false, // true nếu sửa
    initName: String = "",
    initType: String = "",
    initAge: String = "",
    initColor: String = "",
    initWeight: String = "",
    initHeight: String = "",
    initAdoptionDate: String = "",
    initImageUri: String? = null
) {
    var name by remember { mutableStateOf(initName) }
    var type by remember { mutableStateOf(initType) }
    var age by remember { mutableStateOf(initAge) }
    var color by remember { mutableStateOf(initColor) }
    var weight by remember { mutableStateOf(initWeight) }
    var height by remember { mutableStateOf(initHeight) }
    var adoptionDate by remember { mutableStateOf(initAdoptionDate) }
    var petImageUri by remember { mutableStateOf(initImageUri) }

    val lightPink = Color(0xFFFFB3D9)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if(editMode) "Chỉnh sửa thú cưng" else "Thêm thú cưng",
                    fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = lightPink)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().height(60.dp).background(Color.White),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Home, "Trang chủ", tint = Color(0xFF6200EE), modifier = Modifier.size(32.dp))
                Icon(Icons.Default.Notifications, "Thông báo", tint = Color.LightGray, modifier = Modifier.size(32.dp))
                Icon(Icons.Default.Person, "Hồ sơ", tint = Color.LightGray, modifier = Modifier.size(32.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(Brush.verticalGradient(listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))))
            .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Ảnh thú cưng
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(60.dp))
                    .clickable { /* TODO: mở camera/gallery chọn ảnh */ },
                contentAlignment = Alignment.Center
            ) {
                if (petImageUri != null) {
                    // TODO: load ảnh từ URI bằng Coil hoặc Glide
                    Text("Ảnh đã chọn")
                } else {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Chọn ảnh", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Các trường nhập liệu
            PetInputField(label = "Tên", value = name, onValueChange = { name = it })
            Spacer(modifier = Modifier.height(16.dp))
            PetInputField(label = "Loài", value = type, onValueChange = { type = it })
            Spacer(modifier = Modifier.height(16.dp))
            PetInputField(label = "Tuổi", value = age, onValueChange = { age = it })
            Spacer(modifier = Modifier.height(16.dp))
            PetInputField(label = "Màu sắc", value = color, onValueChange = { color = it })
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PetInputField(label = "Cân nặng (kg)", value = weight, onValueChange = { weight = it }, modifier = Modifier.weight(1f))
                PetInputField(label = "Kích thước (cm)", value = height, onValueChange = { height = it }, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            PetInputField(
                label = "Ngày nhận nuôi",
                value = adoptionDate,
                onValueChange = { adoptionDate = it },
                trailingIcon = Icons.Default.CalendarToday,
                isDateField = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Nút Cập nhật/Thêm
            Button(
                onClick = {
                    // TODO: xử lý lưu dữ liệu (thêm hoặc update)
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81F39F)),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth(0.5f).height(48.dp)
            ) {
                Text(if(editMode) "Cập nhật" else "Thêm", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PetInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = Icons.Default.Edit,
    isDateField: Boolean = false // Thêm flag để nhận biết DatePicker
) {
    val context = LocalContext.current

    // DatePickerDialog
    val calendar = remember { java.util.Calendar.getInstance() }
    val datePickerDialog = remember(context) {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formattedDate = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                onValueChange(formattedDate)
            },
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        )
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = isDateField, // nếu là ngày thì không cho nhập thủ công
        label = {
            Text(
                text = label,
                color = Color(0xFF6E6E6E),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        },
        modifier = modifier.fillMaxWidth(),
        trailingIcon = trailingIcon?.let { icon ->
            {
                IconButton(onClick = {
                    if (isDateField) {
                        datePickerDialog.show()
                    }
                }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFFCE3CCB)
                    )
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFFCE3CCB),
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color(0xFFCE3CCB),
            unfocusedLabelColor = Color(0xFF6E6E6E)
        )
    )
}
