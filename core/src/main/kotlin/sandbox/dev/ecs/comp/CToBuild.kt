package sandbox.dev.ecs.comp

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent
import sandbox.sandbox.go.objects.ObjectList

class CToBuild : PoolableComponent {
    companion object : ComponentResolver<CToBuild>(CToBuild::class.java)
    var type : ObjectList = ObjectList.ZERO

    override fun reset() {
        type = ObjectList.ZERO
    }
}