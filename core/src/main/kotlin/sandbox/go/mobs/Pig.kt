package sandbox.go.mobs

import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibrary
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.Components
import dogengine.ecs.components.create
import dogengine.ecs.components.createBody
import dogengine.ecs.components.draw.*
import dogengine.ecs.components.utility.logic.CDefaultPhysics2d
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.logic.CVelocity
import dogengine.ecs.components.utility.logic.updateZIndex
import dogengine.ecs.components.utility.visible.CCameraLook
import dogengine.ecs.def.GameEntity
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import dogengine.utils.extension.get
import dogengine.utils.extension.injector
import dogengine.utils.log
import dogengine.utils.vec2
import map2D.Vector2Int
import sandbox.R
import sandbox.go.mobs.Pig.DirectionSee.*
import kotlin.math.atan2


class Pig(private val am: AssetManager, pos: Vector2) : GameEntity() {
    private val direction = vec2(0f, 0f)
    private var tree = BehaviorTree<Pig>()
    val speed = 30f
    var accum = 0f

    private val messenger: MessageManager = injector[MessageManager::class.java]
    private val stateMachine = DefaultStateMachine<Pig, PigState>(this, PigState.SEEK)

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
            createSequence(DOWN.ordinal, 0.20f) { isRepeat = true; putFrames(intArrayOf(1, 2, 3)) }
            createSequence(UP.ordinal, 0.20f) { isRepeat = true; putFrames(intArrayOf(10, 11, 12)) }
            createSequence(LEFT.ordinal, 0.2f) { isRepeat = true; putFrames(intArrayOf(4, 5, 6)) }
            createSequence(RIGHT.ordinal, 0.2f) { isRepeat = true; putFrames(intArrayOf(7, 8, 9)) }

            currentSequence(DOWN.ordinal)
        }

        create<CVelocity> {
            vector.set(0f, 0f)
        }
        create<CDefaultPhysics2d> {
            val t = CTransforms[this@Pig]
            val bodyW = t.size.width / 3
            createBody(t, t.size.width / 2 - bodyW / 2, 4f, bodyW, t.size.height / 4, Types.TYPE.DYNAMIC, name)
        }

        Components.action(this) {
            stateMachine.update()
            CTransforms[this@Pig].updateZIndex()
            tree.step()
            accum += it
            if (accum >= 3f) {
                accum = 0f
                messenger.dispatchMessage(777,
                        arrayOf(CTransforms[this].run { Vector2Int(position.x.toInt(), position.y.toInt()) }, 3))
            }
        }
        /*create<CStateMachine> { createState(this) }*/

        BehaviorTreeLibraryManager.getInstance().library = BehaviorTreeLibrary(BehaviorTreeParser.DEBUG_HIGH);
        val btlm = BehaviorTreeLibraryManager.getInstance()
        tree = btlm.createBehaviorTree("assets/tree/pig.tree", this)

    }

    fun leftWalk() {
        direction.set(LEFT.vec)
        move(direction)
        setTextureSequence(direction)
    }

    fun rightWalk() {
        direction.set(RIGHT.vec)
        move(direction)
        setTextureSequence(direction)
    }

    fun upWalk() {
        direction.y = UP.vec.y
        move(direction)
        setTextureSequence(direction)

    }

    fun downWalk() {
        direction.set(DOWN.vec)
        move(direction)
        setTextureSequence(direction)
    }

    private fun move(direction: Vector2) {
        var speedDelta = speed
        //Проверяем длинну вектора направления, если она равна двум, значи движемся по диагонали
        if (direction.len2() >= 2f) {
            speedDelta -= speedDelta * 0.2f
        }
        CVelocity[this].vector.set(direction.cpy().scl(speedDelta))
    }

    private fun setTextureSequence(direction: Vector2) {

        if (!CVelocity[this].vector.isZero) {
            when (DirectionSee.direction(direction)) {
                UP -> {
                    CAtlasRegionAnimation[this].currentSequence(UP.ordinal)
                }
                DOWN -> {
                    CAtlasRegionAnimation[this].currentSequence(DOWN.ordinal)
                }
                LEFT, UP_LEFT, DOWN_LEFT -> {
                    CAtlasRegionAnimation[this].currentSequence(LEFT.ordinal)
                }
                RIGHT, UP_RIGHT, DOWN_RIGHT -> {
                    CAtlasRegionAnimation[this].currentSequence(RIGHT.ordinal)
                }
            }
        }
    }

    enum class PigState : State<Pig> {
        SEEK {
            override fun update(entity: Pig) {

            }

            override fun enter(entity: Pig) {

            }

            override fun exit(entity: Pig) {

            }

            override fun onMessage(entity: Pig, telegram: Telegram?): Boolean {
                return true
            }
        },

    }


    enum class DirectionSee(val vec: Vector2) {
        UP(vec2(0f, 1f)),
        DOWN(vec2(0f, -1f)),
        LEFT(vec2(-1f, 0f)),
        RIGHT(vec2(1f, 0f)),
        UP_LEFT(vec2(-1f, 1f)),
        UP_RIGHT(vec2(1f, 1f)),
        DOWN_LEFT(vec2(-1f, -1f)),
        DOWN_RIGHT(vec2(1f, -1f));

        companion object {
            fun direction(vec: Vector2): DirectionSee {
                val angle = atan2(-vec.x.toDouble(), vec.y.toDouble()).toFloat() * MathUtils.radiansToDegrees
                return when {
                    angle == 0f -> {
                        UP
                    }
                    angle == 180f || angle == -180f -> {
                        DOWN
                    }
                    angle == 90f -> {
                        LEFT
                    }
                    angle == -90f -> {
                        RIGHT
                    }
                    angle > 0f && angle < 90f -> {
                        UP_LEFT
                    }
                    angle < 0f && angle > -90f -> {
                        UP_RIGHT
                    }

                    angle < -90f && angle > -180f -> {
                        DOWN_RIGHT
                    }
                    angle > 90f && angle < 180f -> {
                        DOWN_LEFT
                    }
                    else -> DOWN
                }
            }
        }
    }
}

