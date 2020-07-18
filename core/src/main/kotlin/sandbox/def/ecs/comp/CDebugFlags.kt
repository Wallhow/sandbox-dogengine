package sandbox.sandbox.def.def.comp

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent

class CDebugFlags : PoolableComponent {
    companion object : ComponentResolver<CDebugFlags>(CDebugFlags::class.java)

    override fun reset() {

    }
}