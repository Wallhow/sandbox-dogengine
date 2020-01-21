package sandbox.sandbox.go.environment

import dogengine.ecs.components.create
import sandbox.sandbox.def.def.comp.CHealth
import sandbox.sandbox.go.AGameObject
import sandbox.sandbox.go.items.ObjectList

abstract class AGOMap (name: String) : AGameObject(name) {
    abstract override val dropID: ObjectList
    protected fun createCHealth(maxHealth : Float, beforeDeadFunc: () -> Unit) {
        create<CHealth> {
            health = maxHealth
            beforeDead = beforeDeadFunc
        }
    }
}