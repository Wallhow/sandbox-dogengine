package sandbox.sandbox.def.def.comp

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent

class CNearbyObject : PoolableComponent {
    companion object : ComponentResolver<CNearbyObject>(CNearbyObject::class.java)
    override fun reset() {

    }
}
