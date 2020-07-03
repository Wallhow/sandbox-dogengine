package dogengine.drawcore

import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent

class CDrawable: PoolableComponent {
    companion object: ComponentResolver<CDrawable>(CDrawable::class.java)

    var batchable: Boolean = true
    var drawType: Int = 0
    var entityDeleteAfterDraw = false
    override fun reset() {
        drawType = 0
        batchable = true
        entityDeleteAfterDraw = false
    }
}