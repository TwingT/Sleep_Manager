package com.example.sleepmanager.ui

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Calendar
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sleepmanager.App.Companion.prefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyCleControlFirstScreen(
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val calendar = Calendar.getInstance()
    val context = LocalContext.current

    var goalTimeToSleep by remember { mutableStateOf("") }
    var goalTImeToWakeUp by remember { mutableStateOf("") }
    var goalDay by remember { mutableStateOf("") }

    val timePickerDialog1 = TimePickerDialog(
        context,
        { view, hourOfDay, minute ->
            goalTimeToSleep = "${hourOfDay} : ${minute}"
        },
        calendar[Calendar.HOUR_OF_DAY],
        calendar[Calendar.MINUTE],
        true
    )

    val timePickerDialog2 = TimePickerDialog(
        context,
        { view, hourOfDay, minute ->
            goalTImeToWakeUp = "${hourOfDay} : ${minute}"
        },
        calendar[Calendar.HOUR_OF_DAY],
        calendar[Calendar.MINUTE],
        true
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Spacer(modifier = Modifier.width(30.dp))
            Text(text = "목표 취침시각")
            Spacer(modifier = Modifier.width(30.dp))
            Button(onClick = {
                timePickerDialog1.show()
            }) {   // showTimePicker() and more...
                Text(text = goalTimeToSleep)
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Spacer(modifier = Modifier.width(30.dp))
            Text(text = "목표 기상시각")
            Spacer(modifier = Modifier.width(30.dp))
            Button(onClick = {
                timePickerDialog2.show()
            }) {   // showTimePicker() and more...
                Text(text = goalTImeToWakeUp)
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Spacer(modifier = Modifier.width(30.dp))
            Text(text = "목표 일수")
            Spacer(modifier = Modifier.width(15.dp))
            EditNumberField(
                label = "목표 일수",
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                value = goalDay,
                onValueChange = {goalDay = it}
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = {
            prefs.setString("goalTimeToSleep", goalTimeToSleep)
            prefs.setString("goalTimeToWakeUp", goalTImeToWakeUp)
            prefs.setString("goalDay", goalDay)
            prefs.setBoolean("beforeCycleControl",false)
        }) {
            Text(text = "시작")
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNumberField(
    label: String,
    keyboardOptions: KeyboardOptions,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label)},
        singleLine = true,
        keyboardOptions = keyboardOptions,
        modifier = Modifier.width(230.dp)
    )
}

@Preview
@Composable
fun CyCleControlFirstScreenPreview() {
    CyCleControlFirstScreen()
}