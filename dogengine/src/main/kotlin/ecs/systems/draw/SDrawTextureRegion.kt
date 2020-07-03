package dogengine.ecs.systems.draw

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import dogengine.ecs.components.create
import dogengine.ecs.components.draw.CDraw
import dogengine.ecs.components.draw.CTextureRegion
import dogengine.ecs.components.draw.CTextureRegionAnimation
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.systems.SystemPriority

/**
 * Created by Wallhow on 29.10.18
 */
class SDrawTextureRegion : IteratingSystem(Family.all(CTransforms::class.java, CTextureRegion::class.java)
        .exclude(CHide::class.java, CTextureRegionAnimation::class.java).get()) {
    init {
        priority = SystemPriority.DRAW-20
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val it = CTextureRegion[entity]
        entity.create<CDraw> {
            texture = getFrame(entity)
            tint = it.color
            drawLayer = it.drawLayer
            offsetX = it.offsetX
            offsetY = it.offsetY
        }
    }


    private fun getFrame(e:Entity) = CTextureRegion[e].texture
}
