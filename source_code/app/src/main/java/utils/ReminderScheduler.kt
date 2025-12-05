package com.example.pet_health.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.pet_health.data.entity.Reminder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReminderScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleReminder(reminder: Reminder) {
        // 1. Chuyển đổi ngày giờ String sang Milliseconds
        val timeInMillis = parseDateTimeToMillis(reminder.date, reminder.time)

        if (timeInMillis <= System.currentTimeMillis()) {
            Log.w("ReminderScheduler", "Không thể đặt lịch cho quá khứ")
            return
        }

        // 2. Tạo Intent gửi tới Receiver
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("TITLE", "Nhắc lịch: ${reminder.type}")
            putExtra("MESSAGE", "${reminder.title} cho ${reminder.petName}")
            putExtra("ID", reminder.id.hashCode()) // Dùng hashCode của String ID làm ID thông báo
        }

        // 3. Tạo PendingIntent
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(), // RequestCode phải unique cho mỗi reminder
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 4. Đặt lịch (Exact Alarm - Chính xác)
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
            Log.d("ReminderScheduler", "Đã đặt lịch lúc: ${reminder.time} ${reminder.date}")
        } catch (e: SecurityException) {
            Log.e("ReminderScheduler", "Lỗi quyền hạn: ", e)
        }
    }

    // Hàm hủy lịch (Dùng khi xóa reminder)
    fun cancelReminder(reminder: Reminder) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun parseDateTimeToMillis(date: String, time: String): Long {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dateObj = sdf.parse("$date $time")
            dateObj?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}