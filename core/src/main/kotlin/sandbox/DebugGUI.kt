package sandbox.sandbox

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.Viewport
import com.google.inject.Inject
import com.kotcrab.vis.ui.widget.VisImageButton
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisTable
import dogengine.Kernel
import dogengine.ecs.systems.controllers.EventInputListener
import dogengine.ecs.systems.controllers.InputEvent
import dogengine.ecs.systems.controllers.SInputHandler
import dogengine.utils.Size
import dogengine.utils.log
import dogengine.utils.system
import ktx.actors.onClick
import sandbox.go.environment.ItemList
import sandbox.sandbox.go.assetTextureRegion
import sandbox.sandbox.go.player.Player

class DebugGUI(private val viewport: Viewport
               , private val sb: SpriteBatch) : EventInputListener() {

    private var player: Player? = null
    private val rootTable: VisTable = VisTable()
    private val itemTable: VisTable = VisTable()
    private val root: Stage = Stage(viewport, sb)
    val size: Size = Size(36f*6f, 36*4f)
    val position: Vector2 = Vector2(viewport.screenWidth / 2f,
            viewport.screenHeight / 2f - size.halfWidth)

    init {
        system<SInputHandler> {
            subscrabe(InputEvent.KEY_PRESS, this@DebugGUI)
        }
        Kernel.getInjector().getInstance(InputMultiplexer::class.java).addProcessor(root)
    }

    fun setPlayer(player: Player) {
        this.player = player
        rootTable.apply {
            isVisible = false
            setPosition(0f,0f)
            setSize(viewport.screenWidth*1f,viewport.screenHeight*1f)

        }
        itemTable.apply {
            setBounds(position.x, position.y, size.width, size.height)

            val cols = 5
            var currCol = 1
            ItemList.values().forEach {
                if (it != ItemList.ZERO) {
                    val texture = assetTextureRegion(it.resourcesName)
                    log(it)
                    val icon = TextureRegionDrawable(texture)
                    icon.setMinSize(36f,36f)
                    val button = VisImageButton(icon)
                    button.onClick {
                        player.getInventory().push(it)
                    }
                    itemTable.add(button).space(10f)
                    if (currCol == cols) {
                        currCol = 1
                        itemTable.row()
                    }
                    currCol += 1
                }
            }
        }
        itemTable.setBackground(TextureRegionDrawable(getTextureDot()))
        itemTable.color = Color.LIGHT_GRAY

        val scrollPane = VisScrollPane(itemTable)

        rootTable.add(VisLabel("debug mode on\n" +
                "press Z to show grid and FPS").apply { color = Color.RED }).left().top().padLeft(16f)
        rootTable.add(scrollPane).right().top().padRight(16f).expand()

        root.addActor(rootTable)
    }

    fun updateAndDraw(delta: Float) {
        //itemTable.setPosition(position.x, position.y)
        root.act(delta)
        root.draw()
    }

    override fun keyPressed(key: Int): Boolean {
        if (key == Input.Keys.F6) {
            rootTable.isVisible = !rootTable.isVisible
        }

        return false
    }
}