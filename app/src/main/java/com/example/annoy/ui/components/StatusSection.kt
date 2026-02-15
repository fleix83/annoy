package com.example.annoy.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatusSection(
    isEnabled: Boolean,
    isPaused: Boolean,
    pauseCountdown: String,
    isScheduled: Boolean,
    isInWindow: Boolean,
    nextActiveTime: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            val statusText = when {
                !isEnabled -> "Disabled"
                isPaused -> "Paused \u2014 $pauseCountdown remaining"
                isScheduled && !isInWindow -> "Scheduled \u2014 resumes at $nextActiveTime"
                else -> "Active"
            }
            Text(
                text = "Status: $statusText",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
