package sandbox.sandbox.go.environment

import dogengine.ecs.components.create
import sandbox.go.environment.ItemList
import sandbox.sandbox.def.def.comp.CHealth
import sandbox.sandbox.go.AGameObject

abstract class AGameObjectOnMap(val objectType: ObjectList,anotherName: String = objectType.resourcesName) : AGameObject(anotherName) {
    protected fun createCHealth(maxHealth: Float, count: Int = 1, itemType: ItemList?, beforeDeadFunc: (() -> Unit)? = null) {
        create<CHealth> {
            health = maxHealth
            beforeDead = beforeDeadFunc
            if(itemType!=null)
                this.itemTypeDrop = CHealth.DropAndCount(count,dropType = itemType )
        }
    }
}