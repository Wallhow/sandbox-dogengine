package dogengine.ecs.components.utility.logic

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent

class CAction : PoolableComponent {
    companion object : ComponentResolver<CAction>(CAction::class.java)
    var actionFunction: ((delta: Float) -> Unit)? = null
    override fun reset() {
        actionFunction = null
    }
}