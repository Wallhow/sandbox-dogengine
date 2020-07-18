package sandbox.sandbox.go.environment

import dogengine.ecs.components.create
import sandbox.sandbox.go.objects.ItemList
import sandbox.sandbox.go.objects.ObjectList
import sandbox.sandbox.def.def.comp.CHealth
import sandbox.sandbox.def.def.comp.CObjectOnMap
import sandbox.sandbox.go.AGameObject

abstract class AGameObjectOnMap(val objectType: ObjectList, anotherName: String = objectType.resourcesName) : AGameObject(anotherName) {
    init {
        create<CObjectOnMap> {
            typeObject = objectType
        }
    }
    protected fun createCHealth(maxHealth: Float, count: Int = 1, itemType: ItemList?, beforeDeadFunc: (() -> Unit)? = null) {
        create<CHealth> {
            health = maxHealth
            beforeDead = beforeDeadFunc
            if(itemType!=null)
                this.itemTypeDrop = CHealth.DropAndCount(count,dropType = itemType )
        }
    }
}