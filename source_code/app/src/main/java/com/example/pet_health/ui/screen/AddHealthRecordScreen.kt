package com.example.pet_health.ui.screens

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHealthRecordScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var symptoms by remember { mutableStateOf("") }
    var prescription by remember { mutableStateOf("") }
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
                    text = "Tên thú cưng",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    textAlign = TextAlign.Center
                )
                // === Các trường nhập liệu ===
                PetInputField(
                    label = "Ngày khám",
                    value = date,
                    onValueChange = { date = it },
                    trailingIcon = Icons.Default.DateRange,
                    isDateField = true // bật DatePicker
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
                        // Lưu dữ liệu và quay lại trang hồ sơ
                        onClick = {
                            val recordData = mapOf(
                                "date" to date,
                                "symptoms" to symptoms,
                                "prescription" to prescription
                            )
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("new_record", recordData)
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
