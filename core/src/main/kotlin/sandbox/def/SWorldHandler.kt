package sandbox.def

import com.badlogic.ashley.core.EntitySystem
import dogengine.ecs.def.GameEntity
import dogengine.ecs.systems.SystemPriority
import dogengine.utils.log
import sandbox.go.environment.drop.models.GrassDrop
import sandbox.go.environment.drop.models.RockDrop
import sandbox.go.environment.drop.models.SandstoneDrop
import sandbox.go.environment.drop.models.WoodDrop
import sandbox.sandbox.def.def.comp.DropConfig
import sandbox.sandbox.go.items.ObjectList
import java.util.*

class SWorldHandler: EntitySystem() {
    init {
        priority = SystemPriority.BEFORE_UPDATE-1
    }

    override fun update(deltaTime: Float) {
        if(!worldEventDrop.stackDrop.empty()) {
            createDrop(worldEventDrop.stackDrop.pop())
        }
        super.update(deltaTime)
    }
    //TODO Тут централизованное создание дроп Объектов
    private fun createDrop(dropConfig: DropConfig) {
        dropConfig.let {
            val pos = it.position
            when (it.type) {
                ObjectList.GRASS -> {
                    engine.addEntity(GrassDrop(pos, pos.y))
                }
                ObjectList.WOOD -> {
                    engine.addEntity(WoodDrop(pos, pos.y))
                }
                ObjectList.SANDSTONE -> {
                    engine.addEntity(SandstoneDrop(pos, pos.y))
                }
                ObjectList.ROCK -> {
                    engine.addEntity(RockDrop(pos, pos.y))
                }
            }
        }
        if(!worldEventDrop.stackDrop.empty()) {
            createDrop(worldEventDrop.stackDrop.pop())
        }
    }

    companion object {
        val worldEventDrop = WorldEventDrop()
    }

    class WorldEvent : GameEntity() {
        init {
            name = "worldEvent"
        }
    }
    class WorldEventDrop {
        val stackDrop : Stack<DropConfig> = Stack()
    }
}