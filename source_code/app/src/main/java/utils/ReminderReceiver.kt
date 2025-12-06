package com.example.pet_health.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.pet_health.MainActivity
import com.example.pet_health.data.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val title = intent.getStringExtra("TITLE") ?: "Nhắc nhở thú cưng"
        val message = intent.getStringExtra("MESSAGE") ?: "Đã đến giờ chăm sóc thú cưng của bạn!"
        val requestCode = intent.getIntExtra("ID", 0)
        val reminderId = intent.getStringExtra("REMINDER_ID")

        Log.d("ReminderReceiver", "Báo thức kích hoạt! ID: $requestCode, Reminder ID: $reminderId")

        //Tạo repository
        val repo = NotificationRepository(context)

        // Lưu notification vào Room + Firestore
        CoroutineScope(Dispatchers.IO).launch {
            if (reminderId != null) {
                repo.saveNotification(reminderId, title, message)
            }
        }

        //  Hiển thị thông báo hệ thống
        showNotification(context, title, message, requestCode)
    }

    private fun showNotification(context: Context, title: String, message: String, id: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "pet_health_reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Nhắc lịch thú cưng",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id, notification)
    }
}
