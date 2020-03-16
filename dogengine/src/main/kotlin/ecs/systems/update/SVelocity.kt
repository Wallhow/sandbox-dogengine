package dogengine.ecs.systems.update

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.BodyDef
import dogengine.Kernel
import dogengine.ecs.components.utility.logic.CDefaultPhysics2d
import dogengine.ecs.components.utility.logic.CPhysics2D
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.logic.CVelocity
import dogengine.ecs.systems.SystemPriority
import dogengine.redkin.physicsengine2d.world.World

class SVelocity : IteratingSystem(Family.all(CTransforms::class.java, CVelocity::class.java).get()) {
    private var ppu: Float = 1f

    init {
        priority = SystemPriority.UPDATE+50
        if (Kernel.getInjector().getInstance(World::class.java) != null)
            ppu = Kernel.getInjector().getInstance(World::class.java).PPU
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (CPhysics2D[entity] != null) {
            processCPhysics2D(entity, deltaTime)
        } else if (CDefaultPhysics2d[entity] != null) {
            processCDefaultPhysics2d(entity, deltaTime)
        } else {
            //processDefault(entity,deltaTime)
        }
    }

    private fun processCPhysics2D(entity: Entity, deltaTime: Float) {
        val physics = CPhysics2D[entity]
        val velocity = CVelocity[entity]

        if (physics.bodyType == BodyDef.BodyType.DynamicBody) {
            if (Kernel.getInjector().getInstance(com.badlogic.gdx.physics.box2d.World::class.java).gravity.isZero) {
                physics.body?.setLinearVelocity(velocity.vector.x * deltaTime, velocity.vector.y * deltaTime)
            } else
                physics.body?.applyForceToCenter(velocity.vector.x * deltaTime, velocity.vector.y * deltaTime, true)
        } else {
            physics.body?.setLinearVelocity(velocity.vector.x * deltaTime, velocity.vector.y * deltaTime)
        }
    }

    private fun processCDefaultPhysics2d(entity: Entity, deltaTime: Float) {
        val physics = CDefaultPhysics2d[entity]
        val vel = CVelocity[entity]
        physics?.rectangleBody?.apply {
            val velX = (vel.vector.x ) / ppu
            val velY = (vel.vector.y ) / ppu
            velocity.set(velX, velY)
        }
    }

    private fun processDefault(entity: Entity, deltaTime: Float) {
        val transforms = CTransforms[entity]
        val velocity = CVelocity[entity]
        transforms.position.add(velocity.vector.x * deltaTime, velocity.vector.y * deltaTime)
    }
}