package sandbox.dogengine.ecs.components.controllers

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent

class CControllable : PoolableComponent {
    var eventListener: EventListener? = null
    override fun reset() {
        eventListener = null
    }

    companion object : ComponentResolver<CControllable>(CControllable::class.java)
}