package dogengine.ecs.systems.utility

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import dogengine.ecs.components.utility.CDeleteComponent
import dogengine.ecs.systems.SystemPriority

class SDeleteComponent : IteratingSystem(Family.all(CDeleteComponent::class.java).get()) {
    init {
        priority = SystemPriority.BEFORE_UPDATE-1
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val delete = CDeleteComponent[entity]
        if (delete.componentRemove!=null) {
            entity.remove(delete.componentRemove?.javaClass)
            entity.remove(delete.javaClass)
        }
    }
}