package sandbox.sandbox.go.player.inventory

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.Viewport
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup
import com.kotcrab.vis.ui.layout.VerticalFlowGroup
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import dogengine.Kernel
import dogengine.ecs.systems.controllers.EventInputListener
import dogengine.utils.TTFFont
import dogengine.utils.log
import ktx.actors.onClick
import sandbox.go.environment.ItemList
import sandbox.go.player.inventory.Inventory
import sandbox.go.player.inventory.InventoryObserver
import sandbox.go.player.tools.ATool
import sandbox.sandbox.getTextureDot
import sandbox.sandbox.go.assetAtlas
import sandbox.sandbox.go.player.Player
import sandbox.sandbox.go.player.tools.SelectToolObserver
import sandbox.sandbox.go.player.tools.ToolsList

class InventoryAndToolView(private val player: Player) : InventoryObserver, SelectToolObserver {
    private val dot = getTextureDot()
    private val rootTable: VisTable = VisTable()
    private val horTable = VisTable()
    private val leftHGroup = VerticalFlowGroup(40f)
    private val rightHGroup = VerticalFlowGroup(40f)
    private val cells = Array<InventoryCell>()
    private val backgroundColor = Color.DARK_GRAY.cpy().apply { a=.7f }
    private var currentSelectCell = 0
    private val toolCell : ToolCell
    private val select: (current: Int) -> Unit = {
        if(currentSelectCell!=it) {
            cells[currentSelectCell].unselect()
            currentSelectCell = it
            cells[currentSelectCell].select()
            player.getInventory().currentItem = currentSelectCell
        }
    }
    init {
        val inventory = player.getInventory()
        val view = Kernel.getInjector().getInstance(Viewport::class.java)
        for (i in 0..5) {
            cells.add(InventoryCell(i,backgroundColor,select))
            leftHGroup.addActor(cells[i])
        }
        for (i in 6..11) {
            cells.add(InventoryCell(i,backgroundColor,select))
            rightHGroup.addActor(cells[i])
        }
        horTable.add(leftHGroup)
        toolCell = ToolCell(player.getCurrentTool(),backgroundColor)
        horTable.add(toolCell).spaceRight(58f)
        horTable.add(rightHGroup)

        rootTable.background = TextureRegionDrawable(dot)
        rootTable.setPosition(view.screenWidth/2f,12f)
        rootTable.add(horTable)

        inventory.observers.add(this)
        player.toolObservers.add(this)
    }

    override fun countChanged(newCount: Int, oldCount: Int, item: Inventory.InvItem) {
        if(oldCount>=0 && newCount <=0) {
            cells.first { it.invItem.itemID == item.itemID }.setZero()
            return
        } else {
            cells.forEach { cell ->
                if (cell.invItem.itemID == item.itemID) {
                    cell.setItem(item)
                    return
                }
            }
            cells.first { it.invItem.isEmpty() }.setItem(item)
        }
    }

    override fun selectTool(type: ToolsList) {
        toolCell.image.drawable = TextureRegionDrawable(player.getCurrentTool().image)
    }

    fun getRoot(): VisTable = rootTable


    fun update() {
        cells.forEach { cell ->
            if (cell.isEmpty()) {
                cell.setZero()
            }
        }
    }

    private class InventoryCell(val index: Int,backgroundColor: Color,val select: (current: Int) -> Unit) : WidgetGroup() {
        val bg = TextureRegionDrawable(getTextureDot())
        private val selected = VisImage(bg).apply {
            setSize(44f,44f)
            setPosition(-4f,-4f)
            color = Color.LIME.cpy().apply {
                a=.5f
                b=0.9f
            }
            isVisible = false
            touchable = Touchable.disabled
        }
        var imageBG = VisImage(bg).apply {
            setSize(36f,36f)
            color  = backgroundColor
            onClick {
                select.invoke(index)
            }
        }
        var image: VisImage = VisImage().apply {
            setSize(36f,36f)
            touchable = Touchable.disabled
        }
        val labelCounter = VisLabel("0").apply {
            color = Color.BLACK
            setScale(0.8f)
            touchable = Touchable.disabled
        }
        var invItem = Inventory.InvItem()

        init {
            setSize(36f,36f)
            addActor(imageBG)
            addActor(selected)
            addActor(image)
            addActor(labelCounter)
        }

        fun select() {
            selected.isVisible = true
        }
        fun unselect() {
            selected.isVisible = false
        }
        fun isEmpty(): Boolean {
            return invItem.isEmpty()
        }
        fun setZero() {
            invItem.setZero()
            image.isVisible = false
            labelCounter.isVisible = false
        }
        fun setItem(item: Inventory.InvItem) {
            if (item.itemID!=ItemList.ZERO) {
                invItem.count = 1
                invItem.itemID = item.itemID
                image.isVisible = true
                image.drawable = TextureRegionDrawable(assetAtlas().findRegion(item.itemID.resourcesName))
                labelCounter.isVisible = true
                labelCounter.setText("${item.count}")
            }
            else {
                setZero()
            }
        }

    }
    private class ToolCell(currentTool: ATool,backgroundColor: Color) : WidgetGroup() {
        val bg = VisImage().apply {
            setSize(54f,54f)
            color = backgroundColor
            drawable = TextureRegionDrawable(getTextureDot())
        }
        val image = VisImage().apply {
            setSize(50f,50f)
            drawable = TextureRegionDrawable(currentTool.image)
        }

        init {
            addActor(bg)
            addActor(image)
        }

    }
}


