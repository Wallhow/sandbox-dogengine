package sandbox.go.mobs

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute
import dogengine.utils.log

class RightWalkTask : LeafTask<Pig>() {
    @TaskAttribute(required = true, name = "urgent")
    @JvmField
    var urgentProb = 0f

    override fun copyTo(task: Task<Pig>): Task<Pig> {
        return this
    }

    override fun start() {
        super.start()
    }

    override fun execute(): Status {
        val pig = `object`
        pig.rightWalk()
        log(urgentProb)
        return Status.SUCCEEDED
    }
}