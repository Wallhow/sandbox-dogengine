package sandbox.dev.ecs.sys

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.google.inject.Inject
import dogengine.ecs.components.addEntityAddedListener
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.systems.SystemPriority
import dogengine.ecs.systems.tilemap.CMap2D
import sandbox.dev.ecs.comp.CToBuild
import sandbox.dev.ecs.comp.CToDrop
import sandbox.dev.world.Builders
import sandbox.dev.world.IWorldManager
import sandbox.dev.world.MessageListeners
import sandbox.dev.world.WorldManager
import sandbox.sandbox.go.objects.BuildersCreator
import sandbox.sandbox.go.objects.ItemList

class SWorldHandler @Inject constructor() : EntitySystem() {
    init {
        priority = SystemPriority.BEFORE_UPDATE - 1
        BuildersCreator.create(builders)
    }

    override fun addedToEngine(engine: Engine) {
        engine.addEntityAddedListener(Family.all(CMap2D::class.java).get()) {
            worldManager = WorldManager(CMap2D[it].map2D!!)
            MessageListeners(worldManager)
        }


        super.addedToEngine(engine)
    }


    override fun update(deltaTime: Float) {
        engine.getEntitiesFor(Family.all(CToDrop::class.java).get()).forEach {
            builders[CToDrop[it].type]?.let { builder ->
                engine.addEntity(builder.build(CTransforms[it].position))
                it.create<CDeleteMe>()
            }
        }
        engine.getEntitiesFor(Family.all(CToBuild::class.java).get()).forEach {
            builders[CToBuild[it].type]?.let { builder ->
                engine.addEntity(builder.build(CTransforms[it].position))
                it.create<CDeleteMe>()
            }
        }
        super.update(deltaTime)
    }

    companion object {
        lateinit var worldManager: IWorldManager
        val builders: Builders = Builders()
        var itemIDBuild: ItemList? = null
    }
}