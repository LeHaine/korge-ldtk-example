import com.lehaine.ldtkbase.World
import com.lehaine.ldtkbase.ldtkMapView
import com.soywiz.klock.milliseconds
import com.soywiz.kmem.clamp
import com.soywiz.korev.Key
import com.soywiz.korge.Korge
import com.soywiz.korge.view.addUpdater
import com.soywiz.korge.view.camera
import com.soywiz.korge.view.container
import com.soywiz.korge.view.tiles.TileSet
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import kotlin.math.pow

suspend fun main() =
    Korge(
        width = 1920,
        height = 1080,
        virtualWidth = 480,
        virtualHeight = 270,
        bgcolor = Colors["#2b2b2b"]
    ) {

        val world = World().apply { loadAsync() }
        val level = world.allLevels[0].apply { loadAsync() }
        val tiles = resourcesVfs["Cavernas_by_Adam_Saltsman.png"].readBitmap().toBMP32IfRequired()
        val slices = TileSet.extractBitmaps(tiles, 8, 8, 12, 12 * 32, 0, 0)
        val tileSet = TileSet.fromBitmaps(8, 8, slices, 1)

        container {
            val camera = camera {
                ldtkMapView(level, tileSet)
            }
            var dx = 0.0
            var dy = 0.0
            addUpdater {
                val scale = if (it == 0.milliseconds) 0.0 else (it / 16.666666.milliseconds)
                if (views.input.keys[Key.D]) dx -= 1.0
                if (views.input.keys[Key.A]) dx += 1.0
                if (views.input.keys[Key.W]) dy += 1.0
                if (views.input.keys[Key.S]) dy -= 1.0
                if (views.input.keys[Key.ESCAPE]) gameWindow.close()

                dx = dx.clamp(-10.0, +10.0)
                dy = dy.clamp(-10.0, +10.0)
                camera.x += dx * scale
                camera.y += dy * scale
                dx *= 0.9.pow(scale)
                dy *= 0.9.pow(scale)
            }
        }
    }