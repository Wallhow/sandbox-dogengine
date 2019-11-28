package sandbox.def

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.dongbat.jbump.*
import com.google.inject.Inject
import dogengine.ashley.components.CTransforms


class SJBumpAABB @Inject constructor(engine: Engine): IteratingSystem(Family.all(CJBumpAABB::class.java, CTransforms::class.java).get()) {
    private val world: World<Entity> = World()
    private val collisionFilter = CollisionFilter { p0, p1 ->
        Response.bounce
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
                val nW = (size.width - size.width*CJBumpAABB[entity].scaleSize.x)*0.5f
                val nH = (size.height - size.height*CJBumpAABB[entity].scaleSize.y)*0.5f
                CJBumpAABB[entity].item = world.add(Item<Entity>(entity), position.x+nW, position.y+nH,
                        size.width*CJBumpAABB[entity].scaleSize.x,
                        size.height*CJBumpAABB[entity].scaleSize.y)
                println(entity)
                println(CJBumpAABB[entity].item)
            }
        })
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val pos = CTransforms[entity].position
        val size = CTransforms[entity].size
        val jbump = CJBumpAABB[entity]
        if(jbump.dynamic) {
            val nW = (size.width - size.width * jbump.scaleSize.x) * 0.5f
            val nH = (size.height - size.height * jbump.scaleSize.y) * 0.5f
            val result: Response.Result = world.move(jbump.item, pos.x + nW, pos.y + nH, collisionFilter)
            pos.set(result.goalX - nW, result.goalY - nH)
        }
    }

}