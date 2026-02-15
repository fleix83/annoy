package com.example.annoy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeterrentConfigCard(
    deterrentMode: String,
    soundSelection: String,
    vibrationPattern: String,
    intervalSeconds: Int,
    volumePercent: Int,
    gracePeriodSeconds: Int,
    onModeChange: (String) -> Unit,
    onSoundChange: (String) -> Unit,
    onVibrationChange: (String) -> Unit,
    onIntervalChange: (Int) -> Unit,
    onVolumeChange: (Int) -> Unit,
    onGracePeriodChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Deterrent", style = MaterialTheme.typography.titleMedium)

            // Mode selector
            Text("Mode", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (mode in listOf("sound" to "Sound", "vibration" to "Vibration", "both" to "Both")) {
                    FilterChip(
                        selected = deterrentMode == mode.first,
                        onClick = { onModeChange(mode.first) },
                        label = { Text(mode.second) }
                    )
                }
            }

            // Sound selector
            if (deterrentMode != "vibration") {
                Text("Sound", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (sound in listOf(
                        "mosquito" to "Mosquito",
                        "hum" to "Low Hum",
                        "beep" to "Beep",
                        "ticking" to "Ticking"
                    )) {
                        FilterChip(
                            selected = soundSelection == sound.first,
                            onClick = { onSoundChange(sound.first) },
                            label = { Text(sound.second) }
                        )
                    }
                }
            }

            // Vibration pattern selector
            if (deterrentMode != "sound") {
                Text("Vibration", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (pattern in listOf(
                        "single_pulse" to "Single Pulse",
                        "double_tap" to "Double Tap",
                        "long_buzz" to "Long Buzz"
                    )) {
                        FilterChip(
                            selected = vibrationPattern == pattern.first,
                            onClick = { onVibrationChange(pattern.first) },
                            label = { Text(pattern.second) }
                        )
                    }
                }
            }

            // Interval
            Text("Interval", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (secs in listOf(10, 15, 30, 60)) {
                    FilterChip(
                        selected = intervalSeconds == secs,
                        onClick = { onIntervalChange(secs) },
                        label = { Text("${secs}s") }
                    )
                }
            }

            // Volume slider
            if (deterrentMode != "vibration") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volume", style = MaterialTheme.typography.labelLarge)
                    Slider(
                        value = volumePercent.toFloat(),
                        onValueChange = { onVolumeChange(it.roundToInt()) },
                        valueRange = 10f..50f,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                            .semantics { contentDescription = "Volume slider" }
                    )
                    Text("${volumePercent}%", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Grace period
            Text("Grace Period", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (secs in listOf(0, 3, 5, 10)) {
                    FilterChip(
                        selected = gracePeriodSeconds == secs,
                        onClick = { onGracePeriodChange(secs) },
                        label = { Text(if (secs == 0) "None" else "${secs}s") }
                    )
                }
            }
        }
    }
}
