package sandbox.dev.gui

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.*
import com.strongjoshua.console.GUIConsole
import dogengine.Kernel
import dogengine.ecs.systems.controllers.EventInputListener
import dogengine.ecs.systems.controllers.InputEvent
import dogengine.ecs.systems.controllers.SInputHandler
import dogengine.ecs.systems.tilemap.SMap2D
import dogengine.ecs.systems.utility.STime
import dogengine.shadow2d.systems.SShadow2D
import dogengine.utils.GameCamera
import dogengine.utils.Size
import dogengine.utils.extension.get
import dogengine.utils.extension.injector
import dogengine.utils.log
import dogengine.utils.system
import ktx.actors.onClick
import sandbox.sandbox.ConsoleCommands
import sandbox.sandbox.getTextureDot
import sandbox.sandbox.go.assetTextureRegion
import sandbox.sandbox.go.objects.ItemList
import sandbox.sandbox.go.player.Player
import sandbox.sandbox.input.MainInput

class DebugGUI : EventInputListener() {
    companion object {
        const val MSG_DEBUG_GET_MAP_TEXTURE: Int = 3000
        const val MSG_DEBUG_GIVE_MAP_TEXTURE: Int = 3001
        const val MSG_DEBUG_SHOW_MAP_TEXTURE: Int = 3002
        const val MSG_DEBUG_PLAYER_COLLIDE: Int = 3003
    }

    private val console = GUIConsole(true).apply {
        this.displayKeyID = Input.Keys.F1
        this.setCommandExecutor(ConsoleCommands())
        this.setSizePercent(100f, 45f)
        this.setPositionPercent(0f, 100f)
        this.window.isMovable = false
        this.window.isResizable = false
        this.setNoHoverAlpha(0.5f)
        this.setHoverAlpha(0.5f)
    }
    private var player: Player? = null
    private val rootTable: VisTable = VisTable()
    private val itemTable: VisTable = VisTable()
    private val mapImage: VisImage = VisImage()
    private val root: Stage = Stage()
    private val label: VisLabel = VisLabel()
    val size: Size = Size(36f * 6f, 36 * 4f)
    val position: Vector2
    val gameCamera: GameCamera

    init {
        system<SInputHandler> {
            subscribe(InputEvent.KEY_PRESS, this@DebugGUI)
        }
        Kernel.getInjector().getInstance(InputMultiplexer::class.java).apply {
            addProcessor(root)
            addProcessor(console.inputProcessor)
        }

        injector[MessageManager::class.java].apply {
            addListener({
                val img = (it.extraInfo as Texture)
                if (mapImage.drawable == null) {
                    val tr = TextureRegion(img).apply { flip(false,true)}
                    mapImage.drawable = TextureRegionDrawable(tr)
                    //mapImage.setSize(256f,256f)
                    mapImage.sizeBy(tr.regionWidth.toFloat())
                    mapImage.scaleBy(256f/mapImage.width)
                    log(mapImage.width)
                    mapImage.isVisible = true
                }
                true
            }, MSG_DEBUG_GIVE_MAP_TEXTURE)
            addListener({
                val visible = (it.extraInfo as Boolean)
                mapImage.isVisible = visible
                true
            }, MSG_DEBUG_SHOW_MAP_TEXTURE)
        }

        gameCamera = Kernel.getInjector().getInstance(GameCamera::class.java)
        val uiMatrix = Matrix4().apply {
            setToOrtho2D(0f, 0f,
                    gameCamera.getScaledViewport().width,
                    gameCamera.getScaledViewport().height)
        }
        root.batch.projectionMatrix = uiMatrix
        position = Vector2(gameCamera.getViewport().worldWidth,
                gameCamera.getViewport().worldHeight - size.halfHeight)
    }

    fun setPlayer(player: Player) {
        this.player = player
        rootTable.apply {
            isVisible = false
            setPosition(0f, 0f)
            setSize(gameCamera.getViewport().worldWidth, gameCamera.getViewport().worldHeight)
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
                    icon.setMinSize(36f, 36f)
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

        rootTable.apply {
            add(label.apply { color = Color.FIREBRICK }).left().top().padLeft(16f).expand()
            add(scrollPane).right().top().padRight(16f)
            row().colspan(2).expand()
            add(mapImage.apply {
                color.a = 0.85f
            }).expand()
        }



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
                "press Z to show grid\n" //+
                //"detail level shadow = ${SShadow2D.getDetailLevelShadow()}\n"
        if (MainInput.save) {
            text += "world saved ;)"
        }
        label.setText(text)

        root.draw()

        console.draw()
    }

    override fun keyPressed(key: Int): Boolean {
        if (key == Input.Keys.F6) {
            rootTable.isVisible = !rootTable.isVisible
            injector[MessageManager::class.java].dispatchMessage(MSG_DEBUG_GET_MAP_TEXTURE)
        }

        return false
    }
}