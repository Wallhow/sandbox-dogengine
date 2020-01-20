package sandbox.sandbox.def.def.sys

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.utils.log
import sandbox.sandbox.def.def.comp.CDrop

class SDrop : IteratingSystem(Family.all(CDrop::class.java).get()) {
    private var interpolation: Interpolation = Interpolation.linear
    private val g = 20f
    override fun processEntity(entity: Entity, deltaTime: Float) {
        v2(entity,deltaTime)
    }
    fun v2(entity: Entity,deltaTime: Float) {
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
    fun v1(entity: Entity, deltaTime: Float) {
        val drop = CDrop[entity]
        val tr = CTransforms[entity]
        drop.currentTime += deltaTime
        val d = drop.currentTime / drop.time
        if (drop.dirty) {
            log(d)
            if (drop.velocity.isZero) {
                drop.velocity.set(MathUtils.random(-18f, 18f), MathUtils.random(18f, 26f))
                drop.to.set(tr.position.x + drop.velocity.x, tr.position.y + drop.velocity.y)
                drop.from.set(tr.position)
            }
            val interX = Interpolation.sine.apply(drop.from.x, drop.to.x, d)
            val interY = Interpolation.sine.apply(drop.from.y, drop.to.y, d)
            tr.position.set(interX, interY)
            if (d >= 1f) {
                drop.dirty = false
                drop.step2 = true
                drop.from.set(tr.position)
                drop.to.set(tr.position.x + drop.velocity.x, tr.position.y + drop.velocity.y * -1)
                drop.currentTime = 0f
            }
        } else if (drop.step2) {
            log("${d} step2")
            val interX = interpolation.apply(drop.from.x, drop.to.x, d )
            val interY = interpolation.apply(drop.from.y, drop.to.y, d )
            tr.position.set(interX, interY)
        }
    }
}