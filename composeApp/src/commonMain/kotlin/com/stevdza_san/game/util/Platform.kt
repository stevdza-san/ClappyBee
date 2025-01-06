package com.stevdza_san.game.util

enum class Platform {
    Android,
    iOS,
    Desktop,
    Web
}

expect fun getPlatform(): Platform