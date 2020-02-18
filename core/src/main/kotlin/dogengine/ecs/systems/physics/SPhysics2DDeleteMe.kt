package dogengine.ecs.systems.physics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Inject
import dogengine.ecs.components.utility.logic.CPhysics2D
import dogengine.ecs.systems.SystemPriority

class SPhysics2DDeleteMe @Inject constructor(val world: World) : IteratingSystem(Family.all(CPhysics2D::class.java).get()) {
    init {
        priority = SystemPriority.AFTER_UPDATE
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        CPhysics2D[entity].apply {
            if (delete) {
                world.destroyBody(body)
                entity.remove(CPhysics2D::class.java)
            }
        }
    }
}