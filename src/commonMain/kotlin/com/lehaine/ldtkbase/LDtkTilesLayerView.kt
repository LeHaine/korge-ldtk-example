package com.lehaine.ldtkbase

import com.lehaine.ldtk.LayerTiles
import com.soywiz.korge.render.RenderContext
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.View
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.tiles.TileSet

inline fun Container.ldtkTilesLayer(layer: LayerTiles, tileset: TileSet, callback: LDtkTilesLayerView.() -> Unit = {}) =
    LDtkTilesLayerView(layer, tileset).addTo(this, callback)

class LDtkTilesLayerView(val layer: LayerTiles, val tileset: TileSet) : View() {
    override fun renderInternal(ctx: RenderContext) {
        if (!visible) {
            return
        }

        for (cy in 0..layer.cHeight) {
            for (cx in 0..layer.cWidth) {
                if (layer.hasAnyTileAt(cx, cy)) {
                    layer.getTileStackAt(cx, cy).forEach {
                        val bmpSlice = tileset[it.tileId]!!
                        ctx.batch.drawQuad(
                            tex = ctx.getTex(bmpSlice),
                            x = (cx * layer.gridSize + layer.pxTotalOffsetX).toFloat(),
                            y = (cy * layer.gridSize + layer.pxTotalOffsetY).toFloat(),
                            width = layer.gridSize.toFloat(),
                            height = layer.gridSize.toFloat(),
                            m = globalMatrix,
                            filtering = false
                        )
                    }
                }
            }
        }
    }


}