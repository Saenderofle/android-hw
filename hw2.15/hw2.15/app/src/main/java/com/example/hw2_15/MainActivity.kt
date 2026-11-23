package com.example.hw2_15

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.hw2_15.ui.theme.Hw2_15Theme
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {

    val context = this@MainActivity

    private val syncStatus = MutableStateFlow("Очікує...")

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val status by syncStatus.collectAsState()
            Hw2_15Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Синхронізація новин",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        )
                    }
                ) { innerPadding ->
                    SyncButtons(
                        onSyncNowClick = {
                            val constraints = Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build()

                            val syncRequest = OneTimeWorkRequestBuilder<NewsSyncWorker>()
                                .setInputData(Data.Builder().putString("user_token", "abc123").build())
                                .setConstraints(constraints)
                                .build()

                            WorkManager.getInstance(context).enqueue(syncRequest)

                            WorkManager.getInstance(context)
                                .getWorkInfoByIdLiveData(syncRequest.id)
                                .observe(this) { workInfo ->
                                    if (workInfo != null) {
                                        syncStatus.value = when (workInfo.state) {
                                            WorkInfo.State.ENQUEUED -> "Очікує..."
                                            WorkInfo.State.RUNNING -> "Виконується"
                                            WorkInfo.State.SUCCEEDED -> {
                                                val message = workInfo.outputData.getString("result")
                                                "Успіх: $message"
                                            }
                                            else -> workInfo.state.name
                                        }
                                    }
                                }
                        },
                        onNightSyncClick = {
                            val constraints = Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.UNMETERED)
                                .setRequiresCharging(true)
                                .build()

                            val syncRequest = PeriodicWorkRequestBuilder<NewsSyncWorker>(15, TimeUnit.MINUTES)
                                .setConstraints(constraints)
                                .setInputData(
                                    Data.Builder().putString("user_token", "abc123").build()
                                )
                                .build()

                            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                                "night_news_sync",
                                ExistingPeriodicWorkPolicy.KEEP,
                                syncRequest
                            )
                        },
                        status = status,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SyncButtons(
    onSyncNowClick: () -> Unit,
    onNightSyncClick: () -> Unit,
    status: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "WORKMANAGER STATUS",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = status,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Button(
            onClick = onSyncNowClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Синхронізувати зараз",
                style = MaterialTheme.typography.titleMedium
            )
        }

        FilledTonalButton(
            onClick = onNightSyncClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Увімкнути нічну синхронізацію",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SyncButtonsPreview() {
    Hw2_15Theme {
        SyncButtons(onSyncNowClick = {}, onNightSyncClick = {}, status = "Очікує...")
    }
}
