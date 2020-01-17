package sandbox.sandbox.def.def.comp

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent

class CHealth : PoolableComponent {
    companion object : ComponentResolver<CHealth>(CHealth::class.java)
    var health = 0f
    override fun reset() {
        health = 0f
    }
}