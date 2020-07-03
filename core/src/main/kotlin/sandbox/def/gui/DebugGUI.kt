package sandbox.sandbox.def.gui

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.kotcrab.vis.ui.widget.VisImageButton
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisTable
import dogengine.Kernel
import dogengine.ecs.systems.controllers.EventInputListener
import dogengine.ecs.systems.controllers.InputEvent
import dogengine.ecs.systems.controllers.SInputHandler
import dogengine.ecs.systems.utility.STime
import dogengine.shadow2d.systems.SShadow2D
import dogengine.utils.GameCamera
import dogengine.utils.Size
import dogengine.utils.log
import dogengine.utils.system
import ktx.actors.onClick
import sandbox.go.environment.ItemList
import sandbox.sandbox.getTextureDot
import sandbox.sandbox.go.assetTextureRegion
import sandbox.sandbox.go.player.Player
import sandbox.sandbox.input.MainInput

class DebugGUI : EventInputListener() {

    private var player: Player? = null
    private val rootTable: VisTable = VisTable()
    private val itemTable: VisTable = VisTable()
    private val root: Stage = Stage()
    private val label: VisLabel = VisLabel()
    private val batch = Kernel.getInjector().getInstance(SpriteBatch::class.java)
    val size: Size = Size(36f*6f, 36*4f)
    val position: Vector2
    val gameCamera: GameCamera
    init {
        system<SInputHandler> {
            subscribe(InputEvent.KEY_PRESS, this@DebugGUI)
        }
        Kernel.getInjector().getInstance(InputMultiplexer::class.java).addProcessor(root)
        gameCamera = Kernel.getInjector().getInstance(GameCamera::class.java)
        val uiMatrix = Matrix4().apply {  setToOrtho2D(0f, 0f,
                gameCamera.getScaledViewport().width,
                gameCamera.getScaledViewport().height) }
        root.batch.projectionMatrix = uiMatrix
        position = Vector2(gameCamera.getViewport().worldWidth,
                gameCamera.getViewport().worldHeight- size.halfHeight)
    }

    fun setPlayer(player: Player) {
        this.player = player
        rootTable.apply {
            isVisible = false
            setPosition(0f,0f)
            setSize(gameCamera.getViewport().worldWidth,gameCamera.getViewport().worldHeight)

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
        itemTable.background = TextureRegionDrawable(getTextureDot())
        itemTable.color = Color.LIGHT_GRAY

        val scrollPane = VisScrollPane(itemTable)

        rootTable.add(label.apply { color = Color.FIREBRICK }).left().top().padLeft(16f)
        rootTable.add(scrollPane).right().top().padRight(16f).expand()

        root.addActor(rootTable)
    }

    fun updateAndDraw(delta: Float) {
        root.act(delta)
        var time = 0f
        var hour = 0f
        var day = 0f
        system<STime> {
            time = getCurrentMinute()
            hour = getCurrentHour()
            day = getCurrentDay()
        }
        var text = "FPS : ${Gdx.graphics.framesPerSecond}\n" +
                "Count entities : ${Kernel.getInjector().getInstance(Engine::class.java).entities.size()}\n" +
                "Time [ ${hour.toInt()} : ${time.toInt()}]\n" +
                "Day [ ${day.toInt()} ]\n" +
                "press Z to show grid\n"+
                "detail level shadow = ${SShadow2D.getDetailLevelShadow()}\n"
        if(MainInput.save) {
            text+="world saved ;)"
        }
        label.setText(text)

        root.draw()
    }

    override fun keyPressed(key: Int): Boolean {
        if (key == Input.Keys.F6) {
            rootTable.isVisible = !rootTable.isVisible
        }

        return false
    }
}