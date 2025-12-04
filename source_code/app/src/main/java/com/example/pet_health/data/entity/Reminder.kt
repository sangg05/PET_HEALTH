package com.example.pet_health.data.entity

data class Reminder(
    val id: String = "", // Cần giá trị mặc định
    val petName: String = "",
    val title: String = "",
    val type: String = "",
    val date: String = "",
    val time: String = "",
    val repeat: String = "",
    val earlyNotify: String = "",
    val note: String = "",
    var status: String = "Sắp tới",
    var isDone: Boolean = false
)