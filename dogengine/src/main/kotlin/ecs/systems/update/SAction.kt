package dogengine.ecs.systems.update

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import dogengine.ecs.components.utility.logic.CAction
import dogengine.ecs.systems.SystemPriority

class SAction : IteratingSystem(Family.all(CAction::class.java).get()) {
    init {
        priority = SystemPriority.UPDATE
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val act = CAction[entity]
        act.actionFunction?.invoke(deltaTime)
    }
}