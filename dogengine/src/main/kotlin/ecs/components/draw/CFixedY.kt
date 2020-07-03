package dogengine.ecs.components.draw

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent

class CFixedY: PoolableComponent {
    var y: Float = 0f
    override fun reset() {
        y = 0f
    }
    companion object : ComponentResolver<CFixedY>(CFixedY::class.java)
}