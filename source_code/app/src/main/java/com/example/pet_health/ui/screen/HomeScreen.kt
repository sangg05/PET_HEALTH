package com.example.pet_health.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pet_health.R

private val darkPink = Color(0xFFD81B60)

@Composable
fun HomeScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val logoSize = 50.dp
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color.White),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Home, contentDescription = "Trang chủ", tint = Color(0xFF6200EE), modifier = Modifier.size(32.dp))
                Icon(Icons.Default.Notifications, contentDescription = "Thông báo", tint = Color.LightGray, modifier = Modifier.size(32.dp))
                Icon(Icons.Default.Person, contentDescription = "Hồ sơ", tint = Color.LightGray, modifier = Modifier.size(32.dp))
            }
        }
    ) { paddingValues->
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ===== Banner + Header =====
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.banner2),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 20.dp, top = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HELLO, Bạn!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Image(
                            painter = painterResource(id = R.drawable.ic_user),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.95f))
                                .clickable { },
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // ===== Box menu =====
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .shadow(14.dp, RoundedCornerShape(36.dp))
                        .clip(RoundedCornerShape(36.dp))
                        .background(Color(0xFFFFF1F1))
                        .padding(
                            top = logoSize / 2 + 12.dp,
                            bottom = 24.dp,
                            start = 18.dp,
                            end = 18.dp
                        )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Pet Health Icon",
                            modifier = Modifier
                                .size(logoSize)
                                .offset(y = (-logoSize) / 2)
                                .zIndex(10f),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            FeatureButton("Hồ sơ\nsức khỏe", R.drawable.ic_health, Color(0xFFD32F2F)) {}
                            FeatureButton("Nhắc lịch", R.drawable.ic_clock, Color(0xFFFF8F00)) {}
                            FeatureButton("Theo dõi\nsức khỏe", R.drawable.ic_bell, Color(0xFFFFC107)) {}
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            FeatureButton("Sổ tiêm\n& thuốc điện tử", R.drawable.ic_pill, Color(0xFFF06292)) {}
                            FeatureButton("Danh sách\nthú cưng", R.drawable.ic_paw, Color(0xFFCE93D8))
                            {    navController.navigate("pet_list") }
                            Spacer(modifier = Modifier.width(80.dp))
                        }
                    }
                }

                // ===== News Section =====
                NewsSection()

                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

// ===== FeatureButton =====
@Composable
fun FeatureButton(
    title: String,
    iconRes: Int,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable { onClick() }
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
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

// ===== NewsSection =====
@Composable
fun NewsSection() {
    val context = LocalContext.current

    val newsList = listOf(
        Triple("https://www.truoopetcare.com/siteassets/shutterstock_519978535.jpg?format=webp&mode=crop&width=750&height=402",
            "https://www.truoopetcare.com/tin-tuc/cho-va-meo-cung/cach-cham-soc-cho-cung-trong-mua-dong",
            "Cách chăm sóc chó vào mùa đông"),
        Triple("https://helloconsen.com/wp-content/uploads/2024/05/dog-being-lathered-in-a-bathtub.jpg",
            "https://helloconsen.com/bi-quyet-tam-cho-cho-an-toan-va-hieu-qua?srsltid=AfmBOoogQxHL1AbvtbaydzxsGg6rmxIMjD2f1-54WMrBXakqJW-bImqu",
            "Cách tắm cho thú cưng an toàn"),
        Triple("https://tuyensinhthuy.com/assets/uploads/news/images/chon-thuc-an-cho-thu-cung-01.jpg",
            "https://tuyensinhthuy.com/nhung-dieu-ban-can-luu-y-khi-lua-chon-thuc-an-cho-thu-cung-n54.html",
            "Những lưu ý khi chọn thức ăn cho thú cưng")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Tips & Tin tức",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(newsList) { item ->
                val (imageUrl, articleUrl, title) = item
                Card(
                    modifier = Modifier
                        .width(200.dp)
                        .height(120.dp)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl))
                            context.startActivity(intent)
                        },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = title,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.4f))
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = title,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
