package dogengine.ecs.components

import com.badlogic.ashley.core.Entity
import dogengine.ecs.components.utility.logic.CTransforms

fun Entity.getCTransforms() = CTransforms[this]