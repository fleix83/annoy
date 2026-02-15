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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScheduleCard(
    scheduleMode: String,
    startHour: Int,
    startMinute: Int,
    endHour: Int,
    endMinute: Int,
    activeDays: Set<String>,
    onModeChange: (String) -> Unit,
    onStartTimeChange: (Int, Int) -> Unit,
    onEndTimeChange: (Int, Int) -> Unit,
    onDaysChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Schedule", style = MaterialTheme.typography.titleMedium)

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = scheduleMode == "always",
                    onClick = { onModeChange("always") }
                )
                Text("Always on", modifier = Modifier.padding(end = 16.dp))
                RadioButton(
                    selected = scheduleMode == "scheduled",
                    onClick = { onModeChange("scheduled") }
                )
                Text("Scheduled")
            }

            if (scheduleMode == "scheduled") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { showStartPicker = true }) {
                        Text(
                            text = formatTime(startHour, startMinute),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Text("\u2014", style = MaterialTheme.typography.bodyLarge)
                    TextButton(onClick = { showEndPicker = true }) {
                        Text(
                            text = formatTime(endHour, endMinute),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Day chips
                val allDays = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (day in allDays) {
                        FilterChip(
                            selected = day in activeDays,
                            onClick = {
                                val newDays = if (day in activeDays) activeDays - day else activeDays + day
                                if (newDays.isNotEmpty()) onDaysChange(newDays)
                            },
                            label = { Text(day.take(3)) }
                        )
                    }
                }

                if (showStartPicker) {
                    TimePickerDialog(
                        initialHour = startHour,
                        initialMinute = startMinute,
                        onConfirm = { h, m -> onStartTimeChange(h, m); showStartPicker = false },
                        onDismiss = { showStartPicker = false }
                    )
                }
                if (showEndPicker) {
                    TimePickerDialog(
                        initialHour = endHour,
                        initialMinute = endMinute,
                        onConfirm = { h, m -> onEndTimeChange(h, m); showEndPicker = false },
                        onDismiss = { showEndPicker = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var hour by remember { mutableStateOf(initialHour) }
    var minute by remember { mutableStateOf(initialMinute) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Time") },
        text = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                NumberPicker(value = hour, range = 0..23, onValueChange = { hour = it })
                Text(" : ", style = MaterialTheme.typography.headlineMedium)
                NumberPicker(value = minute, range = 0..59, step = 5, onValueChange = { minute = it })
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(hour, minute) }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun NumberPicker(
    value: Int,
    range: IntRange,
    step: Int = 1,
    onValueChange: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(onClick = {
            val next = value + step
            onValueChange(if (next > range.last) range.first else next)
        }) { Text("\u25B2") }
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.headlineMedium
        )
        TextButton(onClick = {
            val prev = value - step
            onValueChange(if (prev < range.first) range.last else prev)
        }) { Text("\u25BC") }
    }
}

private fun formatTime(hour: Int, minute: Int): String {
    return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}
