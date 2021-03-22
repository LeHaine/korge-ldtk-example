import com.lehaine.ldtkbase.World
import com.lehaine.ldtkbase.ldtkMapView
import com.soywiz.klock.milliseconds
import com.soywiz.kmem.clamp
import com.soywiz.korev.Key
import com.soywiz.korge.Korge
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tiles.TileSet
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import kotlin.math.pow
import kotlin.math.roundToInt

suspend fun main() =
    Korge(
        width = 1920,
        height = 1080,
        virtualWidth = 480,
        virtualHeight = 270,
        bgcolor = Colors["#2b2b2b"]
    ) {

        val world = World().apply { loadAsync() }
        var levelIdx = 0
        val level = world.allLevels[levelIdx].apply { loadAsync() }

        val tiles = resourcesVfs["Cavernas_by_Adam_Saltsman.png"].readBitmap().toBMP32IfRequired()
        val slices = TileSet.extractBitmaps(tiles, 8, 8, 12, 12 * 32, 0, 0)
        val tileSet = TileSet.fromBitmaps(8, 8, slices, 1)

        container {
            val camera = camera {
                ldtkMapView(level, tileSet, true)
            }

            val fpsText = text("FPS: ...")
                .apply { smoothing = false }
                .alignTopToTopOf(this)
                .alignLeftToLeftOf(this)

            var dx = 0.0
            var dy = 0.0
            var accumulator = 0
            addUpdater { dt ->
                val scale = if (dt == 0.milliseconds) 0.0 else (dt / 16.666666.milliseconds)
                if (views.input.keys[Key.D]) dx -= 1.0
                if (views.input.keys[Key.A]) dx += 1.0
                if (views.input.keys[Key.W]) dy += 1.0
                if (views.input.keys[Key.S]) dy -= 1.0
                if (views.input.keys[Key.KP_1]) levelIdx = 0
                if (views.input.keys[Key.KP_2]) levelIdx = 1
                if (views.input.keys[Key.KP_3]) levelIdx = 2
                if (views.input.keys[Key.KP_4]) levelIdx = 3
                if (views.input.keys[Key.ESCAPE]) gameWindow.close()

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