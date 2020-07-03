package dogengine.map2D.layers

import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import dogengine.map2D.Cell
import dogengine.utils.Array2D
import dogengine.utils.log

class GridLayer(properties: LayerProperties, override val name: String = properties.name) : Layer {
    override var isVisible: Boolean = true
    override var width: Int = properties.width
    override var height: Int = properties.height
    override val cellWidth: Int = properties.tileWidth
    override val cellHeight: Int = properties.tileHeight
    val cells: GridCells = GridCells(width, height)


    class GridCells(width: Int, height: Int) : Array2D(width, height) {
        private val cells: Array<Cell> = Array(width*height) { Cell.defCell2D.copy(x = -1, y = -1, heightType = -1) }

        fun getCell(x: Int, y: Int): Cell? {
            return cells[toIndex(x, y)]
        }

        fun setCell(cell: Cell, x: Int, y: Int) {
            cells[toIndex(x, y)] = cell
        }

        fun toArray() : Array<Cell> = cells
    }

    override fun getCell(x: Int, y: Int): Cell = cells.getCell(x, y) as Cell

    override fun setCell(cell: Cell, x: Int, y: Int) {
        cells.setCell(cell, x, y)
    }


    override fun getCellsInViewBounds(rectBounds: Rectangle): ImmutableArray<Cell> {
        val mX = 0.coerceAtLeast(MathUtils.roundPositive(rectBounds.x / cellWidth))
        val mY = 0.coerceAtLeast(MathUtils.roundPositive(rectBounds.y / cellHeight))

        val array = com.badlogic.gdx.utils.Array<Cell>()

        val mW = (rectBounds.width / cellWidth).toInt()
        val mH = (rectBounds.height / cellHeight).toInt()
        val minX = mX - mW
        val maxX = mX + mW
        val minY = mY - mH
        val maxY = mY + mH
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                if (cells.isIndexValid(x, y)) {
                    if (cells.getCell(x, y) != null) {
                        array.add(cells.getCell(x, y))
                    }
                }
            }
        }

        return ImmutableArray(array)
    }

}
