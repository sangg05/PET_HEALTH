package com.example.pet_health.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pet_health.R
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.navigation.NavController
import com.example.pet_health.ui.viewmodel.YourViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountActionsScreen(
    navController: NavController,
    viewModel: YourViewModel,
    onBack: () -> Unit = {},
    onUpdateInfo: () -> Unit = {},
    onUpdateAvatar: () -> Unit = {},
    onChangePassword: () -> Unit = {}
) {
    val background = Color(0xFFF3CCE4)
    val cardColor = Color.White

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý tài khoản", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF3CCE4))
            )
        },
        containerColor = background
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // --------- Cập nhật thông tin ---------
            AccountActionCard(
                iconId = R.drawable.ic_edit,
                text = "Cập nhật thông tin",
                onClick = onUpdateInfo
            )

            // --------- Đổi ảnh đại diện ---------
            AccountActionCard(
                iconId = R.drawable.ic_edit,
                text = "Đổi ảnh đại diện",
                onClick = onUpdateAvatar
            )

            // --------- Đổi mật khẩu ---------
            AccountActionCard(
                iconId = R.drawable.ic_edit,
                text = "Đổi mật khẩu",
                onClick = onChangePassword
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --------- Đăng xuất ---------
            Card(
                modifier = Modifier
                    .width(180.dp)
                    .height(55.dp)
                    .clickable {
                        // Logout user và điều hướng về login
                        viewModel.logoutOnly()
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE5A8C8)),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logout),
                        contentDescription = "Đăng xuất",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "ĐĂNG XUẤT",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AccountActionCard(iconId: Int, text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = text,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}
