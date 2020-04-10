package sandbox.sandbox.def.def.sys

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.CDeleteComponent
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.systems.SystemPriority
import sandbox.sandbox.def.def.comp.CExtraction
import sandbox.sandbox.def.def.comp.CHealth
import sandbox.go.environment.items.dropOnMap
import sandbox.go.environment.ItemList

class SExtraction : IteratingSystem(Family.all(CHealth::class.java,CExtraction::class.java).get()) {
    init {
        priority = SystemPriority.UPDATE
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val health = CHealth[entity]
        val ext = CExtraction[entity]
        health.health-=ext.force
        health.shot?.invoke()
        if(health.health<0) {
            if(health.itemTypeDrop==null) {
                health.beforeDead?.invoke()
                entity.create<CDeleteMe>()
            } else {
                health.itemTypeDrop?.let {
                    entity.dropOnMap(it.minCount,it.maxCount,drop = it.dropType as ItemList)
                    entity.create<CDeleteMe>()
                }
            }

        }
        entity.create<CDeleteComponent> {
            componentRemove = ext
        }
    }
}