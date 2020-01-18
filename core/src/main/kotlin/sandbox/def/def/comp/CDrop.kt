package sandbox.sandbox.def.def.comp

import com.badlogic.gdx.math.Vector2
import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent

class CDrop : PoolableComponent {
    companion object : ComponentResolver<CDrop>(CDrop::class.java)
    val velocity: Vector2 = Vector2()
    val from = Vector2()
    val to = Vector2()
    var time = 0f
    var currentTime = 0f
    var step1 = true
    var step2 = false

    override fun reset() {
        velocity.setZero()
        time = 0f
        currentTime = 0f
        from.setZero()
        to.setZero()
    }
}