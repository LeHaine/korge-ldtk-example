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


class LevelScene(val world: World, val levelIdx: Int = 0) : Scene() {

    override suspend fun Container.sceneInit() {
        val worldLevel = world.allLevels[levelIdx]
        val ldtkLevel = worldLevel.toLDtkLevel()
        val atlas = resourcesVfs["tiles.atlas.json"].readAtlas()

        container {
            val playerData = worldLevel.layerEntities.allPlayer[0]
            ldtkMapView(ldtkLevel)
            hero(
                playerData.pixelX.toDouble(),
                playerData.pixelY.toDouble(),
                atlas
            ) {
                anchor(playerData.pivotX, playerData.pivotY)
            }

            fpsLabel {
                alignTopToTopOf(this)
                alignLeftToLeftOf(this)
            }

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
            addUpdater {
                if (views.input.keys[Key.ESCAPE]) stage?.gameWindow?.close()
            }
        }
    }
}