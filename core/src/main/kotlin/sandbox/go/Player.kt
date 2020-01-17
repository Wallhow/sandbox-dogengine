package sandbox.sandbox.go

import com.anupcowkur.statelin.TriggerHandler
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.*
import dogengine.ecs.components.draw.*
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.CName
import dogengine.ecs.components.utility.CStateMachine
import dogengine.ecs.components.utility.logic.*
import dogengine.ecs.components.utility.visible.CCameraLook
import dogengine.ecs.def.GameEntity
import dogengine.es.redkin.physicsengine2d.bodies.RectangleBody
import dogengine.es.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.R
import sandbox.dogengine.ecs.components.controllers.CControllable
import sandbox.dogengine.ecs.components.controllers.EventListener


class Player(val am: AssetManager, pos: Vector2) : GameEntity(), EventListener {
    var directionSee = DirectionSee.DOWN
    private val tool = AxeTools(this, engine, am)


    init {
        name = "player"
        create<CTransforms> {
            position.set(pos)
            size = Size(64f, 48f)
        }
        create<CAtlasRegion> {
            atlas = am.get(R.matlas0, TextureAtlas::class.java)
            nameRegion = "knight"
            drawLayer = CDrawable.DrawLayer.YES_EFFECT
        }
        create<CAtlasRegionAnimation> {
            //createSequence(T.W_NON.ordinal, 0.4f) { isRepeat = true; putFrames(intArrayOf(1, 5)) }
            createSequence(T.W_DOWN.ordinal1, 0.05f) { isRepeat = true; putFrames(intArrayOf(1, 2, 3, 4)) }
            createSequence(T.W_UP.ordinal1, 0.05f) { isRepeat = true; putFrames(intArrayOf(6, 7, 8, 9)) }
            createSequence(T.W_LEFT.ordinal1, 0.05f) { isRepeat = true; putFrames(intArrayOf(11, 12, 13, 14)) }
            createSequence(T.W_RIGHT.ordinal1, 0.05f) { isRepeat = true; putFrames(intArrayOf(16, 17, 18, 19)) }
            createSequence(DirectionSee.UP.i, 0.4f) { isRepeat = true; putFrames(intArrayOf(6, 8, 10)) }
            createSequence(DirectionSee.DOWN.i, 0.4f) { isRepeat = true; putFrames(intArrayOf(1, 5)) }
            createSequence(DirectionSee.LEFT.i, 0.4f) { isRepeat = true; putFrames(intArrayOf(11, 13, 15)) }
            createSequence(DirectionSee.RIGHT.i, 0.4f) { isRepeat = true; putFrames(intArrayOf(16, 18, 20)) }

            currentSequence(DirectionSee.DOWN.ordinal)
        }
        create<CName> {
            name = "player"
        }

        create<CCameraLook> {}
        create<CControllable> { eventListener = this@Player }
        create<CVelocity> {
            vector.set(0f, 0f)
        }
        create<CDefaultPhysics2d> {
            val t = CTransforms[this@Player]
            val bodyW = t.size.width / 3
            createBody(t, t.size.width / 2 - bodyW / 2, 4f, bodyW, t.size.height / 4, Types.TYPE.DYNAMIC, name)
        }
        val hashUp = T.W_UP.hashCode()
        val hashDown = T.W_DOWN.hashCode()
        val hashLeft = T.W_LEFT.hashCode()
        val hashRight = T.W_RIGHT.hashCode()
        create<CUpdate> {
            func = {
                tool.update(it)

                CTransforms[this@Player].updateZIndex()
                when {
                    moveUp -> {
                        if (moveRight || moveLeft) {
                            CStateMachine[this@Player].setTrigger(hashUp)
                        } else {
                            CStateMachine[this@Player].setTrigger(hashUp)
                        }
                        CVelocity[this@Player].vector.y = speed
                    }
                    moveDown -> {
                        if (moveRight || moveLeft) {
                            CStateMachine[this@Player].setTrigger(hashDown)
                        } else {
                            CStateMachine[this@Player].setTrigger(hashDown)
                        }
                        CVelocity[this@Player].vector.y = -speed
                    }
                    moveLeft -> {
                        when {
                            moveUp -> {
                                CStateMachine[this@Player].setTrigger(hashUp)
                            }
                            moveDown -> {
                                CStateMachine[this@Player].setTrigger(hashDown)
                            }
                            else -> {
                                CStateMachine[this@Player].setTrigger(hashLeft)
                                CVelocity[this@Player].vector.x = -speed
                            }
                        }
                    }
                    moveRight -> {
                        when {
                            moveUp -> {
                                CStateMachine[this@Player].setTrigger(hashUp)
                            }
                            moveDown -> {
                                CStateMachine[this@Player].setTrigger(hashDown)
                            }
                            else -> {
                                CStateMachine[this@Player].setTrigger(hashRight)
                                CVelocity[this@Player].vector.x = speed
                            }
                        }
                    }
                }

            }
        }

        create<CStateMachine> { createState(this) }


    }

    fun getCurrentTool(): Tools = tool
    private fun createState(component: CStateMachine) {
        component.apply {
            val sWalkRight = createState(T.W_RIGHT.name) {
                CAtlasRegionAnimation[this@Player].currentSequence(T.W_RIGHT.ordinal1)
                directionSee = DirectionSee.RIGHT
            }
            val sWalkLeft = createState(T.W_LEFT.name) {
                CAtlasRegionAnimation[this@Player].currentSequence(T.W_LEFT.ordinal1)
                directionSee = DirectionSee.LEFT
            }
            val sWalkUp = createState(T.W_UP.name) {
                directionSee = DirectionSee.UP
                CAtlasRegionAnimation[this@Player].currentSequence(T.W_UP.ordinal1)
            }
            val sWalkDown = createState(T.W_DOWN.name) {
                CAtlasRegionAnimation[this@Player].currentSequence(T.W_DOWN.ordinal1)
                directionSee = DirectionSee.DOWN
            }
            val sWalkNon = createState(T.W_NON.name) {
                CAtlasRegionAnimation[this@Player].currentSequence(directionSee.i)
            }
            initMachine(sWalkNon)
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_RIGHT.hashCode())) { setState(T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_LEFT.hashCode())) { setState(T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_UP.hashCode())) { setState(T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_DOWN.hashCode())) { setState(T.W_DOWN.name) })

            addTriggerHandler(TriggerHandler(sWalkLeft, getTrigger(T.W_RIGHT.hashCode())) { setState(T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkLeft, addTrigger(T.W_NON.hashCode())) { setState(T.W_NON.name) })
            addTriggerHandler(TriggerHandler(sWalkLeft, getTrigger(T.W_UP.hashCode())) { setState(T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkLeft, getTrigger(T.W_DOWN.hashCode())) { setState(T.W_DOWN.name) })

            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(T.W_NON.hashCode())) { setState(T.W_NON.name) })
            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(T.W_LEFT.hashCode())) { setState(T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(T.W_UP.hashCode())) { setState(T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(T.W_DOWN.hashCode())) { setState(T.W_DOWN.name) })

            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(T.W_RIGHT.hashCode())) { setState(T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(T.W_LEFT.hashCode())) { setState(T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(T.W_UP.hashCode())) { setState(T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(T.W_NON.hashCode())) { setState(T.W_NON.name) })

            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(T.W_RIGHT.hashCode())) { setState(T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(T.W_LEFT.hashCode())) { setState(T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(T.W_NON.hashCode())) { setState(T.W_NON.name) })
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(T.W_DOWN.hashCode())) { setState(T.W_DOWN.name) })
        }
    }

    //triggers
    enum class T(val ordinal1: Int) {
        W_RIGHT(2),
        W_LEFT(1),
        W_UP(3),
        W_DOWN(4),
        W_NON(0)
    }

    //Куда смотрит игрок
    enum class DirectionSee(val i: Int) {
        UP(5), DOWN(0), LEFT(6), RIGHT(7)
    }

    private val speed = 200f
    var moveUp = false
    var moveDown = false
    var moveLeft = false
    var moveRight = false

    override fun keyJustPressed(keyCode: Int) {
        when (keyCode) {
            Input.Keys.W -> moveUp = true
            Input.Keys.S -> moveDown = true
            Input.Keys.A -> moveLeft = true
            Input.Keys.D -> moveRight = true
            //Input.Keys.SPACE -> hit()
        }
    }

    override fun keyPressed(keyCode: Int, input: Input) {
        if (keyCode == Input.Keys.SPACE) {
            hit()
        }
    }

    override fun keyReleased(keyCode: Int) {
        when (keyCode) {
            Input.Keys.W -> {
                moveUp = false
                if (!moveDown)
                    CVelocity[this@Player].vector.y = 0f
            }
            Input.Keys.S -> {
                moveDown = false
                if (!moveUp)
                    CVelocity[this@Player].vector.y = 0f
            }
            Input.Keys.A -> {
                moveLeft = false
                if (!moveRight)
                    CVelocity[this@Player].vector.x = 0f
            }
            Input.Keys.D -> {
                moveRight = false
                if (!moveLeft)
                    CVelocity[this@Player].vector.x = 0f
            }
        }
        if (!moveRight && !moveLeft && !moveDown && !moveUp) {
            CStateMachine[this@Player].setTrigger(T.W_NON.hashCode())
            CVelocity[this@Player].vector.set(0f, 0f)
        }
    }

    private fun hit() {
        tool.hit()
    }

    interface ITool {
        var hitSpeed: Float
        var isActive: Boolean
        var power: Float
        var name: String
        val image: TextureRegion?
        fun hit()
        fun update(delta: Float)
    }

    open class Tools : ITool {
        override var hitSpeed = 0.4f
        override var isActive = false
        private var time = 0f
        override var power = 0f
        override var name = "non"
        override var image: TextureRegion? = null

        override fun hit() {
            isActive = true
        }

        override fun update(delta: Float) {
            if (isActive) {
                time += delta
                if (time >= hitSpeed) {
                    isActive = false
                    time = 0f
                }
            }
        }
    }

    class AxeTools(private val player: Player, private val engine: Engine, am: AssetManager) : Tools() {
        init {
            hitSpeed = 0.5f
            power = 15f
            name = "axe_item"
            image = am.get(R.matlas0, TextureAtlas::class.java).findRegion(name)
        }

        override fun hit() {
            if (!isActive) {
                super.hit()
                engine.addEntity(createAxe())
            }
        }

        private fun createAxe(): Entity {
            return engine.createEntity {
                components {
                    val pp = CTransforms[player]
                    val size2 = 24f
                    create<CTransforms> {
                        size = Size(size2, size2)
                        position.set(pp.position.x, pp.position.y)
                        zIndex = Int.MAX_VALUE
                    }
                    create<CUpdate> {
                        var time = 0f
                        func = {
                            time += it
                            if (time >= 0.15f) {
                                create<CDeleteMe>()
                            }
                            updatePositionTool(player, this@components, CDefaultPhysics2d[this@components].rectangleBody!!)
                        }
                    }
                    create<CDefaultPhysics2d> {
                        this.type = Types.TYPE.SENSOR
                        this.name = this@AxeTools.name
                        val t = CTransforms[this@components]
                        var nWidth = 0f
                        var nHeight = 0f
                        if (player.directionSee == DirectionSee.DOWN ||
                                player.directionSee == DirectionSee.UP) {
                            nWidth = t.size.width
                            nHeight = t.size.height + t.size.halfHeight
                        } else if (player.directionSee == DirectionSee.RIGHT ||
                                player.directionSee == DirectionSee.LEFT) {
                            nWidth = t.size.width + t.size.halfWidth
                            nHeight = t.size.height
                        }

                        createBody(CTransforms[player], 0f, 0f, nWidth, nHeight,
                                Types.TYPE.DYNAMIC, name)
                                .apply {
                                    updatePositionTool(player, this@components, this)
                                    createSensor()
                                }
                    }
                }
            }
        }
    }

}

fun Player.ITool.updatePositionTool(player: Player, tool: Entity, rectangleBody: RectangleBody) {
    val t = CTransforms[tool]
    val pos = Vector2()
    val pp = CTransforms[player]
    when (player.directionSee) {
        Player.DirectionSee.UP -> {
            pos.set(pp.position.x + pp.size.halfWidth - t.size.halfWidth,
                    pp.position.y + pp.size.height - (t.size.height + t.size.halfHeight) / 2)
        }
        Player.DirectionSee.DOWN -> {
            pos.set(pp.position.x + pp.size.halfWidth - t.size.halfWidth,
                    pp.position.y - pp.size.height + (t.size.height + t.size.halfHeight) / 2 - 4f)
        }
        Player.DirectionSee.LEFT -> {
            pos.set(pp.position.x - pp.size.halfWidth + (t.size.width + t.size.halfWidth) / 2 - 4f,
                    pp.position.y + pp.size.halfHeight - t.size.halfHeight)
        }
        Player.DirectionSee.RIGHT -> {
            pos.set(pp.position.x + pp.size.width - (t.size.width + t.size.halfWidth) / 2,
                    pp.position.y + pp.size.halfHeight - t.size.halfHeight)
        }
    }
    rectangleBody.apply {
        setX(pos.x)
        setY(pos.y)
    }
}

