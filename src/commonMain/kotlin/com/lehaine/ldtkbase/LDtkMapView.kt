package com.lehaine.ldtkbase

import com.lehaine.ldtk.LayerTiles
import com.lehaine.ldtk.Level
import com.soywiz.kds.iterators.fastForEachReverse
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tiles.TileSet

inline fun Container.ldtkMapView(level: Level, tileset: TileSet, callback: LDtkMapView.() -> Unit = {}) =
    LDtkMapView(level, tileset).addTo(this, callback)

class LDtkMapView(val level: Level, val tileset: TileSet) : Container() {

    init {
        require(level.isLoaded()) { "Level is not loaded! Please make sure level is loaded before creating an LDtkMapView" }

        level.allUntypedLayers.fastForEachReverse { layer ->
            val view: View = when (layer) {
                is LayerTiles -> ldtkTilesLayer(layer, tileset)
                else -> dummyView()
            }
            view.visible(true)
                .name(layer.identifier.takeIf { it.isNotEmpty() })
                .xy(layer.pxTotalOffsetX, layer.pxTotalOffsetY)
        }
    }
}