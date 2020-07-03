package dogengine.map2D.comp

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import dogengine.ecs.def.ComponentResolver
import dogengine.map2D.Cell

class CCell : Component, Pool.Poolable {
    companion object : ComponentResolver<CCell>(CCell::class.java)
    var cell: Cell? = null
    override fun reset() {
        cell = null
    }
}