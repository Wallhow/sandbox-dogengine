package dogengine.ecs.components.utility

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import dogengine.ecs.def.ComponentResolver

class CName : Component, Pool.Poolable {
    var name: String? = null
    override fun reset() {
        name = null
    }

    companion object : ComponentResolver<CName>(CName::class.java)
}
