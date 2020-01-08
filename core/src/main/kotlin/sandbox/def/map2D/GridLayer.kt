package sandbox.sandbox.def.map2D

import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.github.czyzby.noise4j.array.Array2D
import dogengine.utils.vec2

class GridLayer(properties: LayerProperties) : Layer {
    override var isVisible: Boolean = true
    override var width: Int = properties.width
    override var height: Int = properties.height
    override val tileWidth: Int = properties.tileWidth
    override val tileHeight: Int = properties.tileHeight
    override val name: String = properties.name
    override var index: Int = properties.index
    private val grid : GridCells = GridCells(width,height)


    class GridCells(width: Int,height: Int) : Array2D(width,height) {
        private val cells: Array<Cell?> = arrayOfNulls(width * height)

        fun getCell(x: Int,y: Int) : Cell? {
            return cells[toIndex(x,y)]
        }
        fun setCell(cell: Cell,x: Int,y: Int) {
            cells[toIndex(x,y)] = cell
        }
    }

    override fun getCell(x: Int, y: Int): Cell = grid.getCell(x,y) as Cell

    override fun setCell(cell: Cell, x: Int, y: Int) {
        grid.setCell(cell, x, y)
    }


    override fun getCellsInViewBounds(rectBounds: Rectangle): ImmutableArray<Cell> {
        val mX = 0.coerceAtLeast(MathUtils.roundPositive(rectBounds.x / tileWidth))
        val mY = 0.coerceAtLeast(MathUtils.roundPositive(rectBounds.y / tileHeight))

        val array = com.badlogic.gdx.utils.Array<Cell>()

        val point = vec2(mX*1f, mY*1f)
        val view = 10
        val minX = mX-view
        val maxX = mX+view
        val minY = mY-view
        val maxY = mY+view
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                if(grid.isIndexValid(x,y)) {
                    array.add(grid.getCell(x,y))
                }
            }
        }

        return ImmutableArray(array)
    }

}
