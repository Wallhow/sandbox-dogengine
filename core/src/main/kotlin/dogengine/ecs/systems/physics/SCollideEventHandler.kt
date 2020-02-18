package dogengine.ecs.systems.physics

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import dogengine.ecs.components.events.CCollideEvent
import dogengine.ecs.components.events.CCollideEventListener
import dogengine.ecs.systems.SystemPriority

class SCollideEventHandler : IteratingSystem (Family.all(CCollideEvent::class.java,CCollideEventListener::class.java).get()){
    init {
        priority = SystemPriority.PHYSICS
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        CCollideEventListener[entity].func(CCollideEvent[entity].collide)
        entity.remove(CCollideEvent::class.java)
    }
}