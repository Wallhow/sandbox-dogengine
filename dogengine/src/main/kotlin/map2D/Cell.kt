package dogengine.map2D

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import dogengine.ecs.def.ComponentResolver

class Cell2D(override var x: Int,
           override var y: Int,override var heightType: Int) : Cell {
    override var bitmask: Int = -1
    //соседи
    override var topNeighbors = Cell.defCellXY
    override var bottomNeighbors = Cell.defCellXY
    override var rightNeighbors = Cell.defCellXY
    override var leftNeighbors = Cell.defCellXY
    override var userData : Any = "no user data"
    override var collidable: Boolean = false
    override var floodFilled: Boolean = false
    override var isInEngine: Boolean = false
}

interface Cell {
    var x:Int
    var y: Int
    var topNeighbors : CellXY
    var bottomNeighbors : CellXY
    var rightNeighbors : CellXY
    var leftNeighbors : CellXY
    var userData : Any
    var heightType : Int
    var bitmask : Int
    var collidable : Boolean
    var floodFilled: Boolean
    var isInEngine : Boolean

    data class CellXY(var x:Int,var y:Int) {
        companion object {
            val tmp = CellXY(-1,-1)
        }
    }
    class DefCell2D: Cell {
        override var x: Int = -1
        override var y: Int = -1
        override var bitmask: Int = 0
        override var heightType: Int = 0
        override var topNeighbors = CellXY.tmp
        override var bottomNeighbors= CellXY.tmp
        override var rightNeighbors= CellXY.tmp
        override var leftNeighbors= CellXY.tmp
        override var userData: Any = "no user data"
        override var collidable: Boolean = false
        override var floodFilled: Boolean = false
        override var isInEngine: Boolean = false
    }
    companion object {
        val defCellXY = CellXY(-1,-1)
        val defCell2D = DefCell2D()
    }

}

class CCell : Component,Pool.Poolable {
    companion object : ComponentResolver<CCell>(CCell::class.java)
    var cell: Cell? = null
    override fun reset() {
        cell = null
    }
}

fun Cell.CellXY.getCell(layerChunk: Layer): Cell {
    return if (x != -1 && y != -1)
        layerChunk.getCell(x, y)
    else Cell.defCell2D
}