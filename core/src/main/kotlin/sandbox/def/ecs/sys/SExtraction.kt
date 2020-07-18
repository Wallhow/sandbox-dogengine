package sandbox.def.ecs.sys

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.CDeleteComponent
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.systems.SystemPriority
import dogengine.utils.log
import map2D.TypeData
import sandbox.sandbox.def.def.comp.CExtraction
import sandbox.sandbox.def.def.comp.CHealth
import sandbox.go.environment.items.dropOnMap
import sandbox.sandbox.go.objects.ItemList
import sandbox.sandbox.def.def.world.LayerNames

class SExtraction : IteratingSystem(Family.all(CHealth::class.java, CExtraction::class.java).get()) {
    init {
        priority = SystemPriority.UPDATE
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val health = CHealth[entity]
        val ext = CExtraction[entity]
        health.health -= ext.force
        health.shot?.invoke()
        if (health.health < 0) {
            if (health.itemTypeDrop == null) {
                health.beforeDead?.invoke()
                entity.create<CDeleteMe>()
            } else {
                health.itemTypeDrop?.let {
                    entity.dropOnMap(it.minCount, it.maxCount, drop = it.dropType as ItemList)
                    entity.create<CDeleteMe>()
                }
            }
            val s = CTransforms[entity].size
            log(entity)
            for (x1 in 0..(s.width/32).toInt()) {
                val x = (CTransforms[entity].position.x/32).toInt()
                val y =(CTransforms[entity].position.y/32).toInt()

                val cell = SWorldHandler.worldManager.getCell(x+x1, y, LayerNames.OBJECTS)
                cell.data.put(TypeData.ObjectOn, null)
            }

        }
        entity.create<CDeleteComponent> {
            componentRemove = ext
        }
    }
}