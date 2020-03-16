package dogengine.ecs.components.utility.visible

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import dogengine.ecs.def.ComponentResolver

class CHide : Component, Pool.Poolable {
    companion object : ComponentResolver<CHide>(CHide::class.java)
    override fun reset() {}
}