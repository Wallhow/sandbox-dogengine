package dogengine.ecs.components.utility.logic

import com.badlogic.gdx.math.Vector2
import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent
import dogengine.redkin.physicsengine2d.bodies.RectangleBody
import dogengine.redkin.physicsengine2d.variables.Types

class CDefaultPhysics2d : PoolableComponent {
    var type: Types.TYPE = Types.TYPE.STATIC
    var name: String = PoolableComponent.tmpString
    var rectangleBody : RectangleBody? = null
    var offset: Vector2 = Vector2()
    var userData : Any? = null
    companion object : ComponentResolver<CDefaultPhysics2d>(CDefaultPhysics2d::class.java)
    override fun reset() {
        rectangleBody = null
        name = PoolableComponent.tmpString
        type = Types.TYPE.STATIC
        userData = null
    }
}