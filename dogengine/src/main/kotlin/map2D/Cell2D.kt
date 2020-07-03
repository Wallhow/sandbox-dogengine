package dogengine.map2D

import com.badlogic.gdx.utils.ArrayMap
import map2D.TypeData
import map2D.Vector2Int

data class Cell2D(override var x: Int,
                  override var y: Int, override var heightType: Int) : Cell {
    override var bitmask: Int = -1
    //соседи
    override var topNeighbors = Vector2Int.tmp
    override var bottomNeighbors = Vector2Int.tmp
    override var rightNeighbors = Vector2Int.tmp
    override var leftNeighbors = Vector2Int.tmp
    override val data : ArrayMap<TypeData, Any> = ArrayMap()
    override var collidable: Boolean = false
    override var floodFilled: Boolean = false
    override var isInEngine: Boolean = false
    init {
        data.put(TypeData.TypeCell,"no user data")
        data.put(TypeData.ObjectOn, null)
    }
}