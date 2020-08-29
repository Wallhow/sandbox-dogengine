package sandbox.sandbox.def.gui

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisImage
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import dogengine.Kernel
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.systems.SystemPriority
import dogengine.ecs.systems.controllers.InputEvent
import dogengine.ecs.systems.controllers.SInputHandler
import dogengine.utils.GameCamera
import dogengine.utils.system
import ktx.vis.table
import sandbox.dev.gui.HInventoryAndTool
import sandbox.dev.ecs.sys.SWorldHandler
import sandbox.dev.gui.BuildConstruction
import sandbox.dev.gui.HCraftTable
import sandbox.sandbox.getTextureDot
import sandbox.sandbox.go.player.Player
import space.earlygrey.shapedrawer.ShapeDrawer

class SMainGUI(private val player: Player) : EntitySystem(SystemPriority.DRAW + 10) {
    private val gameCamera = Kernel.getInjector().getInstance(GameCamera::class.java)
    private val view = gameCamera.getViewport()
    private val gui = Stage(view)
    private val guiHUD = Stage()
    private val image = VisImage()
    private val shapeDrawer = ShapeDrawer(Kernel.getInjector().getInstance(SpriteBatch::class.java), getTextureDot())
    private var preDirectionSee: Player.DirectionSee = Player.DirectionSee.DOWN
    private lateinit var table: VisTable
    private var dirty = true
    private val hInventoryAndTool = HInventoryAndTool(player)
    private val craftMenu = HCraftTable(player)
    private val buildConstruction = BuildConstruction(player,Kernel.getInjector().getInstance(Engine::class.java))
    private val labelCurrentMode = VisLabel("building")
    init {

        toolHitInit()
        //Подписка на событие !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!TODO

        system<SInputHandler> {
            subscribe(InputEvent.KEY_PRESS, craftMenu)
            subscribe(InputEvent.SCREEN_TOUCH,buildConstruction)
        }
        val gll = GlyphLayout()
        gll.setText(VisUI.getSkin().getFont("default-font"),"building")
        labelCurrentMode.isVisible = false
        labelCurrentMode.setPosition(gameCamera.getViewport().worldWidth/2-gll.width/2,60f)

        guiHUD.addActor(craftMenu.getRoot())
        guiHUD.addActor(hInventoryAndTool.getRoot())
        guiHUD.addActor(labelCurrentMode)

        Kernel.getInjector().getInstance(InputMultiplexer::class.java).addProcessor(guiHUD)

        val uiMatrix = gameCamera.getCamera().combined.cpy().setToOrtho2D(0f, 0f,
                gameCamera.getScaledViewport().width,
                gameCamera.getScaledViewport().height)

        guiHUD.batch.projectionMatrix = uiMatrix
    }

    private fun toolHitInit() {
        val padding = 64f
        table = table {
            setPosition(CTransforms[player].getCenterX() - padding, CTransforms[player].getCenterY() - padding)
            setSize(padding * 2, padding * 2)
            add(image).expand().bottom()
            debug = true
        }
        gui.addActor(table)
    }

    override fun update(deltaTime: Float) {
        toolHit()
        craftMenu.update()
        hInventoryAndTool.update()

        buildConstruction.update()

        labelCurrentMode.isVisible = SWorldHandler.itemIDBuild!=null


        guiHUD.act()
        guiHUD.draw()
        //invDockBarViewer.draw(view.camera.viewportWidth, view.camera.viewportHeight, atlas)

    }

    private fun toolHit() {
        fun actionInit(image: Actor) {
            val time = player.getCurrentTool().force.duration / 2f
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
                val dx = view.worldWidth / view.camera.viewportWidth
                val x = CTransforms[player].getCenterX() * dx
                val y = CTransforms[player].getCenterY()
                val pos = Vector2(x, y)

                table.setPosition(pos.x - 64f, pos.y - 64f)
                if (preDirectionSee != player.directionSee) {
                    preDirectionSee = player.directionSee
                    table.reset()
                    table.apply {
                        table.setPosition(pos.x - 64f, pos.y - 64f)
                        setSize(64f * 2, 64f * 2)
                    }
                    when (preDirectionSee) {
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
            if (player.getCurrentTool().type.name_res != image.name) {
                image.name = player.getCurrentTool().type.name_res
                image.drawable = TextureRegionDrawable(player.getCurrentTool().image)
            }
            gui.act()
            gui.draw()
        } else {
            dirty = true
        }
    }

}