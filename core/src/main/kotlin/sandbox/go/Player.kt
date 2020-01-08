package sandbox.sandbox.go

import com.anupcowkur.statelin.TriggerHandler
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.components
import dogengine.ecs.components.create
import dogengine.ecs.components.createEntity
import dogengine.ecs.components.draw.*
import dogengine.ecs.components.utility.logic.CVelocity
import dogengine.ecs.def.GameEntity
import dogengine.ecs.systems.controllers.CPlayerController
import dogengine.ecs.systems.controllers.ControllerListener
import dogengine.es.redkin.physicsengine2d.bodies.RectangleBody
import dogengine.es.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.R
import sandbox.dogengine.ashley.components.utility.CCameraLook
import sandbox.dogengine.ashley.components.utility.CName
import sandbox.dogengine.ashley.components.utility.CStateMachine
import sandbox.dogengine.ashley.components.utility.CUpdate
import sandbox.dogengine.ecs.components.utility.logic.CTransforms
import sandbox.dogengine.ecs.components.utility.logic.updateZIndex
import sandbox.sandbox.def.jbump.CJBumpAABB
import sandbox.sandbox.def.redkin.physicsengine2d.CDefaultPhysics2d

class Player(val am: AssetManager, pos: Vector2) : GameEntity(), ControllerListener {

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
            createSequence(T.W_NON.ordinal, 0.4f) { isRepeat = true; putFrames(intArrayOf(1, 5)) }
            createSequence(T.W_DOWN.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(1, 2, 3, 4, 5)) }
            createSequence(T.W_UP.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(6, 7, 8, 9, 10)) }
            createSequence(T.W_LEFT.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(11, 12, 13, 14)) }
            createSequence(T.W_RIGHT.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(16, 17, 18, 19, 20)) }
            currentSequence(T.W_NON.ordinal)
        }
        create<CName> {
            name = "player"
        }

        create<CCameraLook> {}
        create<CPlayerController> { controllerListener = this@Player }
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
            rectangleBody = RectangleBody(t.position.x, t.position.y, t.size.width, t.size.height, Types.TYPE.DYNAMIC, name)
        }
        create<CUpdate> {
            CTransforms[this@Player].updateZIndex()

        }

        create<CStateMachine> { createState(this) }

        //randomGenerate(128)

    }

    private fun randomGenerate(num: Int) {
        for (i in 0..num) {
            val pos = Vector2(MathUtils.random(0, 300) * 1f, MathUtils.random(0, 400) * 1f)
            engine.addEntity(engine.createEntity {
                components {
                    create<CTransforms> {
                        position.set(pos)
                        size = Size(64f, 48f)
                    }
                    create<CAtlasRegion> {
                        atlas = am.get("assets/atlas/matlas.atlas", TextureAtlas::class.java)
                        nameRegion = "knight"
                        drawLayer = CDrawable.DrawLayer.YES_EFFECT
                    }
                    create<CAtlasRegionAnimation> {
                        createSequence(T.W_NON.ordinal, 0.4f) { isRepeat = true; putFrames(intArrayOf(1, 5)) }
                        createSequence(T.W_DOWN.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(1, 2, 3, 4, 5)) }
                        createSequence(T.W_UP.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(6, 7, 8, 9, 10)) }
                        createSequence(T.W_LEFT.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(11, 12, 13, 14)) }
                        createSequence(T.W_RIGHT.ordinal, 0.15f) { isRepeat = true; putFrames(intArrayOf(16, 17, 18, 19, 20)) }
                        currentSequence(T.W_NON.ordinal)
                    }
                    create<CName> {
                        name = "player"
                    }
                    create<CJBumpAABB> {
                        scaleSize.x = 0.2f
                        scaleSize.y = 0.2f
                        dynamic = true
                        positionOffset.x = 32 - 5f
                        positionOffset.y = 10f
                    }
                }
            })
        }

    }

    private fun createState(component: CStateMachine) {
        component.apply {
            val sWalkRight = createState(T.W_RIGHT.name) {
                CAtlasRegionAnimation[this@Player].currentSequence(T.W_RIGHT.ordinal)
            }
            val sWalkLeft = createState(T.W_LEFT.name) {
                CAtlasRegionAnimation[this@Player].currentSequence(T.W_LEFT.ordinal)
            }
            val sWalkUp = createState(T.W_UP.name) {
                CAtlasRegionAnimation[this@Player].currentSequence(T.W_UP.ordinal)
            }
            val sWalkDown = createState(T.W_DOWN.name) {
                CAtlasRegionAnimation[this@Player].currentSequence(T.W_DOWN.ordinal)
            }
            val sWalkNon = createState(T.W_NON.name) {
                CAtlasRegionAnimation[this@Player].currentSequence(T.W_NON.ordinal)
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
    enum class T(i: Int) {
        W_RIGHT(2),
        W_LEFT(1),
        W_UP(3),
        W_DOWN(4),
        W_NON(0)
    }

    private val speed = 200f
    override fun up() {
        CStateMachine[this@Player].setTrigger(T.W_UP.name.hashCode())
        CVelocity[this@Player].vector.y = speed
    }

    override fun down() {
        CStateMachine[this@Player].setTrigger(T.W_DOWN.name.hashCode())
        CVelocity[this@Player].vector.y = -speed
    }

    override fun left() {
        CStateMachine[this@Player].setTrigger(T.W_LEFT.name.hashCode())
        CVelocity[this@Player].vector.x = -speed
    }

    override fun right() {
        CStateMachine[this@Player].setTrigger(T.W_RIGHT.name.hashCode())
        CVelocity[this@Player].vector.x = speed
    }

    override fun a() {
    }

    override fun b() {
    }

    override fun c() {
    }

    override fun none(keyCode: Int) {
        CStateMachine[this@Player].setTrigger(T.W_NON.name.hashCode())
        CVelocity[this@Player].vector.set(0f,0f)
    }
}