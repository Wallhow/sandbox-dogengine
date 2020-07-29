package sandbox.sandbox.def.def.comp

import com.badlogic.gdx.math.Vector2
import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent

class CShack : PoolableComponent {
companion object: ComponentResolver<CShack>(CShack::class.java)
    val beforePosition = Vector2(-1f,-1f)
    var powerShake = 1f
    var duration = 0f
    var time = 0f
    var repeat = false
    override fun reset() {
        beforePosition.setZero()
        powerShake = 2f
        duration = 0f
        time = 0f
        repeat = false
    }
}