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
import com.example.pet_health.data.entity.NotificationEntity // Import Entity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pet_health.data.local.AppDatabase
import java.util.concurrent.TimeUnit // Thêm import này

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val title = intent.getStringExtra("TITLE") ?: "Nhắc nhở thú cưng"
        val message = intent.getStringExtra("MESSAGE") ?: "Đã đến giờ chăm sóc thú cưng của bạn!"
        val requestCode = intent.getIntExtra("ID", 0)
        // Lấy ID thật của Reminder từ Scheduler
        val reminderId = intent.getStringExtra("REMINDER_ID")

        Log.d("ReminderReceiver", "Báo thức kích hoạt! ID: $requestCode, Reminder ID: $reminderId")

        // 1. Lưu thông báo vào DB
        saveNotificationToDB(context, reminderId, title, message)

        // 2. Hiển thị thông báo hệ thống
        showNotification(context, title, message, requestCode)
    }

    // Hàm lưu vào Room DB
    private fun saveNotificationToDB(context: Context, reminderId: String?, title: String, message: String) {
        // Sử dụng ApplicationContext để lấy DB an toàn
        val appContext = context.applicationContext
        val db = AppDatabase.getDatabase(appContext)
        val dao = db.notificationDao()

        // Thực hiện DB operation trên CoroutineScope IO
        CoroutineScope(Dispatchers.IO).launch {
            dao.insert(
                NotificationEntity(
                    // id sẽ tự autoGenerate
                    reminderId = reminderId ?: "", // Gán ID thật (hoặc rỗng nếu không xác định)
                    title = title,
                    message = message,
                    timestamp = System.currentTimeMillis(),
                    isRead = false
                )
            )
            Log.d("ReminderReceiver", "Đã lưu Notification vào DB cho Reminder ID: $reminderId")
        }
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
            // Optional: Để mở thẳng màn hình chi tiết, bạn cần dùng DeepLink URI,
            // nhưng hiện tại ta sẽ mở MainActivity
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