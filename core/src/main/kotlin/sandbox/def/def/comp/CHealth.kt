package sandbox.sandbox.def.def.comp

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent
import sandbox.sandbox.go.items.ObjectList

class CHealth : PoolableComponent {
    companion object : ComponentResolver<CHealth>(CHealth::class.java)
    var health = 0f
    var beforeDead : (() -> Unit)? = null
    var shot : (() -> Unit)? = null
    var dropType: DropAndCount? = null
    override fun reset() {
        health = 0f
        beforeDead = null
        shot = null
        dropType = null
    }

    data class DropAndCount(val minCount: Int = 1, val maxCount: Int = minCount + 1,val dropType: ObjectList?)
}