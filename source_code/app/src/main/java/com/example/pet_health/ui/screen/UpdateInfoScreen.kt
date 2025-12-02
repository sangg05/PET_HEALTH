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

    var gender by remember { mutableStateOf("Nữ") }

    var name by remember { mutableStateOf("Lê Thị B") }
    var birthday by remember { mutableStateOf("") }

    var editingField by remember { mutableStateOf<String?>(null) }

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

            Spacer(Modifier.height(30.dp))

            Card(
                colors = CardDefaults.cardColors(Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // ====== HÀM ROW CHỈNH SỬA ======
                    @Composable
                    fun EditableRow(
                        title: String,
                        fieldKey: String,
                        value: String,
                        placeholder: String,
                        onValueChange: (String) -> Unit
                    ) {

                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            if (editingField == fieldKey) {
                                // === TEXTFIELD EDIT MODE ===
                                OutlinedTextField(
                                    value = value,
                                    onValueChange = onValueChange,
                                    placeholder = { Text(placeholder, color = Color.Gray) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                // === NORMAL MODE ===
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        text = "$title:",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Text(
                                        text = if (value.isEmpty()) placeholder else value,
                                        color = if (value.isEmpty()) Color.Gray else Color.Black,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }

                                Icon(
                                    painter = painterResource(R.drawable.ic_edit),
                                    contentDescription = "edit",
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clickable { editingField = fieldKey }
                                )
                            }
                        }
                    }

                    // ====== TÊN ======
                    EditableRow(
                        title = "Tên",
                        fieldKey = "name",
                        value = name,
                        placeholder = "Nhập tên...",
                        onValueChange = { name = it }
                    )

                    Spacer(Modifier.height(14.dp))

                    // ====== NGÀY SINH ======
                    EditableRow(
                        title = "Ngày sinh",
                        fieldKey = "birthday",
                        value = birthday,
                        placeholder = "dd/mm/yyyy",
                        onValueChange = { birthday = it },
                    )

                    Spacer(Modifier.height(20.dp))

                    // ====== GIỚI TÍNH ======
                    Text("Giới tính:", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        // ==== NAM ====
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { gender = "Nam" }
                        ) {
                            Text("Nam", fontSize = 15.sp)
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
                                        contentDescription = "",
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.width(40.dp))

                        // ==== NỮ ====
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { gender = "Nữ" }
                        ) {
                            Text("Nữ", fontSize = 15.sp)
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
                                        contentDescription = "",
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(30.dp))

            // ==== NÚT LƯU ====
            Box(
                modifier = Modifier
                    .width(170.dp)
                    .height(50.dp)
                    .background(Color(0xFFE5A8C8), RoundedCornerShape(25.dp))
                    .clickable {
                        editingField = null
                        onSave()
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_save),
                        contentDescription = "Save",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("LƯU", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
