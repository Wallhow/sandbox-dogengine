package dogengine.ashley.components.utility

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import dogengine.def.ComponentResolver

class CSpawn : Component, Pool.Poolable {
    override fun reset() {}

    companion object : ComponentResolver<CSpawn>(CSpawn::class.java)
}
