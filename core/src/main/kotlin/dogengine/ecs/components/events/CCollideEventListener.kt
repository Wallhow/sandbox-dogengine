package dogengine.ecs.components.events

import com.badlogic.ashley.core.Component
import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.systems.physics.Collide

class CCollideEventListener(val func: (collide: Collide) -> Unit): Component {
    companion object : ComponentResolver<CCollideEventListener>(CCollideEventListener::class.java)
}