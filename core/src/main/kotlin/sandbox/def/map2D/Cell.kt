package sandbox.sandbox.def.map2D

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import dogengine.ecs.def.ComponentResolver

class Cell2D(override var x: Int,
           override var y: Int,override var heightType: Int) : Cell {
    override var bitmask: Int = -1
    //соседи
    override var topNeighbors : Cell = Cell.defCell2D
    override var bottomNeighbors : Cell = Cell.defCell2D
    override var rightNeighbors : Cell = Cell.defCell2D
    override var leftNeighbors : Cell = Cell.defCell2D
    override var userData : Any = "no user data"
    override var collidable: Boolean = false
    override var floodFilled: Boolean = false
    override var isInEngine: Boolean = false
}

interface Cell {
    var x:Int
    var y: Int
    var topNeighbors : Cell
    var bottomNeighbors : Cell
    var rightNeighbors : Cell
    var leftNeighbors : Cell
    var userData : Any
    var heightType : Int
    var bitmask : Int
    var collidable : Boolean
    var floodFilled: Boolean
    var isInEngine : Boolean
    companion object {
        val defCell2D = DefCell2D()
    }
    class DefCell2D: Cell {
        override var x: Int = -1
        override var y: Int = -1
        override var bitmask: Int = 0
        override var heightType: Int = 0
        override var topNeighbors: Cell = this
        override var bottomNeighbors: Cell = this
        override var rightNeighbors: Cell = this
        override var leftNeighbors: Cell = this
        override var userData: Any = "no user data"
        override var collidable: Boolean = false
        override var floodFilled: Boolean = false
        override var isInEngine: Boolean = false
    }
}

class CCell : Component,Pool.Poolable {
    companion object : ComponentResolver<CCell>(CCell::class.java)
    var cell: Cell? = null
    override fun reset() {
        cell = null
    }
}