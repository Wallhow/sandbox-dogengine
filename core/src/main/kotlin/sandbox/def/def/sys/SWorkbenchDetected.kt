package sandbox.sandbox.def.def.sys

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import dogengine.Kernel
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.utils.log
import sandbox.sandbox.go.environment.objects.buiding.CWorkbench
import sandbox.sandbox.go.player.Player
import kotlin.math.abs

class SWorkbenchDetected(private val player: Player) : IteratingSystem(Family.all(CWorkbench::class.java).exclude(CHide::class.java).get()) {
    private val rateResSecond = 0.15f
    private var ct = 0f // CurrentTime
    override fun processEntity(entity: Entity, deltaTime: Float) {
        ct += deltaTime
        if (ct >= rateResSecond) {
            ct = 0f

            val t = CWorkbench[entity].type
            val pos = CTransforms[entity].position.cpy().add(CTransforms[entity].size.halfWidth,CTransforms[entity].size.halfHeight)
            val posPlayer = CTransforms[player].position.cpy().add(CTransforms[player].size.halfWidth, CTransforms[player].size.halfHeight)
            val dst = posPlayer.dst(pos)
            if (abs(dst) <= 35) {
                if (player.workbenchNear[t] == null) {
                    player.workbenchNear.put(t, false)
                }
                player.workbenchNear.put(t, true)
            } else {
                player.workbenchNear.put(t, false)
            }


        }
    }
}