package sandbox.sandbox.def.def.sys

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.logic.CDefaultPhysics2d
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.utils.log
import sandbox.sandbox.def.def.comp.CDrop
import sandbox.sandbox.go.player.Player

class SDrop (private val player: Player) : IteratingSystem(Family.all(CDrop::class.java).exclude(CHide::class.java).get()) {
    private var interpolation: Interpolation = Interpolation.linear
    private val durationNotTake = 1.5f
    override fun processEntity(entity: Entity, deltaTime: Float) {
        drop(entity,deltaTime)
        takingObj(entity,deltaTime)
    }
    private fun takingObj(entity: Entity, deltaTime: Float) {
        val drop = CDrop[entity]
        val tr = CTransforms[entity]
        //можно поднять только после заданного времени
        if(drop.currentTime >= 0) {
            if (tr.getRect().overlaps(CDefaultPhysics2d[player].rectangleBody)) {
                log(player.getInventory().push(drop.itemID!!))
                entity.create<CDeleteMe>()
            }
        }
    }

    private fun drop(entity: Entity, deltaTime: Float) {
        val drop = CDrop[entity]
        val tr = CTransforms[entity]

        drop.apply {
            if(dirty) {
                dirty = false
                if(y==0f) {
                    y = tr.position.y
                }
                val dirX = MathUtils.random(-1.0f,1.0f)
                val velX = MathUtils.random(50f,70f)
                val velY = MathUtils.random(100f,200f)
                velocity.set(velX,velY)
                direction.set(dirX,1f)
            } else {
                if (velocity.y != 0f) {
                    currentTime += deltaTime
                    velocity.x *= 0.99f

                    val dX = velocity.x * direction.x * deltaTime
                    val iDirectionY = interpolation.apply(direction.y, -direction.y, currentTime / time)
                    val dY = velocity.y * iDirectionY * deltaTime
                    tr.position.add(dX, dY)
                    boundsVelocity(velocity)
                    if (tr.position.y <= y) {
                        currentTime = 0f
                        direction.y = -iDirectionY
                        velocity.y *= 0.5f
                    }
                }
            }
        }
    }

    private fun boundsVelocity(velocity: Vector2) {
        if (velocity.x != 0f && velocity.x < 1 && velocity.x > -1)
            velocity.x = 0f
        if (velocity.y != 0f && velocity.y < 2 && velocity.y > -2)
            velocity.y = 0f
    }
}