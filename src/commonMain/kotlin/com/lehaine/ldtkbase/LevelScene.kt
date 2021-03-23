package com.lehaine.ldtkbase

import com.lehaine.ldtkbase.ldtk.ldtkMapView
import com.lehaine.ldtkbase.ldtk.toLDtkLevel
import com.soywiz.korev.Key
import com.soywiz.korge.input.keys
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korim.atlas.readAtlas
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korio.file.std.resourcesVfs
import kotlin.math.roundToInt

class LevelScene(val world: World, val levelIdx: Int = 0) : Scene() {

    override suspend fun Container.sceneInit() {
        val worldLevel = world.allLevels[levelIdx]
        val ldtkLevel = worldLevel.toLDtkLevel()
        val atlas = resourcesVfs["tiles.atlas.json"].readAtlas()

        container {
            val playerData = worldLevel.layerEntities.allPlayer[0]
            ldtkMapView(ldtkLevel)
            val hero = hero(
                playerData.pixelX.toDouble(),
                playerData.pixelY.toDouble(),
                atlas
            ) {
                anchor(playerData.pivotX, playerData.pivotY)
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
                if (views.input.keys[Key.ESCAPE]) stage?.gameWindow?.close()

                hero.update(dt)

                accumulator += dt.millisecondsInt
                if (accumulator > 200) {
                    fpsText.text = "FPS: ${(1 / dt.seconds).roundToInt()}"
                    accumulator = 0
                }
            }
        }
    }
}