package com.example.sleepmanager.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sleepmanager.App.Companion.prefs

@Composable
fun CycleControlSecondScreen(
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "이미 사이클 조절 기능을 사용중입니다.", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(50.dp))
        Text(text = "목표 :   ${prefs.getString("goalTimeToSleep","")} ~ ${prefs.getString("goalTimeToWakeUp","")}", fontSize = 30.sp)
        Spacer(modifier = Modifier.height(80.dp))
        Button(onClick = {prefs.setBoolean("beforeCycleControl",true)}) {
            Text(text = "중단")
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Preview
@Composable
fun CycleControlSeccondScreenPreview() {
    CycleControlSecondScreen(
    )
}