package com.example.hw2_16

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.TimeUnit

data class WaterUiState(
    val isReminderEnabled: Boolean = false,
    val runCount: Int = 0
)

class WaterViewModel(application: Application) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)

    private val prefs = application.getSharedPreferences("water_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        WaterUiState(
            isReminderEnabled = prefs.getBoolean(KEY_ENABLED, false),
            runCount = prefs.getInt(KEY_RUN_COUNT, 0)
        )
    )
    val uiState: StateFlow<WaterUiState> = _uiState.asStateFlow()

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        if (key == KEY_RUN_COUNT) {
            _uiState.update { it.copy(runCount = sp.getInt(KEY_RUN_COUNT, 0)) }
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(prefsListener)
    }

    fun startReminder(intervalMinutes: Long) {
        val safeInterval = if (intervalMinutes < 15) 15L else intervalMinutes

        val request = PeriodicWorkRequestBuilder<WaterReminderWorker>(
            safeInterval, TimeUnit.MINUTES
        ).build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
        setEnabled(true)
    }

    fun stopReminder() {
        workManager.cancelUniqueWork(WORK_NAME)
        setEnabled(false)
    }

    private fun setEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_ENABLED, enabled) }
        _uiState.update { it.copy(isReminderEnabled = enabled) }
    }

    override fun onCleared() {
        prefs.unregisterOnSharedPreferenceChangeListener(prefsListener)
    }

    companion object {
        const val WORK_NAME = "water_reminder"
        private const val KEY_ENABLED = "reminder_enabled"
        private const val KEY_RUN_COUNT = "run_count"
    }
}
