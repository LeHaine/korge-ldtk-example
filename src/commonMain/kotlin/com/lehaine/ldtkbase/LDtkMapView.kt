package com.lehaine.ldtkbase

import com.lehaine.ldtk.*
import com.soywiz.kds.iterators.fastForEachReverse
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tiles.TileSet
import com.soywiz.korim.color.Colors

inline fun Container.ldtkMapView(
    level: Level,
    tileset: TileSet,
    renderIntGridLayer: Boolean = false,
    debugEntities: Boolean = false,
    callback: LDtkMapView.() -> Unit = {}
) =
    LDtkMapView(level, tileset, renderIntGridLayer, debugEntities).addTo(this, callback)

class LDtkMapView(
    val level: Level,
    val tileset: TileSet,
    val renderIntGridLayers: Boolean = false,
    val debugEntities: Boolean = false
) : Container() {

    init {
        require(level.isLoaded()) { "Level is not loaded! Please make sure level is loaded before creating an LDtkMapView" }

        level.allUntypedLayers.fastForEachReverse { layer ->
            val view: View = when (layer) {
                is LayerTiles,
                is LayerAutoLayer,
                is LayerIntGridAutoLayer -> ldtkLayer(layer, tileset)
                is LayerIntGrid -> {
                    if (renderIntGridLayers) {
                        ldtkLayer(layer, tileset)
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