package com.lehaine.ldtkbase

import com.lehaine.ldtkbase.ldtk.ldtkMapView
import com.lehaine.ldtkbase.ldtk.toLDtkLevel
import com.soywiz.klock.milliseconds
import com.soywiz.kmem.clamp
import com.soywiz.korev.Key
import com.soywiz.korge.input.keys
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korio.async.launchImmediately
import kotlin.math.pow
import kotlin.math.roundToInt

class LevelScene(val world: World, val levelIdx: Int = 0) : Scene() {

    override suspend fun Container.sceneInit() {
        val level = world.allLevels[levelIdx].toLDtkLevel()

        container {
            val camera = camera {
                ldtkMapView(level, renderIntGridLayer = true, debugEntities = true)
            }

            val fpsText = text("FPS: ...")
                .apply { smoothing = false }
                .alignTopToTopOf(this)
                .alignLeftToLeftOf(this)

            var dx = 0.0
            var dy = 0.0
            var accumulator = 0

            keys {
                down(Key.N) {
                    launchImmediately {
                        if (levelIdx < world.allUntypedLevels.lastIndex) {
                            sceneContainer.changeTo<LevelScene>(world, levelIdx + 1)
                        }
                    }
                }
                down(Key.B) {
                    launchImmediately {
                        if (levelIdx > 0) {
                            sceneContainer.changeTo<LevelScene>(world, levelIdx - 1)
                        }
                    }
                }

            }
            addUpdater { dt ->
                val scale = if (dt == 0.milliseconds) 0.0 else (dt / 16.666666.milliseconds)
                if (views.input.keys[Key.D]) dx -= 1.0
                if (views.input.keys[Key.A]) dx += 1.0
                if (views.input.keys[Key.W]) dy += 1.0
                if (views.input.keys[Key.S]) dy -= 1.0
                if (views.input.keys[Key.ESCAPE]) stage?.gameWindow?.close()

                dx = dx.clamp(-10.0, +10.0)
                dy = dy.clamp(-10.0, +10.0)
                camera.x += dx * scale
                camera.y += dy * scale
                dx *= 0.9.pow(scale)
                dy *= 0.9.pow(scale)

                accumulator += dt.millisecondsInt
                if (accumulator > 200) {
                    fpsText.text = "FPS: ${(1 / dt.seconds).roundToInt()}"
                    accumulator = 0
                }
            }
        }
    }
}