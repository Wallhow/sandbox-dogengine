package dogengine.ecs.systems.utility

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.steer.Steerable
import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.ai.steer.behaviors.Arrive
import com.badlogic.gdx.ai.utils.Location
import com.badlogic.gdx.math.Vector2
import com.google.inject.Inject
import com.strongjoshua.console.Console
import com.strongjoshua.console.LogLevel
import dogengine.MessagesType
import dogengine.ecs.components.addEntityAddedListener
import dogengine.ecs.components.utility.CName
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CCameraLook
import dogengine.ecs.systems.SystemPriority
import dogengine.utils.GameCamera
import dogengine.utils.extension.clamp
import ecs.systems.utility.LocationAdapter
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


/**
 * Created by wallhow on 20.12.16.
 */
class SCameraLook @Inject constructor(private val gameCamera: GameCamera) :
        EntitySystem() {
    init {
        priority = SystemPriority.UPDATE + 150
    }

    @Inject
    private lateinit var messageManager: MessageManager
    private var fixedCamera: Boolean = false
    private var smoothMove = true
    private val camera = gameCamera.getCamera()
    private val worldSize = gameCamera.getWorldSize()
    private val entityLookStack: Stack<Entity> = Stack()
    private val entityWithLookComponent: com.badlogic.gdx.utils.Array<Entity> = com.badlogic.gdx.utils.Array()
    private val cameraPosition = SteeringBehaviorCamera(Vector2())

    override fun addedToEngine(engine: Engine) {
        messageManager.apply {
            //Добавляем все необходимые слушатели сообщений для камеры
            addCameraFixedListener(this)
            addCameraSmoothListener(this)
            addCameraLookBeforeLastListener(this)
            addCameraLookToListener(this)
        }

        engine.addEntityAddedListener(Family.all(CCameraLook::class.java).get()) {
            entityLookStack.push(it)
            entityWithLookComponent.add(it)
            CTransforms[it].run {
                cameraPosition.setTargetLocation(getCenterVector())
                //gameCamera.setPosition(getCenterX(), getCenterY())
            }
        }

        super.addedToEngine(engine)
    }

    override fun update(deltaTime: Float) {
        entityLookStack.peek().apply {
            val pos = Vector2()

            if (!smoothMove) {
                pos.set(CTransforms[this].getCenterVector())
                cameraPosition.position.set(pos)
            } else {
                pos.set(cameraPosition.position)
                cameraPosition.setTargetLocation(CTransforms[this].getCenterVector())
                cameraPosition.update(deltaTime)
            }

            if (fixedCamera) {
                val halfViewportZoomedWidth = camera.viewportWidth * camera.zoom * 0.5f
                val halfViewportZoomedHeight = camera.viewportHeight * camera.zoom * 0.5f
                pos.clamp(halfViewportZoomedWidth,
                        worldSize.width - halfViewportZoomedWidth,
                        halfViewportZoomedHeight,
                        worldSize.height - halfViewportZoomedHeight)
            }
            gameCamera.setPosition(pos.x, pos.y)
            gameCamera.update()
        }
    }

    private fun addCameraLookToListener(messageManager: MessageManager) {
        messageManager.addListener({
            val name = (it.extraInfo as String)
            val eInStack = entityLookStack.firstOrNull() { e -> CName[e].name == name }
            val eInArray = entityWithLookComponent.firstOrNull { entity -> CName[entity].name == name }
            when {
                eInStack != null -> eInStack.apply {
                    add(engine.createComponent(CCameraLook::class.java))
                    entityLookStack.push(this)
                    cameraPosition.setTargetLocation(CTransforms[this].getCenterVector())
                    it.sender.handleMessage(Telegram().apply { extraInfo = 1 })
                }
                eInArray != null -> {
                    entityLookStack.push(eInArray)
                    cameraPosition.setTargetLocation(CTransforms[eInArray].getCenterVector())
                    it.sender.handleMessage(Telegram().apply { extraInfo = 1 })
                }
                else -> {
                    it.sender.handleMessage(Telegram().apply { extraInfo = -1 })
                }
            }
            true
        }, MessagesType.CAMERA_LOOK_TO)
    }
    private fun addCameraLookBeforeLastListener(mm: MessageManager) {
        mm.addListener({
            if (entityLookStack.size != 1) {
                entityLookStack.pop()
                val e = entityLookStack.peek()
                cameraPosition.setTargetLocation(CTransforms[e].getCenterVector())
            }
            true
        }, MessagesType.CAMERA_LOOK_BEFORE)
    }
    private fun addCameraSmoothListener(messageManager: MessageManager) {
        messageManager.addListener({
            val arr = it.extraInfo as Array<*>
            val console = arr[1] as Console
            smoothMove = arr[0] as Boolean
            console.log("smooth move $smoothMove", LogLevel.SUCCESS)
            true
        }, MessagesType.CAMERA_SMOOTH_MOVE)
    }
    private fun addCameraFixedListener(messageManager: MessageManager) {
        messageManager.addListener({
            fixedCamera = (it.extraInfo as Boolean)
            true
        }, MessagesType.CAMERA_FIXED_BOUNDS)
    }


    class SteeringBehaviorCamera(@JvmField val position: Vector2, targetPosition: Vector2 = Vector2()) : Steerable<Vector2> {
        private var linearVelocity: Vector2 = Vector2(400f, 400f)
        private var independentFacing = true
        private val target = LocationAdapter(targetPosition)
        private var maxLinearSpeed: Float = 600f
        private var maxLinearAcceleration: Float = 1000f
        private var angularVelocity: Float = 0f
        private val steeringAcceleration = SteeringAcceleration<Vector2>(Vector2())
        private val steeringBehavior: Arrive<Vector2>

        init {
            steeringBehavior = Arrive<Vector2>(this).apply {
                this.arrivalTolerance = 0.01f
                this.timeToTarget = 0.05f //
                this.decelerationRadius = 160f
                this.isEnabled = true
            }
        }

        override fun getPosition(): Vector2 {
            return position
        }
        fun setTargetLocation(targetPosition: Vector2) {
            steeringBehavior.apply {
                this@SteeringBehaviorCamera.target.position.set(targetPosition)
                this.target = this@SteeringBehaviorCamera.target
            }
        }

        private fun calculateOrientationFromLinearVelocity(character: Steerable<Vector2>): Float {
            // If we haven't got any velocity, then we can do nothing.
            return if (character.linearVelocity.isZero(character.zeroLinearSpeedThreshold)) character.orientation else character.vectorToAngle(character.linearVelocity)
        }

        override fun getAngularVelocity(): Float {
            return angularVelocity
        }

        override fun vectorToAngle(vector: Vector2): Float {
            return atan2(-vector.x.toDouble(), vector.y.toDouble()).toFloat()
        }

        override fun angleToVector(outVector: Vector2, angle: Float): Vector2 {
            outVector.x = (-sin(angle.toDouble())).toFloat()
            outVector.y = cos(angle.toDouble()).toFloat()
            return outVector
        }

        fun update(delta: Float) {
            // Calculate steering acceleration
            steeringBehavior.calculateSteering(steeringAcceleration)

            if(steeringAcceleration.linear.len2()<=2.5f) {
                steeringAcceleration.linear.set(0f,0f)
            }
            // Apply steering acceleration to move this agent
            applySteering(steeringAcceleration, delta)

        }

        private fun applySteering(steering: SteeringAcceleration<Vector2>, time: Float) {
            // Update position and linear velocity. Velocity is trimmed to maximum speed
            position.mulAdd(linearVelocity, time)
            linearVelocity.mulAdd(steering.linear, time).limit(this.maxLinearSpeed)

            if (independentFacing) {
                this.orientation += angularVelocity * time
                this.angularVelocity += steering.angular * time
            }
        }

        override fun getOrientation(): Float {
            return 0f
        }

        override fun setOrientation(orientation: Float) {

        }

        override fun newLocation(): Location<Vector2> {
            TODO("Not yet implemented")
        }

        override fun getZeroLinearSpeedThreshold(): Float {
            return 0f
        }

        override fun setZeroLinearSpeedThreshold(value: Float) {

        }

        override fun getMaxLinearSpeed(): Float {
            return maxLinearSpeed
        }

        override fun setMaxLinearSpeed(maxLinearSpeed: Float) {
            this.maxLinearSpeed = maxLinearSpeed
        }

        override fun getMaxLinearAcceleration(): Float {
            return maxLinearAcceleration
        }

        override fun setMaxLinearAcceleration(maxLinearAcceleration: Float) {
            this.maxLinearAcceleration = maxLinearAcceleration
        }

        override fun getMaxAngularSpeed(): Float {
            return 0f
        }

        override fun setMaxAngularSpeed(maxAngularSpeed: Float) {
            TODO("Not yet implemented")
        }

        override fun getMaxAngularAcceleration(): Float {
            TODO("Not yet implemented")
        }

        override fun setMaxAngularAcceleration(maxAngularAcceleration: Float) {
            TODO("Not yet implemented")
        }

        override fun getBoundingRadius(): Float {
            TODO("Not yet implemented")
        }

        override fun isTagged(): Boolean {
            TODO("Not yet implemented")
        }

        override fun setTagged(tagged: Boolean) {
            TODO("Not yet implemented")
        }

        override fun getLinearVelocity(): Vector2 {
            return linearVelocity
        }
    }

}