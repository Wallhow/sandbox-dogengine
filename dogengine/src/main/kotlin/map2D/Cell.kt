package dogengine.map2D

import com.badlogic.gdx.utils.ArrayMap
import dogengine.map2D.layers.Layer
import map2D.TypeData
import map2D.Vector2Int

interface Cell {
    var x: Int
    var y: Int
    var topNeighbors : Vector2Int
    var bottomNeighbors : Vector2Int
    var rightNeighbors : Vector2Int
    var leftNeighbors : Vector2Int
    val data : ArrayMap<TypeData,Any>
    var heightType : Int
    var bitmask : Int
    var collidable : Boolean
    var floodFilled: Boolean
    var isInEngine : Boolean


    companion object {
        val defCell2D = Cell2D(-1, -1, -1)
    }

}

fun Vector2Int.getCell(layerChunk: Layer): Cell {
    return if (x != -1 && y != -1)
        layerChunk.getCell(x, y)
    else Cell.defCell2D
}