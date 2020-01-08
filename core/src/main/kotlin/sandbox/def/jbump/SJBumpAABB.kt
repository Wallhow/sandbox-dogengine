package sandbox.sandbox.def.jbump

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.dongbat.jbump.CollisionFilter
import com.dongbat.jbump.Item
import com.dongbat.jbump.Response
import com.dongbat.jbump.World
import com.google.inject.Inject
import dogengine.Kernel
import sandbox.dogengine.ecs.components.utility.logic.CTransforms


class SJBumpAABB @Inject constructor(engine: Engine): IteratingSystem(Family.all(CJBumpAABB::class.java, CTransforms::class.java).get()) {
    val world = Kernel.getInjector().getInstance(World::class.java) as World<Entity>
    init {
        world.isTileMode = false
    }
    var collisionListener : ((e1: Item<Entity>,e2: Item<Entity>) -> Unit)? = null
    private val collisionFilter = CollisionFilter { p0, p1 ->
        collisionListener?.invoke(p0 as Item<Entity>, p1 as Item<Entity>)
        Response.slide
    }
    init {
        engine.addEntityListener(Family.all(CJBumpAABB::class.java).get(), object : EntityListener {
            override fun entityRemoved(entity: Entity) {
                if (CJBumpAABB[entity].item != null) {
                    world.remove(CJBumpAABB[entity].item)
                }
            }

            override fun entityAdded(entity: Entity) {
                val position = CTransforms[entity].position
                val size = CTransforms[entity].size
                val nW = (size.width - size.width* CJBumpAABB[entity].scaleSize.x)*0.5f
                val nH = (size.height - size.height* CJBumpAABB[entity].scaleSize.y)*0.5f
                CJBumpAABB[entity].item = world.add(Item<Entity>(entity), position.x+nW, position.y+nH,
                        size.width* CJBumpAABB[entity].scaleSize.x,
                        size.height* CJBumpAABB[entity].scaleSize.y)
            }
        })
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        /*val pos = CTransforms[entity].position
        val jbump = CJBumpAABB[entity]
        if(jbump.dynamic) {
            val result: Response.Result = world.check(jbump.item, pos.x + jbump.positionOffset.x, pos.y + jbump.positionOffset.y, collisionFilter)
            result.projectedCollisions.items.forEach {
                println(CTransforms[it.userData as Entity].position)
            }
            result.projectedCollisions.others.forEach {
                println(CTransforms[it.userData as Entity].position)
            }
        }*/
    }

}