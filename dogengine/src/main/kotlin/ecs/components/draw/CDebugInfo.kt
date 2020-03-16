package dogengine.ecs.components.draw

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import dogengine.ecs.def.ComponentResolver

class CDebugInfo : Component, Pool.Poolable {
    var info = ""
    var drawBox = false
    var drawInfo = true
    override fun reset() {
        info = ""
        drawBox = false
        drawInfo = true
    }
    companion object : ComponentResolver<CDebugInfo>(CDebugInfo::class.java)

}

