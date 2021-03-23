package com.lehaine.ldtkbase

import com.lehaine.ldtk.*
import com.soywiz.kds.iterators.fastForEachReverse
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tiles.TileSet
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.bitmap.BitmapSlice
import com.soywiz.korim.bitmap.sliceWithSize
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs

suspend inline fun Container.ldtkMapView(
    level: Level,
    renderIntGridLayer: Boolean = false,
    debugEntities: Boolean = false,
    callback: LDtkMapView.() -> Unit = {}
) {
    level.loadAsync()
    val tileSets = level.loadTileSets()
    val bgImage = if (level.hasBgImage) resourcesVfs[level.bgImageInfos!!.relFilePath].readBitmap() else null
    val slice = bgImage?.let {
        val cropRect = level.bgImageInfos!!.cropRect
        it.sliceWithSize(cropRect.x.toInt(), cropRect.y.toInt(), cropRect.w.toInt(), cropRect.h.toInt())

    }
    LDtkMapView(level, tileSets, slice, renderIntGridLayer, debugEntities).addTo(this, callback)
}

class LDtkMapView(
    val level: Level,
    val tileSets: Map<Int, TileSet>,
    val bgImage: BitmapSlice<Bitmap>? = null,
    val renderIntGridLayers: Boolean = false,
    val debugEntities: Boolean = false
) : Container() {

    init {
        require(level.isLoaded()) { "Level is not loaded! Please make sure level is loaded before creating an LDtkMapView" }

        if (bgImage != null) {
            sprite(texture = bgImage)
        }
        level.allUntypedLayers.fastForEachReverse { layer ->
            val view: View = when (layer) {
                is LayerTiles -> ldtkLayer(layer, tileSets[layer.tileset.json.uid])
                is LayerAutoLayer -> ldtkLayer(layer, tileSets[layer.tileset.json.uid])
                is LayerIntGridAutoLayer -> ldtkLayer(layer, tileSets[layer.tileset.json.uid])
                is LayerIntGrid -> {
                    if (renderIntGridLayers) {
                        ldtkLayer(layer, null)
                    }
                    dummyView()
                }
                is LayerEntities -> {
                    if (debugEntities) {
                        layer.entities.forEach { entity ->
                            level.project.defs?.let { defs ->
                                defs.entities.find { it.uid == entity.json.defUid }?.let {
                                    solidRect(entity.width, entity.height, Colors[it.color])
                                        .xy(
                                            entity.pixelX - entity.width * it.pivotX,
                                            entity.pixelY - entity.height * it.pivotY
                                        )
                                }
                            }
                        }
                    }
                    dummyView()
                }
                else -> dummyView()
            }
            view.visible(true)
                .name(layer.identifier.takeIf { it.isNotEmpty() })
                .xy(layer.pxTotalOffsetX, layer.pxTotalOffsetY)
        }
    }
}