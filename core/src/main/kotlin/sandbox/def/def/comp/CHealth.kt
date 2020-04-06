package sandbox.sandbox.def.def.comp

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent
import sandbox.go.environment.ObjectList

class CHealth : PoolableComponent {
    companion object : ComponentResolver<CHealth>(CHealth::class.java)
    var health = 0f
    var beforeDead : (() -> Unit)? = null
    var shot : (() -> Unit)? = null
    var itemTypeDrop: DropAndCount? = null
    override fun reset() {
        health = 0f
        beforeDead = null
        shot = null
        itemTypeDrop = null
    }

    data class DropAndCount(val minCount: Int = 1, val maxCount: Int = minCount,val dropType: ObjectList?)
}