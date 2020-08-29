package sandbox.go.mobs

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import com.badlogic.gdx.ai.utils.random.ConstantIntegerDistribution
import com.badlogic.gdx.ai.utils.random.IntegerDistribution
import com.badlogic.gdx.math.MathUtils
import dogengine.utils.log
import sandbox.go.mobs.Pig

class FreeWalkTask : LeafTask<Pig>() {
    /*@TaskAttribute
    @JvmField*/
    var times = MathUtils.random(2f,7f)
    private var t = 1

    override fun start() {
        super.start()
    }
    override fun copyTo(task: Task<Pig>): Task<Pig> {
        return this
    }
    private var timeRunning = 0f
    override fun execute(): Status {
        timeRunning += Gdx.graphics.deltaTime
        val pig = `object`
        when (t) {
            1 -> {pig.leftWalk()}
            2 -> {pig.rightWalk()}
            3 -> {pig.upWalk()}
            4 -> {pig.downWalk()}
        }
        return if (timeRunning>times) {
            timeRunning = 0f
            times = MathUtils.random(2f,7f)
            t = MathUtils.random(1,4)
            log("freeWalk refresh")
            Status.SUCCEEDED
        } else
        {

            Status.FAILED
        }

    }
}