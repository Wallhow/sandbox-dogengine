package sandbox.go.mobs

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution
import com.badlogic.gdx.ai.utils.random.IntegerDistribution
import dogengine.utils.log
import sandbox.go.mobs.Pig

class DownWalkTask : LeafTask<Pig>() {
    override fun copyTo(task: Task<Pig>): Task<Pig> {
        return this
    }

    override fun execute(): Status {
        val pig = `object`
        pig.downWalk()
        return Status.SUCCEEDED
    }
}