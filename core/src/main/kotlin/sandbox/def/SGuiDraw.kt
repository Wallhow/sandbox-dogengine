package sandbox.sandbox.def

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
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
import dogengine.ecs.systems.controllers.InputEvent
import dogengine.ecs.systems.controllers.SInputHandler
import dogengine.utils.TTFFont
import ktx.vis.table
import sandbox.R
import sandbox.def.craftsys.HCraftTable
import sandbox.go.player.inventory.InventoryView
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
    private val invDockBarViewer = InventoryView(sb, fnt, player.getInventory())

    private val craftMenu = HCraftTable(player, sb)

    init {
        toolHitInit()
        //Подписка на событие !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!TODO
        invDockBarViewer.init()
        Kernel.getInjector().getInstance(SInputHandler::class.java).sign(InputEvent.SCREEN_TOUCH, invDockBarViewer)


        fnt.create(26, Color.LIGHT_GRAY)
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

    override fun update(deltaTime: Float) {
        toolHit()
        invDockBarViewer.update()
        invDockBarViewer.draw(view.camera.viewportWidth, view.camera.viewportHeight, atlas)

        craftMenu.update()
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

}