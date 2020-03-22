package dogengine.map2D

import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.GdxRuntimeException
import dogengine.utils.Array2D
import dogengine.utils.vec2


private val Cell.CellXY.isTmp: Boolean
    get() {
        return (x == -1 || y == -1)
    }

class ChunkGridLayer(properties: LayerProperties) : Layer {
    override var isVisible: Boolean = true
    override var width: Int = properties.width
    override var height: Int = properties.height
    override val tileWidth: Int = properties.tileWidth
    override val tileHeight: Int = properties.tileHeight
    override val name: String = properties.name
    override var index: Int = properties.index
    private val chunkManager = ChunkManager(width, height, 8)


    class ChunkManager(widthWorld: Int, heightWorld: Int, val sizeChunk: Int) :
            Array2D(widthWorld / sizeChunk, heightWorld / sizeChunk) {
        private val chunks: Array<Chunk?> = arrayOfNulls(col * row)
        private val buffer: com.badlogic.gdx.utils.Array<Chunk> = com.badlogic.gdx.utils.Array()

        fun updateBuffer(point: Vector2, viewChunkSize: Int): com.badlogic.gdx.utils.Array<Chunk> {
            buffer.clear()
            val pointInChunk = Vector2(MathUtils.round(point.x / sizeChunk).toFloat(),
                    MathUtils.round(point.y / sizeChunk).toFloat())

            chunks.filterNotNull().forEach {
                if (pointInChunk.dst(it.x.toFloat(), it.y.toFloat()) <= viewChunkSize) {
                    buffer.add(it)
                }
            }
            return buffer
        }

        fun getCell(x: Int, y: Int): Cell? {
            val chX = (x/(sizeChunk))
            val chY = y/(sizeChunk)
            val index = toIndex(chX, chY)
            return chunks[index]?.getCell(x - chX*sizeChunk, y - chY*sizeChunk)
        }
        //Передаю тайловую позицию
        fun setCell(cell: Cell, x: Int, y: Int) {
            val chX = (x/(sizeChunk))
            val chY = y/(sizeChunk)
            val index = toIndex(chX, chY)
            if (chunks[index] == null) {
                chunks[index] = Chunk(sizeChunk, sizeChunk).apply { this.x = chX ; this.y = chY }
            }
            chunks[index]?.setCell(cell, x - chX*sizeChunk, y - chY*sizeChunk)
        }

    }

    override fun getCell(x: Int, y: Int): Cell =
            if (chunkManager.getCell(x, y) != null) {
                chunkManager.getCell(x, y) as Cell
            } else throw GdxRuntimeException("Out of array cells bounds, cell with index : ${chunkManager.toIndex(x, y)} not found")

    override fun setCell(cell: Cell, x: Int, y: Int) {
        chunkManager.setCell(cell, x, y)
    }

    companion object {
        fun updateBitmask(cell: Cell, layerChunk: Layer) {
            var count: Int = 0
            if (!cell.topNeighbors.isTmp && cell.topNeighbors.getCell(layerChunk).heightType == cell.heightType) count += 1
            if (!cell.rightNeighbors.isTmp && cell.rightNeighbors.getCell(layerChunk).heightType == cell.heightType) count += 2
            if (!cell.bottomNeighbors.isTmp && cell.bottomNeighbors.getCell(layerChunk).heightType == cell.heightType) count += 4
            if (!cell.leftNeighbors.isTmp && cell.leftNeighbors.getCell(layerChunk).heightType == cell.heightType) count += 8
            cell.bitmask = count
        }
    }

    override fun getCellsInViewBounds(rectBounds: Rectangle): ImmutableArray<Cell> {
        val mX = 0.coerceAtLeast(MathUtils.roundPositive(rectBounds.x / tileWidth))
        val mY = 0.coerceAtLeast(MathUtils.roundPositive(rectBounds.y / tileHeight))

        val array = com.badlogic.gdx.utils.Array<Cell>()

        val point = vec2(mX*1f, mY*1f)
        chunkManager.updateBuffer(point, 1).forEach { chunk ->
            chunk.cells.forEach {
                array.add(it)
            }
        }
        return ImmutableArray(array)
    }


    private fun inArray(x: Int, y: Int): Int = x + y * (width)


    fun initChunks() {
        /*val g = GsonBuilder().create()
        val json = g.toJson(chunks)
        println(json)*/

    }
}

private fun <E> List<E>.toGdxArray(): com.badlogic.gdx.utils.Array<E> {
    val array: com.badlogic.gdx.utils.Array<E> = com.badlogic.gdx.utils.Array()
    this.forEach { array.add(it) }
    return array
}

data class LayerProperties(val width: Int, val height: Int,
                           val tileWidth: Int, val tileHeight: Int,
                           val index: Int, val name: String = "unnamed")