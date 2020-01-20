package sandbox.sandbox.def

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.assets.AssetManager
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
import ktx.vis.table
import sandbox.R
import sandbox.sandbox.go.Player
import sandbox.sandbox.go.items.Item
import sandbox.sandbox.go.items.Items

class SGUI(private val player: Player) : EntitySystem(SystemPriority.DRAW + 10) {
    private val view = Kernel.getInjector().getInstance(Viewport::class.java)
    private val gui = Stage(view)
    private val image = VisImage()
    private var beforeDirectionSee: Player.DirectionSee = Player.DirectionSee.DOWN
    private var currentTool = ""
    private lateinit var table: VisTable
    private var dirty = true

    val inventoryView = Stage(view)
    lateinit var horGroup: VisTable
    private val atlas: TextureAtlas = Kernel.getInjector().getInstance(AssetManager::class.java).get<TextureAtlas>(R.matlas0)


    init {
        toolHitInit()
        player.getInventory().push(object : Item {
            override val type: Items = Items.WOOD
        })
        player.getInventory().push(object : Item {
            override val type: Items = Items.GRASS
        })
        player.getInventory().push(object : Item {
            override val type: Items = Items.GRASS
        })
        player.getInventory().push(object : Item {
            override val type: Items = Items.GRASS
        })
        inventoryViewerInit()

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
        val halfHeight = viewHeight / 2
        inventoryView.addActor(table {
            setPosition(halfWidth-36f*4, 50f)
            setSize(36f*8, 36f)
            center()
            player.getInventory().readAll().forEach {
                this.add(VisImage(TextureRegionDrawable(atlas.findRegion(it.first.name_res)))).fill()
                add(label("${it.second}"))
            }

            debug = true
        })
    }


    override fun update(deltaTime: Float) {
        toolHit()

        inventoryView.act()
        inventoryView.draw()
    }

    private fun toolHit() {
        fun actionInit(image: Actor) {
            val time = player.getCurrentTool().hitSpeed / 2f
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