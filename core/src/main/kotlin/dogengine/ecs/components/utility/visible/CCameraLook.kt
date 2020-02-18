package dogengine.ecs.components.utility.visible

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import dogengine.ecs.def.ComponentResolver

/**
 * Created by wallhow on 20.12.16.
 */
class CCameraLook : Component, Pool.Poolable {
    override fun reset() {}

    companion object : ComponentResolver<CCameraLook>(CCameraLook::class.java)
}