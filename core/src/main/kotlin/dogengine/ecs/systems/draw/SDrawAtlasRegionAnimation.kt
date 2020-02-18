package dogengine.ecs.systems.draw


import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.utils.GdxRuntimeException
import dogengine.ecs.components.create
import dogengine.ecs.components.draw.CAtlasRegion
import dogengine.ecs.components.draw.CAtlasRegionAnimation
import dogengine.ecs.components.draw.CDrawable
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.systems.SystemPriority

class SDrawAtlasRegionAnimation : IteratingSystem(Family.all(CTransforms::class.java, CAtlasRegion::class.java, CAtlasRegionAnimation::class.java)
                .exclude(CHide::class.java).get()) {
    init {
        priority = SystemPriority.DRAW-20
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val it = CAtlasRegion[entity]
        CAtlasRegionAnimation[entity].frameSequenceArray.anim(deltaTime)
        val index = CAtlasRegionAnimation[entity].frameSequenceArray.getCurrentFrame()

        val region = it.atlas?.findRegion(it.nameRegion,index) ?: throw GdxRuntimeException("frame $index in ${entity} not found")
        entity.create<CDrawable> {
            texture = region
            tint = it.color
            drawLayer = it.drawLayer
        }
    }

}