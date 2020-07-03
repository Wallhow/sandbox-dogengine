package dogengine.spine.components

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent
import dogengine.spine.SpineSkeleton

class CSpineSkeleton : PoolableComponent {
    companion object : ComponentResolver<CSpineSkeleton>(CSpineSkeleton::class.java)
    var skeleton : SpineSkeleton? = null
    override fun reset() {
        skeleton = null
    }
}