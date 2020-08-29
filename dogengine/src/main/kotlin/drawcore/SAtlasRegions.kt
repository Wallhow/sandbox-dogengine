package dogengine.drawcore

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import dogengine.ecs.components.addEntityAddedListener
import dogengine.ecs.systems.SystemPriority

class SAtlasRegions : EntitySystem(SystemPriority.DRAW - 1) {
    override fun addedToEngine(engine: Engine) {
        engine.addEntityAddedListener(Family.all(CAtlasRegions::class.java).get()) {
            if (CDrawable[it] == null) {
                val drawable = engine.createComponent(CDrawable::class.java)
                drawable.batchable = true
                drawable.drawType = DrawTypes.MULTI_REGIONS
                it.add(drawable)
            }
        }
        super.addedToEngine(engine)
    }
}