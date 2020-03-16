package dogengine.ecs.components.utility.visible

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import dogengine.ecs.def.ComponentResolver

class CVisibleEntityListener : Component, Pool.Poolable {
    companion object : ComponentResolver<CVisibleEntityListener>(CVisibleEntityListener::class.java)
    var hide : ((entity: Entity) -> Unit)? = null
    var visible : ((entity: Entity) -> Unit)? = null
    override fun reset() {
        hide = null
        visible = null
    }
}