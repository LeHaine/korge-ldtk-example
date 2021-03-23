package com.lehaine.ldtkbase

import com.lehaine.ldtk.*
import com.soywiz.kds.iterators.fastForEach
import com.soywiz.korge.view.tiles.TileSet
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs


suspend fun Level.loadTileSets(): Map<Int, TileSet> {
    if (!isLoaded()) {
        loadAsync()
    }

    val tileSets = mutableMapOf<Int, TileSet>()

    allUntypedLayers.fastForEach { layer ->
        when (layer.type) {
            LayerType.IntGrid -> {
                if (layer is LayerIntGridAutoLayer) {
                    if (!tileSets.containsKey(layer.tileset.json.uid)) {
                        tileSets[layer.tileset.json.uid] = layer.tileset.toTileSet()
                    }
                }
            }
            LayerType.Tiles -> {
                layer as LayerTiles
                if (!tileSets.containsKey(layer.tileset.json.uid)) {
                    tileSets[layer.tileset.json.uid] = layer.tileset.toTileSet()
                }
            }
            LayerType.AutoLayer -> {
                layer as LayerAutoLayer
                if (!tileSets.containsKey(layer.tileset.json.uid)) {
                    tileSets[layer.tileset.json.uid] = layer.tileset.toTileSet()
                }
            }
            else -> {
                // nothing
            }
        }
    }
    return tileSets
}

suspend fun Tileset.toTileSet(): TileSet {
    val bmp = resourcesVfs[relPath].readBitmap().toBMP32IfRequired()
    val columns = bmp.width / tileGridSize
    val rows = bmp.height / tileGridSize
    val slices = TileSet.extractBitmaps(bmp, tileGridSize, tileGridSize, columns, columns * rows, 0, 0)
    return TileSet.fromBitmaps(tileGridSize, tileGridSize, slices, 1)
}