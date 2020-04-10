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
import dogengine.utils.Array2D
import dogengine.utils.TTFFont
import dogengine.utils.vec2

class InventoryView(private val sb: SpriteBatch, private val fnt: TTFFont, private val inventory: Inventory) : EventInputListener() {
    private val dot: Texture = Texture(Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
        setColor(Color.WHITE)
        fill()
    })
    val invArray = InvTable(12)
    val colorBG = Color.BLACK.cpy().apply { a = 0.5f }
    val colorCell = Color.BLUE.cpy().apply { a = 0.4f }
    private val view = Kernel.getInjector().getInstance(Viewport::class.java)
    private var x = 0f
    fun init() {
        invArray.init(inventory.readAll())
    }

    override fun touchDown(x_: Float, y_: Float): Boolean {
        invArray.cells.forEach { cell ->
            val rectCell = Rectangle(x + cell.x, invArray.pos.y + cell.y, cell.size, cell.size)
            if (rectCell.contains(x_, y_)) {
                inventory.currentItem = invArray.getIndex(cell)
            }
        }
        return false
    }

    fun draw(width: Float, height: Float, atlas: TextureAtlas) {
        if (x == 0f) {
            x = (width / 2f) - invArray.width / 2
        }
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
            if (!cell.isEmpty || cell.nameRes != "null") {
                sb.color = Color.WHITE
                sb.drawImgInCell(cell, atlas.findRegion(cell.nameRes))
                fnt.get(26).draw(sb, "${cell.item.count}", x + cell.x, invArray.pos.y + cell.y + 16)
            }
        }
        sb.color = Color.WHITE
        sb.end()
    }

    fun update() {
        inventory.readAll().forEach { cell ->
            if (cell.isEmpty()) {
                cell.setZero()
            }
        }
    }

        class InvCell {
            var size: Float = 0f
            var x: Float = 0f
            var y: Float = 0f
            var item: Inventory.InvItem = Inventory.InvItem()
            val isEmpty: Boolean
                get() = item.isEmpty()
            val nameRes: String
                get() = item.itemID.resourcesName
        }

        class InvTable(size: Int) : Array2D(size, 1) {
            val cells = Array<InvCell>()
            var width = 0f
            var height = 0f
            var separatorSize = 3f
            val pos = vec2(0f, 0f)
            var cellSize = 36f
            var cellCount = size
            fun getCell(index: Int): InvCell = this.cells[index]
            fun getIndex(cell: InvCell): Int {
                return this.cells.indexOf(cell, true)
            }

            fun getEmptyCell(): InvCell? {
                var cell: InvCell? = null
                for (i in 0 until cells.size) {
                    if (cells[i].isEmpty) {
                        cell = cells[i]
                        break
                    }
                }
                return cell
            }

            fun reset() {

            }

            fun init(array: kotlin.Array<out Inventory.InvItem>) {
                width = (cellSize * cellCount + (cellCount + 1) * separatorSize)
                height = cellSize + separatorSize * 2
                for (i in 0 until cellCount) {
                    val paddingLeft = separatorSize * (i + 1)
                    val cell = InvCell()
                    cell.x = i * cellSize + paddingLeft
                    cell.y = separatorSize
                    cell.size = cellSize
                    cell.item = array[i]
                    this.cells.add(cell)
                }
            }
        }
    }