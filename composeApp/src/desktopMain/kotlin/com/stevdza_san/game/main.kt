package com.stevdza_san.game

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.stevdza_san.game.di.initializeKoin

fun main() = application {
    initializeKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "ClappyBee",
        state = WindowState(
            width = 1200.dp,
            height = 800.dp
        ),
        resizable = false
    ) {
        App()
    }
}