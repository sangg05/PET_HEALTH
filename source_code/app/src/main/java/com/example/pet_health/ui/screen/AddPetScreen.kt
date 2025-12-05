package com.example.pet_health.ui.screens

import BottomBar
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.pet_health.ui.screen.PetImagePicker
import com.example.pet_health.ui.viewmodel.PetViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    navController: NavController,
    petViewModel: PetViewModel
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var adoptionDate by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Error states
    var nameError by remember { mutableStateOf("") }
    var typeError by remember { mutableStateOf("") }
    var ageError by remember { mutableStateOf("") }
    var weightError by remember { mutableStateOf("") }
    var heightError by remember { mutableStateOf("") }

    val context = LocalContext.current
    val lightPink = Color(0xFFFFB3D9)

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { imageUri = it } }

    var showImagePickerDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thêm thú cưng", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = com.example.pet_health.ui.screen.lightPink)
            )
        },
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))))
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Ảnh thú cưng
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .background(Color.LightGray)
                    .clickable { showImagePickerDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Ảnh thú cưng",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Chọn ảnh", tint = Color.White)
                }
            }

            if (showImagePickerDialog) {
                PetImagePicker(
                    onImageSelected = { uriString ->
                        imageUri = Uri.parse(uriString)
                    },
                    onDismiss = { showImagePickerDialog = false },
                    onGalleryClick = {
                        galleryLauncher.launch("image/*")
                        showImagePickerDialog = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tên (bắt buộc)
            PetInputField(
                label = "Tên ",
                value = name,
                onValueChange = {
                    name = it
                    nameError = if (it.isEmpty()) "Tên không được để trống" else ""
                },
                errorMessage = nameError
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Loài (bắt buộc)
            PetInputField(
                label = "Loài ",
                value = type,
                onValueChange = {
                    type = it
                    typeError = if (it.isEmpty()) "Loài không được để trống" else ""
                },
                errorMessage = typeError
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Tuổi (chỉ số, 0-50)
            PetInputField(
                label = "Tuổi",
                value = age,
                onValueChange = {
                    if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 0..50)) {
                        age = it
                        ageError = ""
                    } else {
                        ageError = "Tuổi phải từ 0-50"
                    }
                },
                keyboardType = KeyboardType.Number,
                errorMessage = ageError
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Màu sắc
            PetInputField(
                label = "Màu sắc",
                value = color,
                onValueChange = { color = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Cân nặng và Kích thước
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Cân nặng (0-200 kg)
                PetInputField(
                    label = "Cân nặng (kg)",
                    value = weight,
                    onValueChange = {
                        if (it.isEmpty() || (it.toDoubleOrNull() != null && it.toDouble() in 0.0..200.0)) {
                            weight = it
                            weightError = ""
                        } else {
                            weightError = "0-200kg"
                        }
                    },
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Decimal,
                    errorMessage = weightError
                )

                // Kích thước (0-500 cm)
                PetInputField(
                    label = "Kích thước (cm)",
                    value = height,
                    onValueChange = {
                        if (it.isEmpty() || (it.toDoubleOrNull() != null && it.toDouble() in 0.0..500.0)) {
                            height = it
                            heightError = ""
                        } else {
                            heightError = "0-500cm"
                        }
                    },
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Decimal,
                    errorMessage = heightError
                )
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

            Button(
                onClick = {
                    // Validate trước khi submit
                    var hasError = false

                    if (name.isEmpty()) {
                        nameError = "Tên không được để trống"
                        hasError = true
                    }
                    if (type.isEmpty()) {
                        typeError = "Loài không được để trống"
                        hasError = true
                    }

                    if (hasError) {
                        Toast.makeText(context, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val birthDateMillis = if(age.isNotEmpty()) {
                        val years = age.toIntOrNull() ?: 0
                        Calendar.getInstance().apply { add(Calendar.YEAR, -years) }.timeInMillis
                    } else 0L

                    val adoptionTimestamp = if(adoptionDate.isNotEmpty()) {
                        val parts = adoptionDate.split("/")
                        if(parts.size == 3) Calendar.getInstance().apply {
                            set(parts[2].toInt(), parts[1].toInt()-1, parts[0].toInt())
                        }.timeInMillis else null
                    } else null

                    petViewModel.addOrUpdatePet(
                        context = context,
                        name = name,
                        species = type,
                        birthDate = birthDateMillis,
                        color = color,
                        weightKg = weight.toDoubleOrNull() ?: 0.0,
                        sizeCm = height.toDoubleOrNull(),
                        adoptionDate = adoptionTimestamp,
                        imageUri = imageUri,
                        existingImageUrl = null,
                        editMode = false,
                        petId = null
                    ) {
                        navController.popBackStack()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81F39F)),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth(0.5f).height(48.dp)
            ) {
                Text("Thêm", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
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
    isDateField: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    errorMessage: String = ""
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val datePickerDialog = remember(context) {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formattedDate = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                onValueChange(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = isDateField,
            label = { Text(label, color = Color(0xFF6E6E6E), fontSize = 16.sp, fontWeight = FontWeight.Medium) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = trailingIcon?.let { icon ->
                {
                    IconButton(onClick = { if(isDateField) datePickerDialog.show() }) {
                        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFFCE3CCB))
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedBorderColor = if (errorMessage.isNotEmpty()) Color.Red else Color(0xFFCE3CCB),
                unfocusedBorderColor = if (errorMessage.isNotEmpty()) Color.Red else Color.Gray,
                focusedLabelColor = if (errorMessage.isNotEmpty()) Color.Red else Color(0xFFCE3CCB),
                unfocusedLabelColor = if (errorMessage.isNotEmpty()) Color.Red else Color(0xFF6E6E6E)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = errorMessage.isNotEmpty()
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}