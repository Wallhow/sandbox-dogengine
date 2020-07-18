package sandbox.sandbox.go.mobs

import com.anupcowkur.statelin.TriggerHandler
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.GdxRuntimeException
import dogengine.ecs.components.create
import dogengine.ecs.components.createBody
import dogengine.ecs.components.draw.*
import dogengine.ecs.components.utility.CName
import dogengine.ecs.components.utility.CStateMachine
import dogengine.ecs.components.utility.logic.*
import dogengine.ecs.components.utility.visible.CCameraLook
import dogengine.ecs.def.GameEntity
import dogengine.ecs.systems.flexbatch.CBump
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import dogengine.utils.vec2
import sandbox.R
import sandbox.dogengine.ecs.components.controllers.CControllable
import sandbox.sandbox.go.player.Player

class Pig (val am: AssetManager, pos: Vector2) : GameEntity() {
    var directionSee = Player.DirectionSee.DOWN
    private val direction = vec2(0f,0f)

    init {
        name = "pig"
        create<CTransforms> {
            position.set(pos)
            size = Size(48f, 48f)
        }
        create<CAtlasRegion> {
            atlas = am.get(R.matlas0, TextureAtlas::class.java)
            nameRegion = "pig"
            drawLayer = CDraw.DrawLayer.YES_EFFECT
        }
        create<CAtlasRegionAnimation> {
            createSequence(DirectionSee.DOWN.i, 0.20f) { isRepeat = true; putFrames(intArrayOf(1, 2, 3)) }
            createSequence(DirectionSee.UP.i, 0.20f) { isRepeat = true; putFrames(intArrayOf(10,11,12)) }
            createSequence(DirectionSee.LEFT.i, 0.2f) { isRepeat = true; putFrames(intArrayOf(4, 5, 6)) }
            createSequence(DirectionSee.RIGHT.i, 0.2f) { isRepeat = true; putFrames(intArrayOf(7,8,9)) }

            currentSequence(DirectionSee.DOWN.i)
        }
        create<CName> {
            name = "pig"
        }

        create<CVelocity> {
            vector.set(0f, -19f)
        }
        create<CDefaultPhysics2d> {
            val t = CTransforms[this@Pig]
            val bodyW = t.size.width / 3
            createBody(t, t.size.width / 2 - bodyW / 2, 4f, bodyW, t.size.height / 4, Types.TYPE.DYNAMIC, name)
        }
       /* create<CBump> {
            normalMap = CAtlasRegion[this@Pig].atlas?.findRegion(CAtlasRegion[this@Pig].nameRegion)!!
        }*/
        create<CUpdate> {
            func = {
                CTransforms[this@Pig].updateZIndex()
               // movePlayer()

                /*CBump[this@Pig].apply {
                    val a = CAtlasRegion[this@Pig]
                    val index = CAtlasRegionAnimation[this@Pig].frameSequenceArray.getCurrentFrame()
                    normalMap = a.atlas?.findRegion(a.nameRegion,index) ?: throw GdxRuntimeException("frame $index in $this not found")
                }*/
            }
        }

        /*create<CStateMachine> { createState(this) }*/

    }
    val speed = 50f
    private fun movePlayer() {
        var speedDelta = speed
        if(direction.x== 1f && direction.y==1f || direction.x== 1f && direction.y==-1f ||
                direction.x== -1f && direction.y==1f || direction.x== -1f && direction.y==-1f) {
            speedDelta-=speedDelta*0.2f
        }
        CVelocity[this@Pig].vector.set(direction.cpy().scl(speedDelta))
        if(!CVelocity[this@Pig].vector.isZero) {
            val a = 270 - direction.angle()
            if (a == 180f) {
                CStateMachine[this@Pig].setTrigger(hashUp)
            } else if (a == 90f) {
                CStateMachine[this@Pig].setTrigger(hashLeft)
            } else if (a == 270f) {
                CStateMachine[this@Pig].setTrigger(hashRight)
            } else if (a == 0f) {
                CStateMachine[this@Pig].setTrigger(hashDown)
            } else if (a > 0 && a < 90) {
                CStateMachine[this@Pig].setTrigger(hashDown)
            } else if( a > 90 && a < 180) {
                CStateMachine[this@Pig].setTrigger(hashUp)
            } else if( a > 180 && a < 270) {
                CStateMachine[this@Pig].setTrigger(hashUp)
            }else if( a == -45f) {
                CStateMachine[this@Pig].setTrigger(hashDown)
            }
        } else {
            CStateMachine[this@Pig].setTrigger(Player.T.W_NON.hashCode())
        }
    }

    private val hashUp = DirectionSee.UP.i
    private val hashDown =  DirectionSee.DOWN.i
    private val hashLeft =  DirectionSee.LEFT.i
    private val hashRight =  DirectionSee.RIGHT.i

    private fun createState(component: CStateMachine) {
        component.apply {
            val sWalkRight = createState(DirectionSee.RIGHT.i.toString()) {
                CAtlasRegionAnimation[this@Pig].currentSequence(DirectionSee.RIGHT.i)
                directionSee = Player.DirectionSee.RIGHT
            }
            val sWalkLeft = createState(DirectionSee.LEFT.i.toString()) {
                CAtlasRegionAnimation[this@Pig].currentSequence(DirectionSee.LEFT.i)
                directionSee = Player.DirectionSee.LEFT
            }
            val sWalkUp = createState(DirectionSee.UP.i.toString()) {
                directionSee = Player.DirectionSee.UP
                CAtlasRegionAnimation[this@Pig].currentSequence(DirectionSee.UP.i)
            }
            val sWalkDown = createState(DirectionSee.DOWN.i.toString()) {
                CAtlasRegionAnimation[this@Pig].currentSequence(DirectionSee.DOWN.i)
                directionSee = Player.DirectionSee.DOWN
            }
            val sWalkNon = createState("non") {
                CAtlasRegionAnimation[this@Pig].currentSequence(directionSee.i)
            }
            initMachine(sWalkNon)
            /*addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_RIGHT.hashCode())) { setState(DirectionSee.RIGHT.i.toString()) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_LEFT.hashCode())) { setState(DirectionSee.LEFT.i.toString()) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_UP.hashCode())) { setState(DirectionSee.UP.i.toString()) })
            addTriggerHandler(TriggerHandler(sWalkNon, addTrigger(T.W_DOWN.hashCode())) { setState(DirectionSee.DOWN.i.toString()) })

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
            addTriggerHandler(TriggerHandler(sWalkUp, getTrigger(T.W_DOWN.hashCode())) { setState(T.W_DOWN.name) })*/
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
    enum class DirectionSee(val i: Int) {
        UP(5), DOWN(0), LEFT(6), RIGHT(7)
    }
}