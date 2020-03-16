package dogengine.ecs.components.utility.visible

import box2dLight.PointLight
import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import dogengine.ecs.def.ComponentResolver

class CLightBox2D : Component, Pool.Poolable {
    var radius: Float = 0f
    var pointLight: PointLight? = null
    override fun reset() {
        radius = 0f
        pointLight = null
    }

    companion object : ComponentResolver<CLightBox2D>(CLightBox2D::class.java)
}