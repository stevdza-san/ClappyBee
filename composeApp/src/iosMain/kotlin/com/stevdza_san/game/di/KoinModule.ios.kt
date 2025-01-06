package com.stevdza_san.game.di

import com.stevdza_san.game.domain.AudioPlayer
import org.koin.dsl.module

actual val targetModule = module {
    single<AudioPlayer> { AudioPlayer() }
}