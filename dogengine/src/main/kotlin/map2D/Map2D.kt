package dogengine.map2D

import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import dogengine.map2D.layers.Layer

class Map2D(private val prop : Properties) {
    private val layers: Array<Layer> = Array()

    fun addLayer(layer: Layer) {
        layers.add(layer)
    }
    fun getWidthMap() : Int = prop.width
    fun getHeightMap() : Int = prop.height
    fun getTileWidth() : Int = prop.tileWidth
    fun getTileHeight() : Int = prop.tileHeight
    fun getWidthMapInPixels() : Float = getWidthMap()*getTileWidth()*1f
    fun getHeightMapInPixels() : Float = getHeightMap()*getTileHeight()*1f

    fun getLayer(index: Int) : Layer = if(index > layers.size || layers.isEmpty)
        throw GdxRuntimeException("Layer with index : $index not found :(")
        else layers[index]
    fun getLayer(name: String) : Layer {
        return layers.first { it.name == name }
    }
    fun getLayers() : Array<Layer> = layers

    fun getViewRectBoundsInLayer(rectBounds: Rectangle,indexLayer: Int) : ImmutableArray<Cell> = getLayer(indexLayer).getCellsInViewBounds(rectBounds)

}