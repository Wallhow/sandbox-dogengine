package sandbox.sandbox.go

import com.anupcowkur.statelin.TriggerHandler
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.create
import dogengine.ecs.components.draw.*
import dogengine.ecs.components.utility.logic.CUpdate
import dogengine.ecs.components.utility.logic.CVelocity
import dogengine.ecs.def.GameEntity
import dogengine.ecs.systems.controllers.CControllable
import dogengine.ecs.systems.controllers.EventListener
import dogengine.es.redkin.physicsengine2d.bodies.RectangleBody
import dogengine.es.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.R
import sandbox.dogengine.ashley.components.utility.CCameraLook
import sandbox.dogengine.ashley.components.utility.CName
import sandbox.dogengine.ashley.components.utility.CStateMachine
import sandbox.dogengine.ecs.components.utility.logic.CTransforms
import sandbox.dogengine.ecs.components.utility.logic.updateZIndex
import sandbox.sandbox.def.redkin.physicsengine2d.CDefaultPhysics2d
import sandbox.sandbox.def.redkin.physicsengine2d.createBody

class Player(val am: AssetManager, pos: Vector2) : GameEntity(), EventListener {
    private var directionSee = DirectionSee.DOWN

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
            createSequence(T.W_DOWN.ordinal1, 0.10f) { isRepeat = true; putFrames(intArrayOf(1, 2, 3, 4)) }
            createSequence(T.W_UP.ordinal1, 0.10f) { isRepeat = true; putFrames(intArrayOf(6, 7, 8, 9)) }
            createSequence(T.W_LEFT.ordinal1, 0.10f) { isRepeat = true; putFrames(intArrayOf(11, 12, 13, 14)) }
            createSequence(T.W_RIGHT.ordinal1, 0.10f) { isRepeat = true; putFrames(intArrayOf(16, 17, 18, 19)) }
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
        /*create<CJBumpAABB> {
            scaleSize.x = 0.2f
            scaleSize.y = 0.2f
            dynamic = true
            positionOffset.x = 32 - 5f
            positionOffset.y = 10f
        }*/

        create<CDefaultPhysics2d> {
            val t = CTransforms[this@Player]
            createBody(t, 16f, 0f, t.size.width / 2, t.size.height / 4, Types.TYPE.DYNAMIC, name)
            rectangleBody = RectangleBody(16f, 0f, t.size.width / 2, t.size.height / 4, Types.TYPE.DYNAMIC, name)
        }
        create<CUpdate> {
            func = {
                CTransforms[this@Player].updateZIndex()
                when {
                    moveUp -> {
                        if (moveRight || moveLeft) {
                            CStateMachine[this@Player].setTrigger(T.W_UP.name.hashCode())
                        } else {
                            CStateMachine[this@Player].setTrigger(T.W_UP.name.hashCode())
                        }
                        CVelocity[this@Player].vector.y = speed
                    }
                    moveDown -> {
                        if (moveRight || moveLeft) {
                            CStateMachine[this@Player].setTrigger(T.W_DOWN.name.hashCode())
                        } else {
                            CStateMachine[this@Player].setTrigger(T.W_DOWN.name.hashCode())
                        }
                        CVelocity[this@Player].vector.y = -speed
                    }
                    moveLeft -> {
                        CStateMachine[this@Player].setTrigger(T.W_LEFT.name.hashCode())
                        CVelocity[this@Player].vector.x = -speed
                    }
                    moveRight -> {
                        CStateMachine[this@Player].setTrigger(T.W_RIGHT.name.hashCode())
                        CVelocity[this@Player].vector.x = speed
                    }
                }

            }
        }

        create<CStateMachine> { createState(this) }


    }

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
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_RIGHT.name.hashCode())) { setState(T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_LEFT.name.hashCode())) { setState(T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_UP.name.hashCode())) { setState(T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_DOWN.name.hashCode())) { setState(T.W_DOWN.name) })

            addTriggerHandler(TriggerHandler(sWalkLeft, getTrigger(T.W_RIGHT.name.hashCode())) { setState(T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkLeft, addTrigger(T.W_NON.name.hashCode())) { setState(T.W_NON.name) })
            addTriggerHandler(TriggerHandler(sWalkLeft, getTrigger(T.W_UP.name.hashCode())) { setState(T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkLeft, getTrigger(T.W_DOWN.name.hashCode())) { setState(T.W_DOWN.name) })

            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(T.W_NON.name.hashCode())) { setState(T.W_NON.name) })
            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(T.W_LEFT.name.hashCode())) { setState(T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(T.W_UP.name.hashCode())) { setState(T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkRight, getTrigger(T.W_DOWN.name.hashCode())) { setState(T.W_DOWN.name) })

            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(T.W_RIGHT.name.hashCode())) { setState(T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(T.W_LEFT.name.hashCode())) { setState(T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(T.W_UP.name.hashCode())) { setState(T.W_UP.name) })
            addTriggerHandler(TriggerHandler(sWalkDown, getTrigger(T.W_NON.name.hashCode())) { setState(T.W_NON.name) })

            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(T.W_RIGHT.name.hashCode())) { setState(T.W_RIGHT.name) })
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(T.W_LEFT.name.hashCode())) { setState(T.W_LEFT.name) })
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(T.W_NON.name.hashCode())) { setState(T.W_NON.name) })
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(T.W_DOWN.name.hashCode())) { setState(T.W_DOWN.name) })
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

    override fun keyPressed(keyCode: Int) {
        when (keyCode) {
            Input.Keys.W -> moveUp = true
            Input.Keys.S -> moveDown = true
            Input.Keys.A -> moveLeft = true
            Input.Keys.D -> moveRight = true
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
            CStateMachine[this@Player].setTrigger(T.W_NON.name.hashCode())
            CVelocity[this@Player].vector.set(0f, 0f)
        }
    }
}