package sandbox.sandbox.def.def.comp

import com.badlogic.gdx.math.Vector2
import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent

class CDrop : PoolableComponent {
    companion object : ComponentResolver<CDrop>(CDrop::class.java)

    var deltaY: Float = 1f
    val velocity: Vector2 = Vector2()
    val from = Vector2()
    val to = Vector2()
    val direction = Vector2(1f,-1f)
    var time = 0f
    var currentTime = 0f
    var dirty = true
    var step2 = false

    var y = 0f

    override fun reset() {
        velocity.setZero()
        time = 0f
        currentTime = 0f
        from.setZero()
        to.setZero()
    }
}