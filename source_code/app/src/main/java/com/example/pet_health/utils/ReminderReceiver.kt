package com.example.pet_health.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.pet_health.MainActivity // Giả sử MainActivity nằm ở thư mục gốc
// import com.example.pet_health.R // Bạn cần import R nếu dùng icon tùy chỉnh

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Lấy thông tin từ Intent do Scheduler gửi
        val title = intent.getStringExtra("TITLE") ?: "Nhắc nhở thú cưng"
        val message = intent.getStringExtra("MESSAGE") ?: "Đã đến giờ chăm sóc thú cưng của bạn!"
        val requestCode = intent.getIntExtra("ID", 0)
        val reminderId = intent.getStringExtra("REMINDER_ID")

        Log.d("ReminderReceiver", "Báo thức kích hoạt! ID: $requestCode, Reminder ID: $reminderId")

        showNotification(context, title, message, requestCode)
    }

    private fun showNotification(context: Context, title: String, message: String, id: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "pet_health_reminder_channel"
        val channelName = "Nhắc lịch thú cưng"

        // 1. Tạo Notification Channel (Bắt buộc cho Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Thông báo nhắc nhở lịch tiêm, uống thuốc..."
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Tạo Intent để mở MainActivity khi người dùng bấm vào thông báo
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            // Xóa hết Activity trên stack và mở Activity mới (MainActivity)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Nếu bạn muốn mở thẳng màn hình chi tiết, bạn cần pass URI navigation phức tạp hơn
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Cấu hình giao diện thông báo
        val notification = NotificationCompat.Builder(context, channelId)
            // Dùng icon hệ thống an toàn để đảm bảo compile
            .setSmallIcon(android.R.drawable.ic_dialog_info)
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