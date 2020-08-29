package sandbox.go.mobs

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution
import com.badlogic.gdx.ai.utils.random.IntegerDistribution
import dogengine.utils.log

class LeftWalkTask : LeafTask<Pig>() {
    @TaskAttribute
    @JvmField
    var times: IntegerDistribution = ConstantIntegerDistribution.ONE
    private var t = 0f

    override fun start() {
        super.start()
        t = times.nextFloat()
    }
    override fun copyTo(task: Task<Pig>): Task<Pig> {
        return this
    }

    override fun execute(): Status {
        val pig = `object`
        pig.leftWalk()
        log(times.nextFloat())
        return Status.SUCCEEDED
    }
}