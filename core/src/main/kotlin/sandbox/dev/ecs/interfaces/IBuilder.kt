package sandbox.dev.ecs.interfaces

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2

interface IBuilder {
    fun build(position: Vector2) : Entity
}