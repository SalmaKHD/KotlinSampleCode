package com.salmakhd.android.forpractice.KotlinFlows

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = viewModel<MainViewModel>()
            val value = viewModel.stateFlow.collectAsState(initial = 10)
            Box(modifier = Modifier.fillMaxSize()) {
                Button(onClick = { viewModel.incrementCounter() }) {
                    Text(text = "Counter: ${value.value}")
                }
            }
        }
    }
}