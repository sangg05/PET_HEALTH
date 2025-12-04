package com.example.pet_health.ui.screen

import android.R.attr.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pet_health.R
import com.example.pet_health.data.repository.UserRepository
import com.example.pet_health.ui.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateInfoScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    onSave: () -> Unit = {}
) {

    val user by userViewModel.userInfo

    var name by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }


// Khi user info load xong, cập nhật state
    LaunchedEffect(userViewModel.userInfo.value) {
        val user = userViewModel.userInfo.value
        if (user != null) {
            name = user.name ?: ""
            birthday = user.birthDate ?: ""
            gender = user.gender ?: "Nữ"
            phone = user.phone
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Cập nhật thông tin",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF3CCE4))
            )
        },
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
                    var birthday by remember { mutableStateOf(user?.birthDate ?: "") }

                    OutlinedTextField(
                        value = birthday,
                        onValueChange = { input ->
                            // Chỉ lấy số
                            var digits = input.filter { it.isDigit() }
                            if (digits.length > 8) digits = digits.take(8)

                            // Tách từng phần, không thêm số 0
                            val dayPart = digits.take(2)
                            val monthPart = digits.drop(2).take(2)
                            val yearPart = digits.drop(4).take(4)

                            // Build format
                            val formatted = buildString {
                                if (dayPart.isNotEmpty()) append(dayPart)
                                if (monthPart.isNotEmpty()) append("/$monthPart")
                                if (yearPart.isNotEmpty()) append("/$yearPart")
                            }

                            // Kiểm tra hợp lệ nếu đủ 8 số
                            var warning = ""
                            if (digits.length == 8) {
                                val day = dayPart.toIntOrNull() ?: 0
                                val month = monthPart.toIntOrNull() ?: 0
                                val year = yearPart.toIntOrNull() ?: 0

                                val validMonth = month in 1..12
                                val validDay = when (month) {
                                    1,3,5,7,8,10,12 -> day in 1..31
                                    4,6,9,11 -> day in 1..30
                                    2 -> day in 1..29
                                    else -> false
                                }
                                val validYear = year in 1900..2025

                                if (!validDay || !validMonth || !validYear) warning = " ⚠"
                            }

                            birthday = formatted + warning
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
            val scope = rememberCoroutineScope()

            Box(
                modifier = Modifier
                    .width(170.dp)
                    .height(50.dp)
                    .background(Color(0xFFE5A8C8), RoundedCornerShape(25.dp))
                    .clickable {
                        scope.launch {
                            userViewModel.updateUserInfo(
                                name = name,
                                birthDate = birthday,
                                gender = gender,
                                phone=phone,
                            )
                            onSave()
                            navController.popBackStack()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("LƯU", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

