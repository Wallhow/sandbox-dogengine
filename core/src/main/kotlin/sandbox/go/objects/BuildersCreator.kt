package sandbox.sandbox.go.objects

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.create
import dogengine.ecs.components.draw.CFixedY
import dogengine.utils.Size
import sandbox.dev.ecs.interfaces.IBuilder
import sandbox.dev.world.Builders
import sandbox.go.environment.items.AItemOnMap
import sandbox.go.environment.items.Shadow
import sandbox.go.environment.objects.buiding.Workbench
import sandbox.sandbox.go.environment.objects.buiding.Bonfire

object BuildersCreator {
    fun create(builders: Builders) {
        //Конструкции
        builders.addBuilder(ObjectList.WORKBENCH, builder {
            Workbench(it)
        })
        builders.addBuilder(ObjectList.BONFIRE1, builder {
            Bonfire(it)
        })

        //Предметы
        ItemList.values().forEach { it ->
            builders.addBuilder(it, builder { position ->
                createDefEntity(it, position)
            })
        }
    }

    private inline fun builder(crossinline block: (position: Vector2) -> Entity): IBuilder {
        return object : IBuilder {
            override fun build(position: Vector2): Entity = block(position)
        }
    }

    private fun createDefEntity(itemType: ItemList, position: Vector2): Entity {
        return object : AItemOnMap(itemType, position.y) {
            init {
                createCTransform(position.cpy(), Size(24f, 24f))
                createCAtlasRegion(itemType.resourcesName)
                createCDrop(0.75f)
                createCUpdate { }
                horizontalLine = position.y
                create<CFixedY> {
                    y = position.y
                }
                engine.addEntity(Shadow(this))
            }
        }
    }

}