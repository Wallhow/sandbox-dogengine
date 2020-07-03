package dogengine.map2D.layers

import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Rectangle
import dogengine.map2D.Cell

interface Layer {
    val name: String
    var isVisible: Boolean
    var width:Int
    var height:Int
    val cellWidth: Int
    val cellHeight: Int

    fun getCell(x: Int,y: Int) : Cell
    fun setCell(cell: Cell, x: Int, y: Int)
    fun getCellsInViewBounds(rectBounds: Rectangle) : ImmutableArray<Cell>

}