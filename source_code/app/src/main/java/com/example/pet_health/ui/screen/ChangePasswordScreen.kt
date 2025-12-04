package com.example.pet_health.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pet_health.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit = {},
    onPasswordChanged: () -> Unit = {}
) {
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }

    var showOld by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    var message by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đổi mật khẩu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE5A8C8))
            )
        },
        containerColor = Color(0xFFF3CCE4)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mật khẩu cũ
            OutlinedTextField(
                value = oldPass,
                onValueChange = { oldPass = it },
                label = { Text("Mật khẩu cũ") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showOld) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showOld = !showOld }) {
                        Icon(
                            if (showOld) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle password"
                        )
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            // Mật khẩu mới
            OutlinedTextField(
                value = newPass,
                onValueChange = { newPass = it },
                label = { Text("Mật khẩu mới") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showNew) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showNew = !showNew }) {
                        Icon(
                            if (showNew) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle password"
                        )
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            // Xác nhận mật khẩu
            OutlinedTextField(
                value = confirmPass,
                onValueChange = { confirmPass = it },
                label = { Text("Xác nhận mật khẩu") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(
                            if (showConfirm) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle password"
                        )
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            // Hiển thị message
            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = if (message.contains("thành công")) Color.Green else Color.Red,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Nút cập nhật
            Button(
                onClick = {
                    scope.launch {
                        when {
                            newPass != confirmPass -> {
                                message = "Mật khẩu mới và xác nhận không khớp!"
                                return@launch
                            }
                            newPass.length < 6 -> {
                                message = "Mật khẩu mới phải >= 6 ký tự!"
                                return@launch
                            }
                        }

                        val user = auth.currentUser
                        if (user?.email == null) {
                            message = "Người dùng chưa đăng nhập!"
                            return@launch
                        }

                        loading = true
                        message = ""

                        val credential = EmailAuthProvider.getCredential(user.email!!, oldPass)
                        user.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
                            if (reAuthTask.isSuccessful) {
                                user.updatePassword(newPass).addOnCompleteListener { updateTask ->
                                    loading = false
                                    if (updateTask.isSuccessful) {
                                        message = "Đổi mật khẩu thành công!"
                                        onPasswordChanged()

                                        // Quay về trang trước
                                        scope.launch {
                                            delay(1000)
                                            onBack()
                                        }

                                    } else {
                                        message = updateTask.exception?.message ?: "Lỗi khi đổi mật khẩu"
                                    }
                                }
                            } else {
                                loading = false
                                message = "Mật khẩu cũ không đúng!"
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5A8C8)),
                enabled = !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Cập nhật mật khẩu", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
