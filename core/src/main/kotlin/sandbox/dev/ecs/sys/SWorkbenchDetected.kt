package sandbox.dev.ecs.sys

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import sandbox.sandbox.go.environment.objects.buiding.CWorkbench
import sandbox.sandbox.go.player.Player
import kotlin.math.abs

class SWorkbenchDetected(private val player: Player) : IteratingSystem(Family.all(CWorkbench::class.java).exclude(CHide::class.java).get()) {
    private val dist: Int = 40
    private var ct = 0f // CurrentTime
    override fun processEntity(entity: Entity, deltaTime: Float) {
        ct += deltaTime
        entity.afterUpdate(0.15f)
    }

    private fun Entity.afterUpdate(rateResSecond: Float) {
        if(ct>=rateResSecond) {
            ct = 0f

            val t = CWorkbench[this].type
            val xWorkbench = CTransforms[this].position.x + CTransforms[this].size.halfWidth
            val yWorkbench = CTransforms[this].position.y + CTransforms[this].size.halfHeight

            val posPlayer = Vector2(CTransforms[player].getCenterX(),CTransforms[player].getCenterY())
            val dst = posPlayer.dst(xWorkbench,yWorkbench)
            if (abs(dst) <= dist) {
                CWorkbench[this].isNear = true
                if (player.workbenchNear[t] == null) {
                    player.workbenchNear.put(t, false)
                }
                player.workbenchNear.put(t, true)
            } else {
                CWorkbench[this].isNear = false
                player.workbenchNear.put(t, false)
            }

        }
    }
}