package sandbox.sandbox.def.map2D

import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Rectangle

interface Layer {
    val name: String
    var index: Int
    var isVisible: Boolean
    var width:Int
    var height:Int
    val tileWidth: Int
    val tileHeight: Int

    fun getCell(x: Int,y: Int) : Cell
    fun setCell(cell: Cell, x: Int, y: Int)
    fun getCellsInViewBounds(rectBounds: Rectangle) : ImmutableArray<Cell>

}