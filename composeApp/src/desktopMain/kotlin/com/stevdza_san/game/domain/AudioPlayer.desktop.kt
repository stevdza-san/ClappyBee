package com.stevdza_san.game.domain

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine
import kotlin.concurrent.thread

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AudioPlayer {
    private val audioCache = mutableMapOf<String, ByteArray>()
    private val playingLines = mutableMapOf<String, SourceDataLine>()

    actual fun playGameOverSound() {
        stopFallingSound()
        playSound(fileName = "game_over.wav")
    }

    actual fun playJumpSound() {
        stopFallingSound()
        playSound(fileName = "jump.wav")
    }

    actual fun playFallingSound() {
        playSound(fileName = "falling.wav")
    }

    actual fun stopFallingSound() {
        stopSound(fileName = "falling.wav")
    }

    actual fun playGameSoundInLoop() {
        playSound(fileName = "game_sound.wav", loop = true)
    }

    actual fun stopGameSound() {
        playGameOverSound()
        stopSound(fileName = "game_sound.wav")
    }

    actual fun release() {
        audioCache.clear()
        stopAllSounds()
    }

    private fun playSound(fileName: String, loop: Boolean = false) {
        thread {
            try {
                val audioData = audioCache[fileName] ?: loadAudioFile(fileName).also {
                    audioCache[fileName] = it
                }

                val inputStream = AudioSystem.getAudioInputStream(audioData.inputStream())
                val format = inputStream.format
                val info = DataLine.Info(SourceDataLine::class.java, format)
                val line = AudioSystem.getLine(info) as SourceDataLine

                line.open(format)
                line.start()

                synchronized(playingLines) {
                    playingLines[fileName] = line
                }

                val buffer = ByteArray(4096)
                var bytesRead = 0
                var shouldContinue = true

                if (loop) {
                    while (shouldContinue) {
                        inputStream.reset()
                        while (shouldContinue && inputStream.read(buffer).also { bytesRead = it } !=-1) {
                            synchronized(playingLines) {
                                shouldContinue = playingLines.containsKey(fileName)
                            }
                            if(shouldContinue) {
                                line.write(buffer, 0, bytesRead)
                            }
                        }
                    }
                } else {
                    while (shouldContinue && inputStream.read(buffer).also { bytesRead = it } !=-1) {
                        synchronized(playingLines) {
                            shouldContinue = playingLines.containsKey(fileName)
                        }
                        if(shouldContinue) {
                            line.write(buffer, 0, bytesRead)
                        }
                    }

                    line.drain()
                    line.close()
                    synchronized(playingLines) {
                        playingLines.remove(fileName)
                    }
                }
            } catch (e: Exception) {
                println("Error with playing the audio: $fileName. $e")
            }
        }
    }

    private fun stopSound(fileName: String) {
        synchronized(playingLines) {
            playingLines[fileName]?.let { line ->
                line.stop()
                line.close()
                playingLines.remove(fileName)
            }
        }
    }

    private fun stopAllSounds() {
        synchronized(playingLines) {
            playingLines.values.forEach { line ->
                line.stop()
                line.close()
            }
            playingLines.clear()
        }
    }

    private fun loadAudioFile(fileName: String): ByteArray {
        val resourcePath = Paths.get("src/commonMain/composeResources/files/$fileName")
        if (!Files.exists(resourcePath)) {
            throw FileNotFoundException("Resource not found: $resourcePath")
        }
        return FileInputStream(resourcePath.toFile()).use { it.readBytes() }
    }
}