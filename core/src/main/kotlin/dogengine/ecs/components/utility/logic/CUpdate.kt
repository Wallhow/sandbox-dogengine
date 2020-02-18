package dogengine.ecs.components.utility.logic

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent

class CUpdate : PoolableComponent {
    var updateBetweenTime: Float = 0f
    var func : ((delta: Float) -> Unit)? = null
    var currentTime = 0f
    override fun reset() {
        updateBetweenTime = 0f
        func = null
        currentTime = 0f
    }

    companion object : ComponentResolver<CUpdate>(CUpdate::class.java)
}