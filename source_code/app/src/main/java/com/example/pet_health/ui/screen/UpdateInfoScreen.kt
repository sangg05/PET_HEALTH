package com.example.pet_health.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pet_health.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateInfoScreen(
    onBack: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    val background = Color(0xFFF3CCE4)

    var name by remember { mutableStateOf("Lê Thị B") }
    var birthday by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Nữ") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cập nhật thông tin", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE5A8C8)
                )
            )
        },
        containerColor = background
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ========= CARD =========
            Card(
                colors = CardDefaults.cardColors(Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // ======== KHUNG TÊN =========
                    Text("Tên", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nhập tên") },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_edit),
                                contentDescription = "edit",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))


                    // ======== KHUNG NGÀY SINH =========
                    Text("Ngày sinh", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = birthday,
                        onValueChange = onValueChange@{ input ->
                            // Lấy số
                            var digits = input.filter { it.isDigit() }

                            // Giới hạn 8 số (ddMMyyyy)
                            if (digits.length > 8) digits = digits.take(8)

                            // Chưa đủ 8 số → chỉ hiển thị số người dùng nhập
                            if (digits.length < 8) {
                                birthday = digits
                                return@onValueChange
                            }

                            // Đủ 8 số → format
                            val day = digits.substring(0, 2).toIntOrNull() ?: 0
                            val month = digits.substring(2, 4).toIntOrNull() ?: 0
                            val year = digits.substring(4, 8).toIntOrNull() ?: 0

                            // Kiểm tra tháng
                            val validMonth = month in 1..12

                            // Kiểm tra ngày theo tháng
                            val validDay = when (month) {
                                1,3,5,7,8,10,12 -> day in 1..31
                                4,6,9,11 -> day in 1..30
                                2 -> day in 1..29
                                else -> false
                            }

                            // Kiểm tra năm
                            val validYear = year in 1900..2025

                            val formatted = "${digits.substring(0,2)}/${digits.substring(2,4)}/${digits.substring(4,8)}"

                            birthday = if (validDay && validMonth && validYear) {
                                formatted
                            } else {
                                "$formatted ⚠"
                            }
                        },
                        label = { Text("dd/mm/yyyy") },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_edit),
                                contentDescription = "edit",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(22.dp))


                    // ========= GIỚI TÍNH =========
                    Text("Giới tính", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(Color(0xFFFAFAFA)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // ===== NAM =====
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { gender = "Nam" }
                            ) {
                                Text("Nam", fontSize = 16.sp)
                                Spacer(Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .border(2.dp, Color.Gray, RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (gender == "Nam") {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            // ===== NỮ =====
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { gender = "Nữ" }
                            ) {
                                Text("Nữ", fontSize = 16.sp)
                                Spacer(Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .border(2.dp, Color.Gray, RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (gender == "Nữ") {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(30.dp))

            // ====== BUTTON LƯU ======
            Box(
                modifier = Modifier
                    .width(170.dp)
                    .height(50.dp)
                    .background(Color(0xFFE5A8C8), RoundedCornerShape(25.dp))
                    .clickable { onSave() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_save),
                        contentDescription = "Save",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("LƯU", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
