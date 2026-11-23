package com.example.hw2_16

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WaterReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val prefs = applicationContext.getSharedPreferences("water_prefs", Context.MODE_PRIVATE)

    override suspend fun doWork(): Result {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        val count = prefs.getInt("run_count", 0)
        val newCount = count + 1
        prefs.edit {
            putInt("run_count", newCount)
        }

        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "water_channel",
                "Нагадування про воду",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "water_channel")
            .setContentTitle("Трекер води")
            .setContentText("Час випити склянку води! 💧")
            .setSmallIcon(R.drawable.ic_water_drop)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)

        return Result.success()
    }
}
