package sandbox.dogengine.ecs.def

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

interface PoolableComponent : Component, Pool.Poolable {
    companion object {
        val tmpString = ""
    }
}