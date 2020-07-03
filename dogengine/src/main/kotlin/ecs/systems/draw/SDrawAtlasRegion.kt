package dogengine.ecs.systems.draw


import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import dogengine.ecs.components.createComponent
import dogengine.ecs.components.draw.CAtlasRegion
import dogengine.ecs.components.draw.CAtlasRegionAnimation
import dogengine.ecs.components.draw.CDraw
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.systems.SystemPriority

class SDrawAtlasRegion : IteratingSystem(Family.all(CTransforms::class.java, CAtlasRegion::class.java)
        .exclude(CHide::class.java, CAtlasRegionAnimation::class.java).get()) {
    init {
        priority = SystemPriority.DRAW-20
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val it = CAtlasRegion[entity]

        val drawable = createComponent<CDraw> {
            this.offsetX = it.padding.x.toInt()
            this.offsetY = it.padding.y.toInt()
            this.tint = it.color
            this.drawLayer = it.drawLayer
            this.texture = if (it.index >= 0) {
                it.atlas!!.findRegion(it.nameRegion, it.index)
            } else {
                it.atlas!!.findRegion(it.nameRegion)
            }
        }
        entity.add(drawable)
    }

}