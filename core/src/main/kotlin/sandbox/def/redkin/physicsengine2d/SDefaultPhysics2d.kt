package sandbox.sandbox.def.redkin.physicsengine2d

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.google.inject.Inject
import dogengine.es.redkin.physicsengine2d.bodies.RectangleBody
import dogengine.es.redkin.physicsengine2d.variables.Types
import dogengine.es.redkin.physicsengine2d.world.World
import sandbox.dogengine.ecs.components.utility.logic.CTransforms
import sandbox.dogengine.ecs.components.utility.visible.CHide

class SDefaultPhysics2d @Inject constructor(val world: World) : IteratingSystem(Family.all(CTransforms::class.java, CDefaultPhysics2d::class.java).exclude(CHide::class.java).get()) {
    init {
        priority = 200
    }

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(Family.all(CDefaultPhysics2d::class.java).get(),
                99,
                DefaultPhysics2dComponentListener(world))
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val physics = CDefaultPhysics2d[entity]
        val t = CTransforms[entity]
        physics.rectangleBody?.apply {
            if (type == Types.TYPE.DYNAMIC)
                t.position.set(x * world.PPU - physics.offset.x, y * world.PPU - physics.offset.y)

        }
    }

    override fun update(deltaTime: Float) {
        world.updateWorld(deltaTime)
        super.update(deltaTime)
    }

    private class DefaultPhysics2dComponentListener(private val world: World) : EntityListener {
        override fun entityRemoved(entity: Entity) {
            CDefaultPhysics2d[entity]?.apply {
                if (rectangleBody != null) {
                    world.removeRectangleBody(rectangleBody!!)
                }
            }
        }

        override fun entityAdded(entity: Entity) {
            CDefaultPhysics2d[entity]?.apply {
                val t = CTransforms[entity]
                if (rectangleBody == null) {
                    val rectBody = RectangleBody(t.position.x / world.PPU,
                            t.position.y / world.PPU,
                            t.size.width / world.PPU,
                            t.size.height / world.PPU
                            , type, name)
                    rectangleBody = rectBody
                    world.addRectangleBody(rectBody)
                } else {
                    world.addRectangleBody(rectangleBody!!.apply {
                        x = (x + offset.x) / world.PPU
                        y = (y + offset.y) / world.PPU
                        width /= world.PPU
                        height /= world.PPU
                    })
                }
            }
        }
    }
}