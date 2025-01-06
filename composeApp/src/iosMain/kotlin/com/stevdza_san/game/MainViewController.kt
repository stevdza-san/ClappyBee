package com.stevdza_san.game

import androidx.compose.ui.window.ComposeUIViewController
import com.stevdza_san.game.di.initializeKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin() }
) { App() }