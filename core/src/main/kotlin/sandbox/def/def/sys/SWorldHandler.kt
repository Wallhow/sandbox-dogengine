package sandbox.def.def.sys

import com.badlogic.ashley.core.*
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.systems.SystemPriority
import dogengine.utils.Size
import sandbox.sandbox.go.objects.ItemList
import sandbox.go.environment.items.AItemOnMap
import sandbox.go.environment.items.Shadow
import sandbox.go.environment.objects.buiding.Workbench
import dogengine.ecs.components.draw.CFixedY
import dogengine.ecs.components.addEntityAddedListener
import dogengine.ecs.systems.tilemap.CMap2D
import sandbox.def.def.comp.CToBuild
import sandbox.def.def.comp.CToDrop
import sandbox.def.def.interfaces.IBuilder
import sandbox.def.def.world.Builders
import sandbox.def.def.world.IWorldManager
import sandbox.def.def.world.WorldManager
import sandbox.sandbox.go.objects.ObjectList
import sandbox.sandbox.go.environment.objects.buiding.Bonfire
import sandbox.sandbox.go.objects.BuildersCreator

class SWorldHandler: EntitySystem() {
    init {
        priority = SystemPriority.BEFORE_UPDATE-1
        BuildersCreator.create(builders)
    }

    override fun addedToEngine(engine: Engine) {
        engine.addEntityAddedListener(Family.all(CMap2D::class.java).get()) {
            worldManager = WorldManager(CMap2D[it].map2D!!)
        }
        super.addedToEngine(engine)
    }


    override fun update(deltaTime: Float) {
        engine.getEntitiesFor(Family.all(CToDrop::class.java).get()).forEach {
            builders[CToDrop[it].type]?.let { builder->
                engine.addEntity(builder.build(CTransforms[it].position))
                it.create<CDeleteMe>()
            }
        }
        engine.getEntitiesFor(Family.all(CToBuild::class.java).get()).forEach {
            builders[CToBuild[it].type]?.let { builder->
                engine.addEntity(builder.build(CTransforms[it].position))
                it.create<CDeleteMe>()
            }
        }
        super.update(deltaTime)
    }

    companion object {
        lateinit var worldManager: IWorldManager
        val builders : Builders = Builders()
        var itemIDBuild : ItemList? = null
    }
}