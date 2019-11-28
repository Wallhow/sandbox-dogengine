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
        println(p0.userData)
        println(p1.userData)
        Response.touch
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

                CJBumpAABB[entity].item = world.add(Item<Entity>(entity), position.x, position.y, size.width, size.height)
                println(entity)
                println(CJBumpAABB[entity].item)
            }
        })
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val pos = CTransforms[entity].position
        val result: Response.Result = world.move(CJBumpAABB[entity].item, pos.x, pos.y, collisionFilter)
        val projectedCollisions: Collisions = result.projectedCollisions
        val touched = com.badlogic.gdx.utils.Array<Item<Entity>>()
        for (i in 0 until projectedCollisions.size()) {
            val col = projectedCollisions[i]
            touched.add(col.other as Item<Entity>)
        }

    }

}