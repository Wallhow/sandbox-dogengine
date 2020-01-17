package sandbox.sandbox.def.def.sys

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.CDeleteComponent
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.systems.SystemPriority
import sandbox.sandbox.def.def.comp.CShack

class SShack : IteratingSystem(Family.all(CTransforms::class.java,CShack::class.java).exclude(CHide::class.java).get()) {
    init {
        priority = SystemPriority.UPDATE
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val shack = CShack[entity]
        val tr = CTransforms[entity]

        shack.time+=deltaTime
        shack.apply {
            if(beforePosition.x == -1f && beforePosition.y == -1f) beforePosition.set(tr.position)
            tr.position.set(beforePosition)

            if(time<=duration) {
                tr.position.add(MathUtils.random(-powerShake,powerShake),
                        MathUtils.random(-powerShake,powerShake))
            } else {
                if (repeat) {
                    time = 0f
                } else {
                    entity.create<CDeleteComponent>() {
                        componentRemove = shack
                    }
                }
            }
        }
    }
}