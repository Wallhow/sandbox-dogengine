package dogengine.ecs.components.utility

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent

class CName : PoolableComponent {
    companion object : ComponentResolver<CName>(CName::class.java)
    var name : String = ""
    override fun reset() {
        name = ""
    }
}