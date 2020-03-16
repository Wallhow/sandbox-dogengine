package dogengine.ecs.components.events

import com.badlogic.ashley.core.Component
import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.systems.physics.Collide

class CCollideEvent(val collide: Collide) : Component {
    companion object : ComponentResolver<CCollideEvent>(CCollideEvent::class.java)


}
