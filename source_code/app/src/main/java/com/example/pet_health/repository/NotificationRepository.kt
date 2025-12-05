package com.example.pet_health.data.repository

import com.example.pet_health.data.entity.NotificationEntity
import com.example.pet_health.data.local.dao.NotificationDao

class NotificationRepository(private val dao: NotificationDao) {

    fun getNotifications() = dao.getAll()

    suspend fun insert(notification: NotificationEntity) {
        dao.insert(notification)
    }

    suspend fun markAsRead(id: Int) {
        dao.markAsRead(id)
    }
}