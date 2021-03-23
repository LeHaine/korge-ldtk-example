package com.lehaine.ldtkbase

import com.lehaine.ldtkbase.ldtk.ldtkMapView
import com.lehaine.ldtkbase.ldtk.toLDtkLevel
import com.soywiz.klock.milliseconds
import com.soywiz.kmem.clamp
import com.soywiz.korev.Key
import com.soywiz.korge.input.keys
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korim.atlas.readAtlas
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korio.file.std.resourcesVfs
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

class LevelScene(val world: World, val levelIdx: Int = 0) : Scene() {

    override suspend fun Container.sceneInit() {
        val worldLevel = world.allLevels[levelIdx]
        val ldtkLevel = worldLevel.toLDtkLevel()
        val atlas = resourcesVfs["tiles.atlas.json"].readAtlas()

        val heroIdle = atlas.getSpriteAnimation("heroIdle", defaultTimePerFrame = 450.milliseconds)
        val heroRun = atlas.getSpriteAnimation("heroRun", defaultTimePerFrame = 150.milliseconds)


        container {
            val playerData = worldLevel.layerEntities.allPlayer[0]
            ldtkMapView(ldtkLevel)
            val hero = Sprite(heroIdle).apply {
                smoothing = false
                anchor(playerData.pivotX, playerData.pivotY)
                xy(
                    playerData.pixelX,
                    playerData.pixelY
                )
                playAnimationLooped()
            }.addTo(this)

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
            val speed = 0.1
            addUpdater { dt ->
                val scale = if (dt == 0.milliseconds) 0.0 else (dt / 16.666666.milliseconds)
                if (views.input.keys[Key.D]) {
                    dx += speed
                    hero.scaleX = 1.0
                }
                if (views.input.keys[Key.A]) {
                    dx -= speed
                    hero.scaleX = -1.0
                }
                if (views.input.keys[Key.ESCAPE]) stage?.gameWindow?.close()

                dx = dx.clamp(-1.5, +1.5)
                dy = dy.clamp(-1.5, +1.5)
                hero.x += dx * scale
                dx *= 0.9.pow(scale)
                dy *= 0.9.pow(scale)
                if (abs(dx) <= 0.08) {
                    dx = 0.0
                }

                if (dx == 0.0) {
                    hero.playAnimationLooped(heroIdle)
                } else {
                    hero.playAnimationLooped(heroRun)
                }

                accumulator += dt.millisecondsInt
                if (accumulator > 200) {
                    fpsText.text = "FPS: ${(1 / dt.seconds).roundToInt()}"
                    accumulator = 0
                }
            }
        }
    }
}