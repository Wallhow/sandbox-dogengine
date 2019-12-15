package sandbox.sandbox.def.map2D

class Cell2D(override var x: Int,
           override var y: Int,override var heightType: Int) : Cell {
    private val defCell = Cell.DefCell2D()
    override var bitmask: Int = 0
    //соседи
    override var topNeighbors : Cell = defCell
    override var bottomNeighbors : Cell = defCell
    override var rightNeighbors : Cell = defCell
    override var leftNeighbors : Cell = defCell
    override var userData : Any = "no user data"
    override var collidable: Boolean = false
    override var floodFilled: Boolean = false
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
    }
}