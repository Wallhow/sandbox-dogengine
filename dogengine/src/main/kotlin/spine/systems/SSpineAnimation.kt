package dogengine.spine.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.esotericsoftware.spine.SkeletonRenderer
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.spine.components.CSpineSkeleton

class SSpineAnimation : IteratingSystem(Family.all(CTransforms::class.java,CSpineSkeleton::class.java).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val tr = CTransforms[entity]
        val skeleton = CSpineSkeleton[entity]

        skeleton.skeleton?.let { s->
            s.setPosition(tr.position)
            s.rotation = tr.angle
        }
    }
}