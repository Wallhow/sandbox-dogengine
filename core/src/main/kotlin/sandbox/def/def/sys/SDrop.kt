package sandbox.sandbox.def.def.sys

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.utils.log
import sandbox.sandbox.def.def.comp.CDrop

class SDrop : IteratingSystem(Family.all(CDrop::class.java).get()) {
    private val interpolation: Interpolation = Interpolation.bounceOut
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val drop = CDrop[entity]
        val tr = CTransforms[entity]
        drop.currentTime += deltaTime
        val d = drop.currentTime / drop.time
        if (drop.step1) {
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
                drop.step1 = false
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