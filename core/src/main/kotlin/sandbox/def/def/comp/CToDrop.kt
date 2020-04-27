package sandbox.sandbox.def.def.comp

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent
import sandbox.go.environment.ItemList

class CToDrop : PoolableComponent {
    companion object : ComponentResolver<CToDrop>(CToDrop::class.java)
    var type : ItemList = ItemList.ZERO

    override fun reset() {
        type = ItemList.ZERO
    }
}