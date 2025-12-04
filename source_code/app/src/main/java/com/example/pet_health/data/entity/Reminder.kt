package com.example.pet_health.data.entity

import java.util.UUID

data class Reminder(
    val id: String = UUID.randomUUID().toString(), // Tự động tạo ID duy nhất
    val petName: String,
    val title: String,
    val type: String,
    val date: String,
    val time: String,
    val repeat: String,
    val earlyNotify: String,
    val note: String,
    var status: String = "Sắp tới", // Trạng thái: Sắp tới, Hoàn thành, Hoãn lại
    var isDone: Boolean = false
)