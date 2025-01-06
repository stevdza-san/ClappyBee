package com.stevdza_san.game

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.stevdza_san.game.di.initializeKoin
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initializeKoin()
    ComposeViewport(document.body!!) {
        App()
    }
}