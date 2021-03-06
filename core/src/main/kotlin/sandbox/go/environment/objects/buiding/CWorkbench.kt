package sandbox.sandbox.go.environment.objects.buiding

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent
import sandbox.sandbox.go.objects.ObjectList

class CWorkbench : PoolableComponent {
    companion object : ComponentResolver<CWorkbench>(CWorkbench::class.java)
    var type: ObjectList = ObjectList.ZERO
    var isNear = false

    override fun reset() {
        type = ObjectList.ZERO
    }
}