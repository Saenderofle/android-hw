package com.example.hw2_15

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class NewsSyncWorker(
    context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result{
        return try {
            Log.d("WorkManager", "Починаємо синхронізацію...")

            val token = inputData.getString("user_token")
            delay(3000)
            val out = Data.Builder().putString("result", "завантажено 15 новин").build()

            Log.d("WorkManager", "Синхронізація успішна! token=$token")

            Result.success(out)

        } catch (e: Exception) {
            Log.e("WorkManager", "Помилка мережі", e)

            Result.retry()
        }
    }
}
