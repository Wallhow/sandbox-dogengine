package dogengine.map2D

import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException

class Map2D(private val rootLayer : Layer) {
    private val arrayLayer: Array<Layer> = Array()
    private var widthMapInPixels = -1f
    private var heightMapInPixels = -1f

    init {
        arrayLayer.add(rootLayer)
    }

    fun addLayer(layer: Layer) {
        arrayLayer.add(layer)
    }
    fun getWidthMap() : Int = rootLayer.width
    fun getHeightMap() : Int = rootLayer.height
    fun getTileWidth() : Int = rootLayer.tileWidth
    fun getTileHeight() : Int = rootLayer.tileHeight
    fun getWidthMapInPixels() : Float = if (widthMapInPixels!=-1f) widthMapInPixels else { widthMapInPixels = (getWidthMap()*getTileWidth())*1f; widthMapInPixels}
    fun getHeightMapInPixels() : Float = if (heightMapInPixels!=-1f) heightMapInPixels else { heightMapInPixels = (getHeightMap()*getTileHeight())*1f; heightMapInPixels}

    fun getLayer(index: Int) : Layer = if(index > arrayLayer.size || arrayLayer.isEmpty)
        throw GdxRuntimeException("Layer with index : $index not found :(")
        else arrayLayer[index]

    fun getViewRectBoundsInLayer(rectBounds: Rectangle,indexLayer: Int) : ImmutableArray<Cell> = getLayer(indexLayer).getCellsInViewBounds(rectBounds)

}