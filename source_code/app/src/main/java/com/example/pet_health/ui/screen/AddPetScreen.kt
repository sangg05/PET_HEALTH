package com.example.pet_health.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AddPetScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var adoptionDate by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFD6EC), Color(0xFFEAD6FF))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // === Thanh tiêu đề ===
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
                Text(
                    text = "Thêm thú cưng",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp),
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // === Các trường nhập liệu ===
            PetInputField(label = "Tên", value = name, onValueChange = { name = it })
            PetInputField(label = "Loài", value = type, onValueChange = { type = it })
            PetInputField(label = "Tuổi", value = age, onValueChange = { age = it })
            PetInputField(label = "Màu sắc", value = color, onValueChange = { color = it })

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
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

            PetInputField(
                label = "Ngày nhận nuôi",
                value = adoptionDate,
                onValueChange = { adoptionDate = it },
                trailingIcon = Icons.Default.CalendarToday
            )

            Spacer(modifier = Modifier.height(20.dp))

            // === Nút Cập nhật ===
            Button(
                onClick = {
                    // TODO: Lưu dữ liệu nếu có DB hoặc DataStore
                    navController.popBackStack() // Quay lại danh sách thú cưng
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB2F0C0)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Cập nhật", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PetInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = Icons.Default.Edit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        trailingIcon = {
            if (trailingIcon != null) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = "icon",
                    tint = Color.Gray
                )
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFCE3CCB),
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color(0xFFCE3CCB)
        )
    )
}
