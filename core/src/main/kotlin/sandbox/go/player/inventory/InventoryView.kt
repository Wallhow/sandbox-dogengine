package sandbox.go.player.inventory

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.Viewport
import dogengine.Kernel
import dogengine.ecs.systems.controllers.EventInputListener
import dogengine.utils.TTFFont
import dogengine.utils.vec2
import sandbox.sandbox.go.items.ObjectList

class InventoryView(private val sb: SpriteBatch, private val fnt: TTFFont, private val inventory: Inventory) : EventInputListener() {
    private val dot: Texture = Texture(Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
        setColor(Color.WHITE)
        fill() })
    val invArray = InvCellArray()
    val colorBG = Color.BLACK.cpy().apply { a = 0.5f }
    val colorCell = Color.BLUE.cpy().apply { a = 0.4f }
    private val view = Kernel.getInjector().getInstance(Viewport::class.java)
    private var x = 0f
    fun init() {
        invArray.init()
    }

    override fun touchDown(x_: Float, y_: Float): Boolean {
        invArray.cells.forEach { cell ->
            val rectCell = Rectangle(x + cell.x,invArray.pos.y + cell.y,cell.size, cell.size)
            if(rectCell.contains(x_,y_)) {
                inventory.currentItem = invArray.getIndex(cell)
            }
        }
        return false
    }

    fun draw(width: Float, height: Float,atlas: TextureAtlas) {
        if(x==0f) { x = (width / 2f) - invArray.width/2 }
        fun SpriteBatch.drawCell(cell: InvCell) {
            this.draw(dot, x + cell.x, invArray.pos.y + cell.y, cell.size, cell.size)
        }
        fun SpriteBatch.drawImgInCell(cell: InvCell, img: TextureAtlas.AtlasRegion) {
            this.draw(img, x + cell.x, invArray.pos.y + cell.y, cell.size, cell.size)
        }
        sb.begin()
        //бек
        sb.color = colorBG
        sb.draw(dot, x, invArray.pos.y, invArray.width, invArray.height)

        sb.color = Color.GOLD

        val cell = invArray.getCell(inventory.currentItem)
        sb.draw(dot, x + cell.x, invArray.pos.y + cell.y, cell.size, cell.size)

        invArray.cells.forEach { cell ->
            sb.color = colorCell
            sb.drawCell(cell)
            if (!cell.isEmpty) {
                sb.color = Color.WHITE
                sb.drawImgInCell(cell,atlas.findRegion(cell.nameRes))
                fnt.get(26).draw(sb,"${cell.countItem}",x + cell.x,invArray.pos.y + cell.y+16)
            }
        }
        sb.color = Color.WHITE
        sb.end()
    }

    fun update() {

        invArray.reset()

        val array = inventory.readAll()
        array.forEach {
            var isItemOnInv = false
            val cells = invArray.cells
            for (i in 0 until cells.size) {
                val cell = cells[i]
                if (cell.itemID == it.first.id) {
                    cell.countItem = it.second
                    isItemOnInv = true
                    break
                } else {
                    isItemOnInv = false
                }
            }
            if (!isItemOnInv) {
                invArray.getEmptyCell()?.apply {
                    itemID = it.first.id
                    countItem = it.second
                    nameRes = it.first.name_res
                }
            }
        }
    }

    class InvCell {
        var size: Float = 0f
        var x: Float = 0f
        var y: Float = 0f
        var countItem = -1
        var itemID = -1
        val isEmpty: Boolean
            get() = (countItem==-1 && itemID==-1)
        var nameRes = ""
    }
    class InvCellArray {
        private val invCells = Array<InvCell>()
        var width = 0f
        var height = 0f
        var separatorSize = 3f
        val pos = vec2(0f, 0f)
        var cellSize = 36f
        var cellCount = 12
        val cells: Array<InvCell>
            get() = invCells
        fun getCell(index: Int): InvCell = invCells[index]
        fun getIndex(cell: InvCell): Int {
            return invCells.indexOf(cell,true)
        }
        fun getEmptyCell() : InvCell? {
            var cell: InvCell? = null
            for (i in cells.size-1 downTo 0 ) {
                if(cells[i].isEmpty) {
                    cell = cells[i]
                }
            }
            return cell
        }

        fun reset() {
            for (i in 0 until cellCount) {
                invCells[i].countItem = -1
                invCells[i].itemID = -1
            }
        }

        fun init() {
            width = (cellSize * cellCount + (cellCount+1) * separatorSize)
            height = cellSize + separatorSize * 2
            for (i in 0 until cellCount) {
                val paddingLeft = separatorSize*(i+1)
                val cell = InvCell()
                cell.x = i * cellSize + paddingLeft
                cell.y = separatorSize
                cell.size = cellSize
                invCells.add(cell)
            }
        }
    }
}