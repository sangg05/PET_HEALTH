package com.example.pet_health

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pet_health.ui.navigation.AppScreen
import com.example.pet_health.ui.theme.Pet_HealthTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Pet_HealthTheme {
                AppScreen()   // <-- gọi navigation ở đây
            }
        }
    }
}

@Composable
fun FilterChipStyled(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.Companion
            .border(
                width = 1.5.dp,
                color = if (selected) Color(0xFF7B1FA2) else Color.Companion.Gray,
                shape = RoundedCornerShape(20.dp)
            )
            .background(
                color = if (selected) Color(0xFFD7A7E5) else Color.Companion.White,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text,
            fontSize = 14.sp,
            color = if (selected) Color.Companion.Black else Color.Companion.DarkGray,
            fontWeight = FontWeight.Companion.Medium
        )
    }
}