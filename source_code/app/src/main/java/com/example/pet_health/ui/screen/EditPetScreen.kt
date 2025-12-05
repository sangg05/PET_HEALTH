package com.example.pet_health.ui.screen

import BottomBar
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.pet_health.ui.viewmodel.PetViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPetScreen(
    navController: NavController,
    petViewModel: PetViewModel,
    editMode: Boolean = false,
    petId: String? = null,
    initName: String = "",
    initType: String = "",
    initAge: String = "",
    initColor: String = "",
    initWeight: String = "",
    initHeight: String = "",
    initAdoptionDate: String = "",
    initImageUri: String? = null
) {
    // States cho các trường nhập liệu
    var name by remember { mutableStateOf(initName) }
    var type by remember { mutableStateOf(initType) }
    var age by remember { mutableStateOf(initAge) }
    var color by remember { mutableStateOf(initColor) }
    var weight by remember { mutableStateOf(initWeight) }
    var height by remember { mutableStateOf(initHeight) }
    var adoptionDate by remember { mutableStateOf(initAdoptionDate) }

    // State cho ảnh - quan trọng!
    var imageUri by remember {
        mutableStateOf<Uri?>(
            if (!initImageUri.isNullOrEmpty()) Uri.parse(initImageUri) else null
        )
    }

    // Lưu URL ảnh ban đầu để biết ảnh cũ
    val originalImageUrl = remember { initImageUri ?: "" }

    val lightPink = Color(0xFFFFB3D9)
    val context = LocalContext.current

    // Gallery launcher - cập nhật cả imageUri
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa thú cưng", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = com.example.pet_health.ui.screens.lightPink)
            )
        },
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(Brush.verticalGradient(listOf(Color(0xFFFFF6C2), Color(0xFFFFD6EC), Color(0xFFEAD6FF))))
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            var showImagePickerDialog by remember { mutableStateOf(false) }

            // Ảnh thú cưng
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .background(Color.LightGray)
                    .clickable {
                        showImagePickerDialog = true
                    },
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
                    onGalleryClick = { galleryLauncher.launch("image/*") }
                )
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
                PetInputField(
                    label = "Cân nặng (kg)",
                    value = weight,
                    onValueChange = { weight = it },
                    modifier = Modifier.weight(1f)
                )
                PetInputField(
                    label = "Kích thước (cm)",
                    value = height,
                    onValueChange = { height = it },
                    modifier = Modifier.weight(1f)
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

            // Nút Cập nhật/Thêm
            Button(
                onClick = {
                    if (name.isNotEmpty() && type.isNotEmpty()) {
                        // Tính birthDate từ age
                        val birthDateMillis = if(age.isNotEmpty()) {
                            val years = age.toIntOrNull() ?: 0
                            Calendar.getInstance().apply {
                                add(Calendar.YEAR, -years)
                            }.timeInMillis
                        } else 0L

                        // Tính adoptionDate
                        val adoptionTimestamp = if(adoptionDate.isNotEmpty()) {
                            val parts = adoptionDate.split("/") // dd/MM/yyyy
                            if(parts.size == 3) {
                                Calendar.getInstance().apply {
                                    set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
                                }.timeInMillis
                            } else null
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
                            imageUri = imageUri,              // Uri hiện tại (có thể là mới hoặc cũ)
                            existingImageUrl = originalImageUrl, // URL ảnh ban đầu
                            editMode = editMode,
                            petId = petId
                        ) {
                            navController.popBackStack()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Tên và loài không được để trống",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81F39F)),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth(0.5f).height(48.dp)
            ) {
                Text(
                    if(editMode) "Cập nhật" else "Thêm",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
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
    isDateField: Boolean = false
) {
    val context = LocalContext.current

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
        readOnly = isDateField,
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