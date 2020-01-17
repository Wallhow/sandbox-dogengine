package sandbox.sandbox.def.def.comp

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent

class CExtraction: PoolableComponent {
companion object : ComponentResolver<CExtraction>(CExtraction::class.java)
    var force = 0f
    override fun reset() {
        force = 0f
    }
}