package sandbox.dev.ecs.sys

import com.badlogic.ashley.core.*
import com.badlogic.ashley.signals.Listener
import com.badlogic.ashley.signals.Signal
import com.google.inject.Inject
import com.google.inject.Key
import dogengine.Kernel
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.systems.SystemPriority
import sandbox.sandbox.go.objects.ItemList
import dogengine.ecs.components.addEntityAddedListener
import dogengine.ecs.systems.tilemap.CMap2D
import dogengine.ecs.systems.utility.STime
import dogengine.utils.extension.get
import dogengine.utils.log
import dogengine.utils.system
import map2D.TypeData
import map2D.Vector2Int
import sandbox.MainSandbox
import sandbox.dev.ecs.comp.CToBuild
import sandbox.dev.ecs.comp.CToDrop
import sandbox.dev.ecs.world.*
import sandbox.sandbox.def.def.world.LayerNames
import sandbox.sandbox.go.objects.BuildersCreator

class SWorldHandler @Inject constructor(private val messenger: Messenger): EntitySystem() {
    init {
        priority = SystemPriority.BEFORE_UPDATE-1
        BuildersCreator.create(builders)
    }

    override fun addedToEngine(engine: Engine) {
        engine.addEntityAddedListener(Family.all(CMap2D::class.java).get()) {
            worldManager = WorldManager(CMap2D[it].map2D!!)
        }

        messenger.addListener(2, Listener<Message> { _, msg ->
            log(msg.data as String)
        })


        messenger.addListener(MessagesType.EXTRACTION, Listener<Message> { _, msg ->
            val pos = msg.data as Vector2Int
            val cell = worldManager.getCell(pos.x, pos.y, LayerNames.OBJECTS)
            cell.data.put(TypeData.ObjectOn, null)
        })

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