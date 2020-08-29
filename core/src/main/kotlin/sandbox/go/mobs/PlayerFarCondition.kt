package sandbox.go.mobs

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.utility.logic.CTransforms
import sandbox.sandbox.go.player.Player
import kotlin.math.abs

class PlayerFarCondition : LeafTask<Pig>() {
    private val player = Player.playerInstance
    override fun execute(): Status {
        val pig = `object`
        val posPlayer = Vector2(CTransforms[player].getCenterX(),CTransforms[player].getCenterY())
        val posPig = Vector2(CTransforms[pig].getCenterX(),CTransforms[pig].getCenterY())
        return if(abs(posPig.dst(posPlayer)) <= CTransforms[pig].size.getRadius()* 2f) {
            Status.SUCCEEDED
        } else {
            Status.FAILED
        }
    }

    override fun copyTo(task: Task<Pig>?): Task<Pig> {
        return this
    }
}