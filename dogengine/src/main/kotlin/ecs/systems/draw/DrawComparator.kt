package dogengine.ecs.systems.draw

import com.badlogic.ashley.core.Entity
import dogengine.ecs.components.utility.logic.CTransforms

object DrawComparator {
    val comparator: Comparator<Entity> =
        Comparator { e1: Entity, e2: Entity ->
            when {
                CTransforms[e1].zIndex < CTransforms[e2].zIndex -> -1
                CTransforms[e1].zIndex > CTransforms[e2].zIndex -> 1
                else -> 0
            }
        }

}