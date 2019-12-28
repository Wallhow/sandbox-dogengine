package sandbox.def

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.dongbat.jbump.Item
import dogengine.ecs.def.ComponentResolver

class CJBumpAABB : Component, Pool.Poolable {
    var item: Item<Entity>? = null
    val scaleSize : Vector2 = Vector2(1f,1f)
    var dynamic: Boolean = false
    val positionOffset = Vector2()
    companion object : ComponentResolver<CJBumpAABB>(CJBumpAABB::class.java)

    override fun reset() {
        item = null
        scaleSize.set(1f,1f)
        dynamic = false
        positionOffset.setZero()
    }
}
