package sandbox.go.player.tools

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import dogengine.Kernel
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.redkin.physicsengine2d.bodies.RectangleBody
import sandbox.sandbox.go.objects.ObjectList
import sandbox.sandbox.go.player.Player
import sandbox.sandbox.go.player.tools.ToolsList

abstract class ATool {
    abstract val force : Force
    var isActive = false
    var isFinish = true
    protected var time = 0f
    abstract val name : String
    abstract val image: TextureRegion
    abstract val type: ToolsList
    abstract val distance : Float
    protected val workWith: WorkWith = WorkWith()
    protected val engine: Engine = Kernel.getInjector().getInstance(Engine::class.java)

    open fun hit() {
        isActive = true
    }
    fun update(delta: Float) {
        if (isActive) {
            time += delta
            if (time >= force.duration) {
                isActive = false
                isFinish = true
                time = 0f
            }
        }
    }

    fun isWork(typeObject: ObjectList) : Boolean {
        var result = false
        workWith.objects.forEach {
            if(it == typeObject) {
                result = true
                return@forEach
            }
        }
        return result
    }

    class WorkWith {
        fun add(typeObject: ObjectList) {
            objects.add(typeObject)
        }

        val objects: Array<ObjectList> = Array()
    }

    class Force {
        var value = 0f
        var duration = 0f
    }
}

fun ATool.updatePositionTool(player: Player, tool: Entity, rectangleBody: RectangleBody) {
    val t = CTransforms[tool]
    val pos = Vector2()
    val pp = CTransforms[player]
    when (player.directionSee) {
        Player.DirectionSee.UP -> {
            pos.set(pp.position.x + pp.size.halfWidth - t.size.halfWidth,
                    pp.position.y + pp.size.height - (t.size.height + t.size.halfHeight) / 2)
        }
        Player.DirectionSee.DOWN -> {
            pos.set(pp.position.x + pp.size.halfWidth - t.size.halfWidth,
                    pp.position.y - pp.size.height + (t.size.height + t.size.halfHeight) / 2 - 4f)
        }
        Player.DirectionSee.LEFT -> {
            pos.set(pp.position.x - pp.size.halfWidth + (t.size.width + t.size.halfWidth) / 2 - 4f,
                    pp.position.y + pp.size.halfHeight - t.size.halfHeight)
        }
        Player.DirectionSee.RIGHT -> {
            pos.set(pp.position.x + pp.size.width - (t.size.width + t.size.halfWidth) / 2,
                    pp.position.y + pp.size.halfHeight - t.size.halfHeight)
        }
    }
    rectangleBody.apply {
        setX(pos.x)
        setY(pos.y)
    }
}