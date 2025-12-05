package com.example.pet_health.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.pet_health.data.entity.Reminder
import com.example.pet_health.ui.screen.RecordDetailScreen // Giả sử mở màn hình chi tiết khi bấm vào thông báo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Date

class ReminderScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // --- Hàm tính toán thời gian báo thức đã điều chỉnh ---
    private fun calculateAlarmTime(reminder: Reminder): Long {
        // 1. Parse ngày giờ HẸN CUỐI cùng (Due Time)
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dueDateTime = sdf.parse("${reminder.date} ${reminder.time}") ?: return 0L

        // 2. Chuyển sang Calendar để thực hiện phép trừ
        val calendar = Calendar.getInstance().apply { time = dueDateTime }

        // 3. Áp dụng offset dựa trên tùy chọn 'earlyNotify'
        when (reminder.earlyNotify) {
            "1 giờ" -> calendar.add(Calendar.HOUR_OF_DAY, -1)
            "1 ngày" -> calendar.add(Calendar.DAY_OF_YEAR, -1)
            "3 ngày" -> calendar.add(Calendar.DAY_OF_YEAR, -3)
            // "Không" hoặc giá trị khác thì giữ nguyên Due Time
        }

        return calendar.timeInMillis
    }

    // 1. Đặt lịch báo thức mới
    fun scheduleReminder(reminder: Reminder) {
        val adjustedTimeInMillis = calculateAlarmTime(reminder)

        // Kiểm tra xem thời gian đặt lịch có còn hợp lệ (tương lai) không
        if (adjustedTimeInMillis <= System.currentTimeMillis()) {
            Log.w("ReminderScheduler", "Không thể đặt lịch: Thời gian đã trôi qua. Sẽ không đặt báo thức.")
            return
        }

        // Lấy RequestCode duy nhất
        val requestCode = reminder.id.hashCode()

        // Setup Intent: Truyền ID reminder để khi người dùng bấm vào notification,
        // app biết mở màn hình chi tiết nào.
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("TITLE", "NHẮC SỚM (${reminder.earlyNotify}): ${reminder.type}")
            putExtra("MESSAGE", "${reminder.title} cho ${reminder.petName}. Đến hạn: ${reminder.time} ${reminder.date}")
            putExtra("REMINDER_ID", reminder.id) // Truyền ID reminder
            putExtra("ID", requestCode)
        }

        // Setup PendingIntent (FLAG_IMMUTABLE là bắt buộc cho API 31+)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Đặt lịch
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                adjustedTimeInMillis,
                pendingIntent
            )
            val sdfLog = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            Log.d("ReminderScheduler", "Đã đặt lịch thành công lúc: ${sdfLog.format(Date(adjustedTimeInMillis))}")
        } catch (e: SecurityException) {
            Log.e("ReminderScheduler", "Lỗi quyền hạn SCHEDULE_EXACT_ALARM.", e)
        }
    }

    // 2. Hủy lịch báo thức
    fun cancelReminder(reminder: Reminder) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val requestCode = reminder.id.hashCode()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d("ReminderScheduler", "Đã hủy lịch báo thức cho ID: $requestCode")
    }
}