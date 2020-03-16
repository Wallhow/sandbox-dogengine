package dogengine.ecs.systems.physics

import com.badlogic.gdx.physics.box2d.Contact
import dogengine.ecs.def.GameEntity

data class Collide (val gameEntity1: GameEntity, val gameEntity2: GameEntity, val contact: Contact, val type: TypeCollide)
enum class TypeCollide {
    Begin,End,PreSolve,PostSolve
}