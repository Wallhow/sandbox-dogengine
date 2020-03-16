package dogengine.ecs.systems.update

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import dogengine.ecs.components.utility.logic.CUpdate
import dogengine.ecs.systems.SystemPriority

class SUpdate : IteratingSystem(Family.all(CUpdate::class.java).get()) {
    init {
        priority = SystemPriority.UPDATE
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val u = CUpdate[entity]
        u.currentTime+=deltaTime
        if(u.currentTime>=u.updateBetweenTime) {
            u.func?.invoke(deltaTime)
            u.currentTime=0f
        }
    }
}