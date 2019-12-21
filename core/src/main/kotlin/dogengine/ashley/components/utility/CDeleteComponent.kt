package sandbox.dogengine.ashley.components.utility

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import dogengine.def.ComponentResolver

class CDeleteComponent : Component, Pool.Poolable {
    companion object : ComponentResolver<CDeleteComponent>(CDeleteComponent::class.java)
    var componentRemove : Component? = null

    override fun reset() {
        componentRemove = null
    }
}