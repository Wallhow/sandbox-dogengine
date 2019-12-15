package sandbox.dogengine.ashley.systems.utility

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import sandbox.dogengine.ashley.components.utility.CDeleteComponent

class SDeleteComponent : IteratingSystem(Family.all(CDeleteComponent::class.java).get()) {
    init {
        priority = Int.MAX_VALUE-2
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val delete = CDeleteComponent[entity]
        if (delete.componentRemove!=null) {
            entity.remove(delete.componentRemove?.javaClass)
            entity.remove(delete.javaClass)
        }
    }
}