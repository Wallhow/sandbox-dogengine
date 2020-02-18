package sandbox.sandbox.def

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.Viewport
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisTable
import dogengine.Kernel
import dogengine.ecs.systems.SystemPriority
import dogengine.utils.TTFFont
import dogengine.utils.vec2
import ktx.vis.table
import sandbox.R
import sandbox.sandbox.go.player.Player

class SGuiDraw(private val player: Player) : EntitySystem(SystemPriority.DRAW + 10) {
    private val view = Kernel.getInjector().getInstance(Viewport::class.java)
    private val gui = Stage(view)
    private val image = VisImage()
    private var beforeDirectionSee: Player.DirectionSee = Player.DirectionSee.DOWN
    private var currentTool = ""
    private lateinit var table: VisTable
    private var dirty = true
    private val sb = SpriteBatch()

    private val inventoryView = Stage(view, sb)
    lateinit var group: VisTable
    private val invImage: Array<VisImage> = Array(8) { VisImage() }
    private val atlas: TextureAtlas = Kernel.getInjector().getInstance(AssetManager::class.java).get<TextureAtlas>(R.matlas0)
    private val fnt = Kernel.getInjector().getInstance(TTFFont::class.java)
    private val invDockBarViewer = InvDockBarViewer(sb,fnt)

    init {
        toolHitInit()
        inventoryViewerInit()
        invDockBarViewer.init()
        fnt.create(26,Color.LIGHT_GRAY)
    }

    private fun toolHitInit() {
        val viewWidth = view.camera.viewportWidth
        val viewHeight = view.camera.viewportHeight
        val halfWidth = viewWidth / 2
        val halfHeight = viewHeight / 2
        val padding = 64f
        table = table {
            setPosition(halfWidth - padding, halfHeight - padding)
            setSize(padding * 2, padding * 2)
            add(image).expand().bottom()
        }
        gui.addActor(table)
    }

    private fun inventoryViewerInit() {
        val viewWidth = view.camera.viewportWidth
        val viewHeight = view.camera.viewportHeight
        val halfWidth = viewWidth / 2
        val halfHeight = viewHeight * 0.5f
        inventoryView.addActor(table {
            setPosition(halfWidth - 36f * 4, 50f)
            setSize(36f * 8, 36f)
            center()
            left()
            group = table {
                var index = 0
                player.getInventory().readAll().forEach {
                    invImage[index].drawable = TextureRegionDrawable(atlas.findRegion(it.first.name_res))
                    val label = label("${it.second}")
                    this.add(invImage[index])
                    add(label)
                    index++
                }
            }
            add(group)
            debug = true
        })
    }


    override fun update(deltaTime: Float) {
        toolHit()
        val array = player.getInventory().readAll()
        array.forEach {
            var isItemOnInv = false
            val cells = invDockBarViewer.invArray.cells
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
                invDockBarViewer.invArray.getEmptyCell()?.apply {
                    itemID = it.first.id
                    countItem = it.second
                    name_res = it.first.name_res
                }
            }
        }
        invDockBarViewer.draw(view.camera.viewportWidth, view.camera.viewportHeight,atlas)


        inventoryView.act()
        inventoryView.draw()
    }

    private fun toolHit() {
        fun actionInit(image: Actor) {
            val time = player.getCurrentTool().attackSpeed / 2f
            image.color.a = 0f
            image.clearActions()
            val a = Actions.sequence(Actions.fadeIn(time, Interpolation.pow4Out), Actions.fadeOut(time))
            val seq = Actions.sequence(a)
            seq.addAction(Actions.removeAction(seq))
            image.addAction(seq)
        }
        if (player.getCurrentTool().isActive) {
            if (dirty) {
                actionInit(image)
                if (beforeDirectionSee != player.directionSee) {
                    beforeDirectionSee = player.directionSee
                    table.reset()
                    when (beforeDirectionSee) {
                        Player.DirectionSee.UP -> {
                            table.add(image).expand().top()
                        }
                        Player.DirectionSee.DOWN -> {
                            table.add(image).expand().bottom()
                        }
                        Player.DirectionSee.RIGHT -> {
                            table.add(image).expand().right()
                        }
                        Player.DirectionSee.LEFT -> {
                            table.add(image).expand().left()
                        }
                    }
                }
                dirty = false
            }
            if (player.getCurrentTool().name != currentTool) {
                currentTool = player.getCurrentTool().name
                image.drawable = TextureRegionDrawable(player.getCurrentTool().image)
            }
            gui.act()
            gui.draw()
        } else {
            dirty = true
        }
    }

    class InvDockBarViewer(private val sb: SpriteBatch,private val fnt: TTFFont) {
        private val dot: Texture = Texture(Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
            setColor(Color.WHITE)
            fill() })
        val invArray = InventoryCellArray()
        val colorBG = Color.BLACK.cpy().apply { a = 0.5f }
        val colorCell = Color.BLUE.cpy().apply { a = 0.4f }
        fun init() {
            invArray.init()
        }

        fun draw(width: Float, height: Float,atlas: TextureAtlas) {
            val x = (width / 2f) - invArray.width/2
            sb.begin()
            sb.color = colorBG
            sb.draw(dot, x, invArray.pos.y, invArray.width, invArray.height)
            invArray.cells.forEach { cell ->
                sb.color = colorCell
                sb.draw(dot, x + cell.x, invArray.pos.y + cell.y, cell.size, cell.size)
                if (!cell.isEmpty) {
                    sb.color = Color.WHITE
                    sb.draw(atlas.findRegion(cell.name_res), x + cell.x, invArray.pos.y + cell.y, cell.size, cell.size)
                    fnt.get(26).draw(sb,"${cell.countItem}",x + cell.x,invArray.pos.y + cell.y+16)
                }
            }
            sb.color = Color.WHITE
            sb.end()
        }
    }

    class InventoryCell {
        var size: Float = 0f
        var x: Float = 0f
        var y: Float = 0f
        var countItem = -1
        var itemID = -1
        val isEmpty: Boolean
            get() = (countItem==-1 && itemID==-1)
        var name_res = ""
    }
    class InventoryCellArray {
        private val invCells = com.badlogic.gdx.utils.Array<InventoryCell>()
        var width = 0f
        var height = 0f
        var separatorSize = 3f
        val pos = vec2(0f, 0f)
        var cellSize = 36f
        var cellCount = 12
        val cells: com.badlogic.gdx.utils.Array<InventoryCell>
            get() = invCells

        fun getEmptyCell() : InventoryCell? {
            var cell: InventoryCell? = null
            cells.forEach {
                if(it.isEmpty) {
                    cell = it
                }
            }
            return cell
        }

        fun init() {
            width = (cellSize * cellCount + (cellCount+1) * separatorSize)
            height = cellSize + separatorSize * 2
            for (i in 0 until cellCount) {
                val paddingLeft = separatorSize*(i+1)
                val cell = InventoryCell()
                cell.x = i * cellSize + paddingLeft
                cell.y = separatorSize
                cell.size = cellSize
                invCells.add(cell)
            }
        }
    }
}