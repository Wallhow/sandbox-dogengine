package dogengine.ecs.systems.utility

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.google.inject.Inject
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.systems.SystemPriority

/**
 * Created by wallhow on 22.01.17.
 */
class SDeleteMe @Inject constructor() : IteratingSystem(Family.all(CDeleteMe::class.java).get()){
    init {
        priority = SystemPriority.BEFORE_UPDATE
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        CDeleteMe[entity].task?.invoke()
        engine.removeEntity(entity)
    }
}