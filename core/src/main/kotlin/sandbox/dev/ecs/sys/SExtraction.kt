package sandbox.dev.ecs.sys

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.msg.MessageManager
import dogengine.Kernel
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.CDeleteComponent
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.systems.SystemPriority
import dogengine.utils.extension.get
import map2D.Vector2Int
import sandbox.dev.world.MessagesType
import sandbox.go.environment.items.dropOnMap
import sandbox.sandbox.def.def.comp.CExtraction
import sandbox.dev.ecs.comp.CHealth
import sandbox.sandbox.go.objects.ItemList

class SExtraction : IteratingSystem(Family.all(CHealth::class.java, CExtraction::class.java).get()) {
    private val messenger = Kernel.getInjector()[MessageManager::class.java]

    init {
        priority = SystemPriority.UPDATE
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val health = CHealth[entity]
        val ext = CExtraction[entity]
        health.health -= ext.force
        health.shot?.invoke()
        if (health.health < 0) {
            val s = CTransforms[entity].size.cpy()
            if (health.itemTypeDrop == null) {
                health.beforeDead?.invoke()
                entity.create<CDeleteMe>()
            } else {
                health.itemTypeDrop?.let {
                    entity.dropOnMap(it.minCount, it.maxCount, drop = it.dropType as ItemList,size = s)
                    entity.create<CDeleteMe>()
                }
            }

            for (x1 in 0..(s.width / 32).toInt()) {
                val x = (CTransforms[entity].position.x / 32).toInt()
                val y = (CTransforms[entity].position.y / 32).toInt()

                messenger.dispatchMessage(MessagesType.WORLD_EXTRACTION, Vector2Int(x + x1, y))
                //Отправляем сообщение
                //о том что мы разрушили объект в данной ячейке
            }

        }
        entity.create<CDeleteComponent> {
            componentRemove = ext
        }
    }
}