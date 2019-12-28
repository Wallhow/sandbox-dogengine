package sandbox.sandbox.def.map2D

import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.GdxRuntimeException



class GridLayer(properties: LayerProperties) : Layer {
    override var isVisible: Boolean = true
    override var width: Int = properties.width
    override var height: Int = properties.height
    override val tileWidth: Int = properties.tileWidth
    override val tileHeight: Int = properties.tileHeight
    override val name: String = properties.name
    override var index: Int = properties.index

    //TODO доработать чанки
    val chunkWidth : Int = 8
    val chunkHeight : Int = 8
    val chunkCols = width/chunkWidth
    val chunkRows = height / chunkHeight
    val chunks: Array<Chunk?> = arrayOfNulls(chunkCols * chunkRows)


    private val cells: Array<Cell?> = arrayOfNulls(width*height)
    override fun getCell(x: Int, y: Int): Cell =
            if (!checkOutOfArrayBounds(inArray(x, y))) {
                val chunkX = x/chunkWidth
                val chunkY = y/chunkHeight
                //cells[inArray(x, y)]!!
                chunks[chunkX+chunkY*chunkCols]!!.cells[x - chunkX*chunkWidth+ (y - chunkY*chunkHeight)*chunkWidth]!!
            } else throw GdxRuntimeException("Out of array cells bounds, cell with index : ${inArray(x, y)} not found")

    override fun setCell(cell: Cell, x: Int, y: Int) {
        if (!checkOutOfArrayBounds(inArray(x, y))) {
            val chunkX = x/chunkWidth
            val chunkY = y/chunkHeight
            if(chunks[chunkX+chunkY*chunkCols]==null) {
                chunks[chunkX+chunkY*chunkCols] = Chunk(chunkWidth,chunkHeight)
            }
            chunks[chunkX+chunkY*chunkCols]!!.cells[x - chunkX*chunkWidth+ (y - chunkY*chunkHeight)*chunkWidth] = cell
            //cells[inArray(x, y)] = cell
        }
    }

    companion object {
        fun updateBitmask(cell: Cell) {
            var count: Int = 0
            if (cell.topNeighbors.heightType == cell.heightType) count += 1
            if (cell.rightNeighbors.heightType == cell.heightType) count += 2
            if (cell.bottomNeighbors.heightType == cell.heightType) count += 4
            if (cell.leftNeighbors.heightType == cell.heightType) count += 8
            cell.bitmask = count
        }
    }

    override fun getCellsInViewBounds(rectBounds: Rectangle): ImmutableArray<Cell> {
        val mX = 0.coerceAtLeast(MathUtils.roundPositive(rectBounds.x / tileWidth))
        val mY =  0.coerceAtLeast(MathUtils.roundPositive(rectBounds.y / tileHeight))
        val mX2 = (width-1).coerceAtMost(mX + MathUtils.roundPositive(rectBounds.width / tileWidth))
        val mY2 =  (height-1).coerceAtMost(mY + MathUtils.roundPositive(rectBounds.height / tileHeight))


        val array = com.badlogic.gdx.utils.Array<Cell>()
        var count = 0
        for( x in mX..mX2) {
            for (y in mY..mY2) {
                //array.add(cells[inArray(x,y)])
                array.add(getCell(x,y))
                count++
            }
        }
        count
        return ImmutableArray(array)
    }

    private fun checkOutOfArrayBounds(index: Int): Boolean {
        return index > cells.size || index < 0
    }

    private fun inArray(x: Int, y: Int): Int = x + y * (width)

}

private fun <E> List<E>.toGdxArray(): com.badlogic.gdx.utils.Array<E> {
    val array : com.badlogic.gdx.utils.Array<E> = com.badlogic.gdx.utils.Array()
    this.forEach { array.add(it) }
    return array
}

data class LayerProperties(val width: Int, val height: Int,
                           val tileWidth: Int, val tileHeight: Int,
                           val index: Int, val name: String = "unnamed")