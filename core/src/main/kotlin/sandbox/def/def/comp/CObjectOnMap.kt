package sandbox.sandbox.def.def.comp

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent
import sandbox.sandbox.go.objects.ObjectList

class CObjectOnMap : PoolableComponent {
    companion object : ComponentResolver<CObjectOnMap>(CObjectOnMap::class.java)
    var typeObject: ObjectList = ObjectList.ZERO
    override fun reset() {
        typeObject = ObjectList.ZERO
    }
}