package sandbox.def.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.kotcrab.vis.ui.layout.VerticalFlowGroup
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import dogengine.Kernel
import dogengine.ecs.systems.controllers.EventInputListener
import dogengine.utils.GameCamera
import dogengine.utils.onLongPress
import ktx.actors.onClick
import sandbox.sandbox.def.def.sys.SWorldHandler
import sandbox.go.environment.ItemList
import sandbox.go.player.inventory.Inventory
import sandbox.go.player.inventory.InventoryObserver
import sandbox.go.player.tools.ATool
import sandbox.sandbox.getTextureDot
import sandbox.sandbox.go.assetAtlas
import sandbox.sandbox.go.player.Player
import sandbox.sandbox.go.player.tools.SelectToolObserver
import sandbox.sandbox.go.player.tools.ToolsList

class HInventoryAndTool(private val player: Player) : EventInputListener(), InventoryObserver, SelectToolObserver, HUD {
    private val dot = getTextureDot()
    private val rootTable: VisTable = VisTable()
    private val horTable = VisTable()
    private val leftHGroup = VerticalFlowGroup(40f)
    private val rightHGroup = VerticalFlowGroup(40f)
    private val cells = Array<InventoryCell>()
    private val backgroundColor = Color.DARK_GRAY.cpy().apply { a = .7f }
    private var currentSelectCell = -1
    private val toolCell: ToolCell
    private val select: (current: Int) -> Unit = {
        if (currentSelectCell != it) {
            if(currentSelectCell!=-1)
                cells[currentSelectCell].unselect()
            currentSelectCell = it
            cells[currentSelectCell].select()
            player.getInventory().currentItem = currentSelectCell
        }
    }

    init {
        val inventory = player.getInventory()
        val view = Kernel.getInjector().getInstance(GameCamera::class.java).getViewport()
        for (i in 0..5) {
            cells.add(InventoryCell(i, backgroundColor, select))
            leftHGroup.addActor(cells[i])
        }
        for (i in 6..11) {
            cells.add(InventoryCell(i, backgroundColor, select))
            rightHGroup.addActor(cells[i])
        }
        horTable.add(leftHGroup)
        toolCell = ToolCell(player.getCurrentTool(), backgroundColor)
        horTable.add(toolCell).spaceRight(58f)
        horTable.add(rightHGroup)

        rootTable.background = TextureRegionDrawable(dot)
        rootTable.setPosition(view.worldWidth / 2f, 12f)
        rootTable.add(horTable)

        inventory.observers.add(this)
        player.toolObservers.add(this)
    }

    override fun countChanged(newCount: Int, oldCount: Int, item: Inventory.InvItem) {
        if (oldCount >= 0 && newCount <= 0) {
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

    override fun getRoot(): Actor = rootTable


    override fun update() {
        cells.forEach { cell ->
            if (cell.isEmpty()) {
                cell.setZero()
            }
            if (cell.invItem.itemID == ItemList.WORKBENCH && cell.isLongPressed) {
                cell.isLongPressed = false

                SWorldHandler.itemIDBuild = cell.invItem.itemID
            }
        }
    }

    override fun longPress(x: Float, y: Float): Boolean {

        return super.longPress(x, y)
    }

    private class InventoryCell(val index: Int, backgroundColor: Color, val select: (current: Int) -> Unit) : WidgetGroup() {
        val bg = TextureRegionDrawable(getTextureDot())
        var isLongPressed: Boolean = false
        private val selected = VisImage(bg).apply {
            setSize(44f, 44f)
            setPosition(-4f, -4f)
            color = Color.LIME.cpy().apply {
                a = .5f
                b = 0.9f
            }
            isVisible = false
            touchable = Touchable.disabled


            val time = 1 / 2f
            this.color.a = 0f
            this.clearActions()
            val a = Actions.sequence(Actions.fadeIn(time, Interpolation.pow4Out), Actions.fadeOut(time))
            val seq = Actions.forever(a)
            this.addAction(seq)

        }
        var imageBG = VisImage(bg).apply {
            setSize(36f, 36f)
            color = backgroundColor
            onClick {
                select.invoke(index)
                isLongPressed = false
            }

            onLongPress {
                isLongPressed = true
                false
            }
        }
        var image: VisImage = VisImage().apply {
            setSize(36f, 36f)
            touchable = Touchable.disabled
        }
        val labelCounter = VisLabel("0").apply {
            color = Color.BLACK
            setScale(0.8f)
            touchable = Touchable.disabled
        }
        var invItem = Inventory.InvItem()

        init {
            setSize(36f, 36f)
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

        fun isSelected(): Boolean = selected.isVisible
        fun isEmpty(): Boolean {
            return invItem.isEmpty()
        }

        fun setZero() {
            invItem.setZero()
            image.isVisible = false
            labelCounter.isVisible = false
        }

        fun setItem(item: Inventory.InvItem) {
            if (item.itemID != ItemList.ZERO) {
                invItem.count = 1
                invItem.itemID = item.itemID
                image.isVisible = true
                image.drawable = TextureRegionDrawable(assetAtlas().findRegion(item.itemID.resourcesName))
                labelCounter.isVisible = true
                labelCounter.setText("${item.count}")
            } else {
                setZero()
            }
        }

    }

    private class ToolCell(currentTool: ATool, backgroundColor: Color) : WidgetGroup() {
        val bg = VisImage().apply {
            setSize(54f, 54f)
            color = backgroundColor
            drawable = TextureRegionDrawable(getTextureDot())
        }
        val image = VisImage().apply {
            setSize(50f, 50f)
            drawable = TextureRegionDrawable(currentTool.image)
        }

        init {
            addActor(bg)
            addActor(image)
        }

    }
}


