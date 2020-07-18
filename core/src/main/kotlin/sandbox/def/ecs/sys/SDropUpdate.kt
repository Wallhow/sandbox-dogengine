package sandbox.def.ecs.sys

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
import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent
import sandbox.sandbox.go.objects.ItemList
import sandbox.sandbox.def.def.comp.CDrop
import sandbox.sandbox.go.player.Player

class SDropUpdate(private val player: Player) : IteratingSystem(Family.all(CDrop::class.java).exclude(CHide::class.java).get()) {
    private var interpolation: Interpolation = Interpolation.fade
    private val durationNotTake = 0.5f
    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (CInterpolation[entity] != null) {
            CDrop[entity].currentTime += deltaTime
            CInterpolation[entity].apply {
                accTime += deltaTime
                fly(entity, this)
            }

            if (CDrop[entity].currentTime >= durationNotTake) {
                takingObj(entity)
            }
        } else {
            drop(entity, deltaTime)
        }
    }

    private fun fly(entity: Entity, interp: CInterpolation) {
        val tr = CTransforms[entity]
        tr.position.y = Interpolation.fade.apply(interp.from, interp.to, interp.accTime / interp.time)
        if (interp.accTime >= interp.time) {
            interp.accTime = 0f
            val tmp = interp.from
            interp.from = interp.to
            interp.to = tmp
        }
    }

    private fun takingObj(entity: Entity) {
        val drop = CDrop[entity]
        val tr = CTransforms[entity]
        if (tr.getRect().overlaps(CDefaultPhysics2d[player].rectangleBody)) {
            player.getInventory().push(drop.itemID as ItemList)
            entity.create<CDeleteMe>()
        }
    }

    private fun drop(entity: Entity, deltaTime: Float) {
        val drop = CDrop[entity]
        val tr = CTransforms[entity]

        drop.apply {
            if (dirty) {
                dirty = false
                if (y == 0f) {
                    y = tr.position.y
                }
                val dirX = if(player.directionSee==Player.DirectionSee.LEFT) -1f else
                    if (player.directionSee==Player.DirectionSee.RIGHT) 1f else MathUtils.random(-1.0f, 1.0f)
                val velX = MathUtils.random(50f, 70f)
                val velY = MathUtils.random(100f, 150f)
                velocity.set(velX, velY)
                direction.set(dirX, 1f)
            } else {
                if (velocity.y > 1f) {
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
                } else {
                    drop.currentTime = 0f
                    entity.create<CInterpolation> {
                        from = y + 2
                        to = y + 16f
                        time = 1f
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

    class CInterpolation : PoolableComponent {
        var from: Float = 0f
        var to: Float = 0f
        var time: Float = 0f
        var accTime: Float = 0f

        companion object : ComponentResolver<CInterpolation>(CInterpolation::class.java)

        override fun reset() {
            from = 0f
            to = 0f
            time = 0f
            accTime = 0f
        }
    }
}