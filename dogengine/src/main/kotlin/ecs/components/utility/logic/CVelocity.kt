package dogengine.ecs.components.utility.logic

import com.badlogic.gdx.math.Vector2
import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent
import dogengine.utils.vec2

class CVelocity : PoolableComponent {
    var vector: Vector2 = vec2(0f,0f)
    override fun reset() {
        vector = Vector2.Zero.cpy()
    }

    companion object : ComponentResolver<CVelocity>(CVelocity::class.java)
}