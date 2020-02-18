package dogengine.ecs.systems.physics

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject
import dogengine.ecs.systems.SystemPriority

class SPhysicsBox2DUpdate @Inject constructor(private val world: World) : EntitySystem() {
    init {
        priority = SystemPriority.PHYSICS-1
    }

    private var accumulator = 0f
    override fun update(deltaTime: Float) {
        val frameTime = Math.min(deltaTime, 0.25f)
        accumulator += frameTime
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS)
            accumulator -= TIME_STEP
        }
        super.update(deltaTime)

    }

    companion object {
        var TIME_STEP = 1f / 300f //1f/300f
        var VELOCITY_ITERATIONS = 6
        var POSITION_ITERATIONS = 2 //2

    }
}