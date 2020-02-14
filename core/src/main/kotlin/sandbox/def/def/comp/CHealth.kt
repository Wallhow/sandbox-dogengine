package sandbox.sandbox.def.def.comp

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent

class CHealth : PoolableComponent {
    companion object : ComponentResolver<CHealth>(CHealth::class.java)
    var health = 0f
    var beforeDead : (() -> Unit)? = null
    var shot : (() -> Unit)? = null
    override fun reset() {
        health = 0f
    }
}