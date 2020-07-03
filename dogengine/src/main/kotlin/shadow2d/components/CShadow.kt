package dogengine.shadow2d.components

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent

class CShadow : PoolableComponent
{
    override fun reset() {
    }
    companion object : ComponentResolver<CShadow>(CShadow::class.java)
}