package com.example.sleepmanager.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HowToUseScreen(
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
}

@Preview
@Composable
fun HowToUseScreenPreview() {
    HowToUseScreen()
}