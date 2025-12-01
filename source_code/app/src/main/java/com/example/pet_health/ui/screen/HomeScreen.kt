package com.example.pet_health.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pet_health.R

@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF6C2),
                        Color(0xFFFFD6EC),
                        Color(0xFFEAD6FF)
                    )
                )
            )
    ) {
        // Avatar góc phải (tài khoản)
        Image(
            painter = painterResource(id = R.drawable.ic_user),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.TopEnd)
                .padding(top = 25.dp, end = 18.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.95f))
                .shadow(8.dp, CircleShape)
                .clickable { /* TODO: mở màn hình tài khoản */ },
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 85.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // === Sticker Welcome ===
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sticker_welcom),
                    contentDescription = "Welcome Sticker",
                    modifier = Modifier
                        .size(130.dp)
                        .offset(x = (-10).dp, y = 4.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // === Hàng mèo - chó ===
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sticker_cat),
                    contentDescription = "Cat Sticker",
                    modifier = Modifier
                        .size(110.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = 10.dp, y = 4.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.sticker_star),
                    contentDescription = "Star",
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (-8).dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.sticker_dog),
                    contentDescription = "Dog Sticker",
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = (-16).dp, y = 6.dp)
                )
            }

            // === Logo chính giữa ===
            Spacer(modifier = Modifier.height(0.dp))
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pet_logo),
                    contentDescription = "Pet Health Logo",
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "PET HEALTH",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFCE3CCB)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // === Menu chức năng ===
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .shadow(14.dp, RoundedCornerShape(36.dp))
                    .clip(RoundedCornerShape(36.dp))
                    .background(Color(0xFFFFF1F1))
                    .padding(vertical = 24.dp, horizontal = 18.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        FeatureButton(
                            "Hồ sơ\nsức khỏe",
                            R.drawable.ic_health,
                            Color(0xFFE57373)
                        ) { navController.navigate("health_record") }

                        FeatureButton("Nhắc lịch", R.drawable.ic_clock, Color(0xFFFFB74D)) {}
                        FeatureButton("Theo dõi\nsức khỏe", R.drawable.ic_bell, Color(0xFFFFD54F)) {}
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        FeatureButton("Sổ tiêm\n& thuốc điện tử", R.drawable.ic_pill, Color(0xFFF48FB1)) {}

                        // Liên kết trang Danh sách thú cưng
                        FeatureButton("Danh sách\nthú cưng", R.drawable.ic_paw, Color(0xFFCE93D8)) {
                            navController.navigate("pet_list")
                        }
                    }
                }
            }
        }

        // === Thanh navigation dưới cùng ===
        NavigationBar(
            containerColor = Color(0xFFFFD1DC),
            tonalElevation = 6.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(100.dp)
        ) {
            NavigationBarItem(
                selected = true,
                onClick = {},
                icon = {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Trang chủ",
                            tint = Color(0xFFCE3CCB)
                        )
                    }
                },
                label = { Text("Trang chủ", fontSize = 13.sp) }
            )

            NavigationBarItem(
                selected = false,
                onClick = {},
                icon = {
                    Icon(
                        painterResource(id = R.drawable.ic_bell),
                        contentDescription = "Nhắc lịch",
                        tint = Color.Black
                    )
                },
                label = { Text("Nhắc lịch", fontSize = 13.sp) }
            )

            NavigationBarItem(
                selected = false,
                onClick = {
                    navController.navigate("account")
                },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.ic_user),
                        contentDescription = "Tài khoản",
                        tint = Color.Black
                    )
                },
                label = { Text("Tài khoản", fontSize = 13.sp) }
            )

        }
    }
}

// Hàm FeatureButton
@Composable
fun FeatureButton(
    title: String,
    iconRes: Int,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(65.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}
