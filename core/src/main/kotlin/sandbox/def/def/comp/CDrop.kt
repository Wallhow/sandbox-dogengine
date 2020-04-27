package sandbox.sandbox.def.def.comp

import com.badlogic.gdx.math.Vector2
import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent
import sandbox.go.environment.ItemList

class CDrop : PoolableComponent {
    companion object : ComponentResolver<CDrop>(CDrop::class.java)
    val velocity: Vector2 = Vector2()
    val direction = Vector2(1f,-1f)
    var time = 0f
    var currentTime = 0f
    var dirty = true
    var y = 0f
    var itemID: ItemList? = null

    override fun reset() {
        velocity.setZero()
        time = 0f
        currentTime = 0f
        dirty = true
        y = 0f
        itemID = null
    }
}