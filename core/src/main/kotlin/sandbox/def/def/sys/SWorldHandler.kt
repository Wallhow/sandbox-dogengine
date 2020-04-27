package sandbox.sandbox.def.def.sys

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ArrayMap
import dogengine.Kernel
import dogengine.ecs.components.components
import dogengine.ecs.components.create
import dogengine.ecs.components.createEntity
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.systems.SystemPriority
import dogengine.utils.Size
import dogengine.utils.log
import sandbox.go.environment.ItemList
import sandbox.go.environment.items.AItemOnMap
import sandbox.go.environment.items.Shadow
import sandbox.go.environment.objects.buiding.Workbench
import sandbox.sandbox.def.def.comp.CToBuild
import sandbox.sandbox.def.def.comp.CToDrop
import sandbox.sandbox.def.def.interfaces.IBuilder
import sandbox.sandbox.go.environment.ObjectList

class SWorldHandler: EntitySystem() {
    init {
        priority = SystemPriority.BEFORE_UPDATE-1

        //Конструкции
        builders.addBuilder(ObjectList.WORKBENCH,builder {
            Workbench(it)
        })

        //Предметы
        ItemList.values().forEach {
            builders.addBuilder(it,builder { position ->
                createDefEntity(it,position)
            })
        }
    }
    private inline fun builder(crossinline block : (position: Vector2) -> Entity) : IBuilder {
        return object : IBuilder {
            override fun build(position: Vector2): Entity = block(position)
        }
    }
    private fun createDefEntity(itemType: ItemList, position: Vector2) : Entity {
        return object : AItemOnMap(itemType,position.y) {
            init {
                createCTransform(position.cpy(), Size(24f,24f))
                createCAtlasRegion(itemType.resourcesName)
                createCDrop(0.75f)
                createCUpdate {  }
                horizontalLine = position.y
                engine.addEntity(Shadow(this))
            }
        }
    }

    override fun update(deltaTime: Float) {
        engine.getEntitiesFor(Family.all(CToDrop::class.java).get()).forEach {
            builders[CToDrop[it].type]?.let {builder->
                engine.addEntity(builder.build(CTransforms[it].position))
                it.create<CDeleteMe>()
            }
        }
        engine.getEntitiesFor(Family.all(CToBuild::class.java).get()).forEach {
            builders[CToBuild[it].type]?.let {builder->
                engine.addEntity(builder.build(CTransforms[it].position))
                it.create<CDeleteMe>()
            }
        }
        super.update(deltaTime)
    }

    companion object {
        val builders : Builders = Builders()

        var itemIDBuild : ItemList? = null

        fun createItem(type: ItemList, pos: Vector2) {
            val e = Kernel.getInjector().getInstance(Engine::class.java).createEntity {
                components {
                    create<CToDrop> { this.type = type }
                    create<CTransforms> {
                        this.position.set(pos.x,pos.y)
                    }
                }
            }
            Kernel.getInjector().getInstance(Engine::class.java).addEntity(e)
        }
        fun createConstruct(type: ObjectList, pos: Vector2) {
            val e = Kernel.getInjector().getInstance(Engine::class.java).createEntity {
                components {
                    create<CToBuild> { this.type = type }
                    create<CTransforms> {
                        this.position.set(pos.x,pos.y)
                    }
                }
            }
            Kernel.getInjector().getInstance(Engine::class.java).addEntity(e)
        }
    }
}

class Builders {
    private val dropArray : ArrayMap<ItemList, IBuilder> = ArrayMap()
    private val constructArray : ArrayMap<ObjectList, IBuilder> = ArrayMap()

    operator fun get(itemType: ItemList) = dropArray[itemType]
    operator fun get(objectType: ObjectList) = constructArray[objectType]

    fun addBuilder(type: ItemList, builder: IBuilder) {
        dropArray.put(type,builder)
    }
    fun addBuilder(type: ObjectList, builder: IBuilder) {
        constructArray.put(type,builder)
    }

}