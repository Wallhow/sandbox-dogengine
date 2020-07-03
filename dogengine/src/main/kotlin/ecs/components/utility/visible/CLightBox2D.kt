package dogengine.ecs.components.utility.visible

import box2dLight.*
import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import dogengine.ecs.def.ComponentResolver

class CLightBox2D : Component, Pool.Poolable {
    var radius: Float = 0f
    var light: Light? = null
    var type : LightType = LightType.POINT
    override fun reset() {
        radius = 0f
        light = null
        type = LightType.POINT
    }
    companion object : ComponentResolver<CLightBox2D>(CLightBox2D::class.java)
}
enum class LightType(lightType: Class<out Light>) {
    POINT (PointLight::class.java),
    CONE(ConeLight::class.java),
    DIRECTIONAL(DirectionalLight::class.java),
    CHAIN(ChainLight::class.java)
}