package com.example.bluehits.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.interpreter.models.Id

@Composable
fun ForBlockSettingsDialog(
    block: BlueBlock,
    onDismiss: () -> Unit,
    onSave: (first: String, last: String, step: String) -> Unit
) {
    var firstValue by remember { mutableStateOf("0") }
    var lastValue by remember { mutableStateOf("10") }
    var stepValue by remember { mutableStateOf("1") }
    var firstError by remember { mutableStateOf(false) }
    var lastError by remember { mutableStateOf(false) }
    var stepError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.padding(DialogStyles.padding),
        title = {
            Text(
                text = "For Block Settings (ID: ${block.id})",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = firstValue,
                    onValueChange = { newValue ->
                        firstValue = newValue
                        firstError = newValue.isNotEmpty() && newValue.toIntOrNull() == null
                    },
                    label = { Text("First") },
                    isError = firstError,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .fillMaxWidth(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)

                )
                OutlinedTextField(
                    value = lastValue,
                    onValueChange = { newValue ->
                        lastValue = newValue
                        lastError = newValue.isNotEmpty() && newValue.toIntOrNull() == null
                    },
                    label = { Text("Last") },
                    isError = lastError,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .fillMaxWidth(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                )
                OutlinedTextField(
                    value = stepValue,
                    onValueChange = { newValue ->
                        stepValue = newValue
                        stepError = newValue.isNotEmpty() && newValue.toIntOrNull() == null
                    },
                    label = { Text("Step") },
                    isError = stepError,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .fillMaxWidth(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (!firstError && !lastError && !stepError && firstValue.isNotEmpty() && lastValue.isNotEmpty() && stepValue.isNotEmpty()) {
                        onSave(firstValue, lastValue, stepValue)
                        onDismiss()
                    }
                }
            ) {
                Text(
                    text = "Save",
                    fontSize = DialogStyles.buttonTextSize
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Close",
                    fontSize = DialogStyles.buttonTextSize
                )
            }
        }
    )
}