package sandbox.sandbox.go.player

import com.anupcowkur.statelin.TriggerHandler
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import dogengine.ecs.components.create
import dogengine.ecs.components.createBody
import dogengine.ecs.components.draw.*
import dogengine.ecs.components.utility.CName
import dogengine.ecs.components.utility.CStateMachine
import dogengine.ecs.components.utility.logic.*
import dogengine.ecs.components.utility.visible.CCameraLook
import dogengine.ecs.def.GameEntity
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import dogengine.utils.log
import dogengine.utils.vec2
import sandbox.R
import sandbox.dogengine.ecs.components.controllers.CControllable
import sandbox.dogengine.ecs.components.controllers.EventListener
import sandbox.go.player.tools.ATool
import sandbox.go.player.tools.ToolAxeWood
import sandbox.go.player.inventory.Inventory
import sandbox.sandbox.go.environment.ObjectList
import sandbox.sandbox.go.environment.objects.buiding.CWorkbench


class Player(val am: AssetManager, pos: Vector2) : GameEntity(), EventListener {
    var directionSee = DirectionSee.DOWN
    private val tool = ToolAxeWood(this, am)
    private val inventory = Inventory(this)
    private val direction = vec2(0f,0f)

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
        create<CUpdate> {
            func = {
                tool.update(it)
                CTransforms[this@Player].updateZIndex()
                movePlayer()
            }
        }

        create<CStateMachine> { createState(this) }


    }
    private val hashUp = T.W_UP.hashCode()
    private val hashDown = T.W_DOWN.hashCode()
    private val hashLeft = T.W_LEFT.hashCode()
    private val hashRight = T.W_RIGHT.hashCode()
    private fun movePlayer() {
        var speedDelta = speed
        if(direction.x== 1f && direction.y==1f || direction.x== 1f && direction.y==-1f ||
                direction.x== -1f && direction.y==1f || direction.x== -1f && direction.y==-1f) {
            speedDelta-=speedDelta*0.2f
        }
        CVelocity[this@Player].vector.set(direction.cpy().scl(speedDelta))
        if(!CVelocity[this@Player].vector.isZero) {
            val a = 270 - direction.angle()
            if (a == 180f) {
                CStateMachine[this@Player].setTrigger(hashUp)
            } else if (a == 90f) {
                CStateMachine[this@Player].setTrigger(hashLeft)
            } else if (a == 270f) {
                CStateMachine[this@Player].setTrigger(hashRight)
            } else if (a == 0f) {
                CStateMachine[this@Player].setTrigger(hashDown)
            } else if (a > 0 && a < 90) {
                CStateMachine[this@Player].setTrigger(hashDown)
            } else if( a > 90 && a < 180) {
                CStateMachine[this@Player].setTrigger(hashUp)
            } else if( a > 180 && a < 270) {
                CStateMachine[this@Player].setTrigger(hashUp)
            }else if( a == -45f) {
                CStateMachine[this@Player].setTrigger(hashDown)
            }
        } else {
            CStateMachine[this@Player].setTrigger(T.W_NON.hashCode())
        }
    }

    fun getCurrentTool(): ATool = tool
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
            Input.Keys.W -> {
                if(!moveDown) {
                    moveUp = true
                    direction.y = 1f
                }
            }
            Input.Keys.S -> {
                if(!moveUp) {
                    moveDown = true
                    direction.y = -1f
                }
            }
            Input.Keys.A -> {
                if(!moveRight) {
                    moveLeft = true
                    direction.x = -1f
                }
            }
            Input.Keys.D -> {
                if(!moveLeft) {
                    moveRight = true
                    direction.x = 1f
                }
            }

            Input.Keys.Q -> { inventory.dropCurrentItem() }
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
                    direction.y = 0f
            }
            Input.Keys.S -> {
                moveDown = false
                if (!moveUp)
                    direction.y = 0f
            }
            Input.Keys.A -> {
                moveLeft = false
                if (!moveRight)
                    direction.x = 0f
            }
            Input.Keys.D -> {
                moveRight = false
                if (!moveLeft)
                    direction.x = 0f
            }
        }
        if (!moveRight && !moveLeft && !moveDown && !moveUp) {
            CStateMachine[this@Player].setTrigger(T.W_NON.hashCode())
            CVelocity[this@Player].vector.set(0f, 0f)
            direction.setZero()
        }
    }

    private fun hit() {
        tool.hit()
    }

    fun getInventory() : Inventory {
        return inventory
    }

    val workbenchNear: ArrayMap<ObjectList,Boolean> = ArrayMap()
}

