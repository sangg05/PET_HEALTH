package com.example.pet_health.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.pet_health.R
import com.example.pet_health.MainActivity // Import MainActivity của bạn

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("TITLE") ?: "Nhắc nhở thú cưng"
        val message = intent.getStringExtra("MESSAGE") ?: "Đã đến giờ chăm sóc thú cưng của bạn!"
        val reminderId = intent.getIntExtra("ID", 0)

        showNotification(context, title, message, reminderId)
    }

    private fun showNotification(context: Context, title: String, message: String, id: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "pet_health_reminder_channel"

        // 1. Tạo Notification Channel (Bắt buộc cho Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Nhắc lịch thú cưng",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Thông báo nhắc nhở lịch tiêm, uống thuốc..."
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Tạo Intent để khi bấm vào thông báo sẽ mở App
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, id, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Cấu hình giao diện thông báo
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Đảm bảo bạn có icon này hoặc thay bằng icon khác
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Bấm vào là tự tắt thông báo
            .build()

        // 4. Hiển thị
        notificationManager.notify(id, notification)
    }
}