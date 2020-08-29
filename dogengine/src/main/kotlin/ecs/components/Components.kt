package dogengine.ecs.components

import com.badlogic.ashley.core.Entity
import dogengine.PooledEntityCreate
import dogengine.ecs.components.utility.logic.CAction
import dogengine.ecs.def.PoolableComponent

object Components {
    fun action(entity: Entity, func: (delta: Float) -> Unit): PoolableComponent {
        val comp = PooledEntityCreate.engine!!.createComponent(CAction::class.java)
        comp.actionFunction = func
        entity.add(comp)
        return comp
    }
}