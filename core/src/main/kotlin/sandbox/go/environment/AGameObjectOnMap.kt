package sandbox.sandbox.go.environment

import dogengine.ecs.components.create
import sandbox.go.environment.ObjectList
import sandbox.sandbox.def.def.comp.CHealth
import sandbox.sandbox.go.AGameObject

abstract class AGameObjectOnMap(name: String) : AGameObject(name) {
    abstract override var itemType: ObjectList
    protected fun createCHealth(maxHealth: Float, count: Int = 1, itemType: ObjectList? = this.itemType, beforeDeadFunc: (() -> Unit)? = null) {
        create<CHealth> {
            health = maxHealth
            beforeDead = beforeDeadFunc
            if(itemType!=null)
                this.itemTypeDrop = CHealth.DropAndCount(count,dropType = itemType )
        }
    }
}