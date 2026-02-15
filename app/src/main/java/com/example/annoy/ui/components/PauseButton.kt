package com.example.annoy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PauseButton(
    isPaused: Boolean,
    pauseCountdown: String,
    defaultMinutes: Int,
    onPause: (Int) -> Unit,
    onResume: () -> Unit,
    onDefaultChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isPaused) {
                Text(
                    text = "Paused \u2014 $pauseCountdown remaining",
                    style = MaterialTheme.typography.titleMedium
                )
                Button(
                    onClick = onResume,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Resume")
                }
            } else {
                Text("Pause Duration", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (mins in listOf(5, 15, 30, 60)) {
                        FilterChip(
                            selected = defaultMinutes == mins,
                            onClick = { onDefaultChange(mins) },
                            label = { Text("${mins}m") }
                        )
                    }
                }
                OutlinedButton(
                    onClick = { onPause(defaultMinutes) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pause (${defaultMinutes} min)")
                }
            }
        }
    }
}
