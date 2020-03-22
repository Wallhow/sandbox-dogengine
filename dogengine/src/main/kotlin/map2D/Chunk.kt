package dogengine.map2D

import dogengine.utils.Array2D


class Chunk(width: Int,height: Int) : Array2D(width,height) {
    var x: Int = 0
    var y: Int = 0
    val index : Int
        get() = x+y * col

    var cells: Array<Cell?> = arrayOfNulls(width * height)

    fun setCell(cell: Cell, tx: Int, ty: Int) {
        cells[toIndex(tx,ty)] = cell
    }
    fun getCell(tx: Int,ty: Int) : Cell? {
        return cells[toIndex(tx,ty)]
    }
    data class ChunkXY(var x: Int,var y: Int)
}