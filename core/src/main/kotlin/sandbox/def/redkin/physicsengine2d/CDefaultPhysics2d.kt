package sandbox.sandbox.def.redkin.physicsengine2d

import dogengine.ecs.def.ComponentResolver
import dogengine.es.redkin.physicsengine2d.bodies.RectangleBody
import dogengine.es.redkin.physicsengine2d.variables.Types
import sandbox.dogengine.ecs.def.PoolableComponent

class CDefaultPhysics2d : PoolableComponent {
    var type: Types.TYPE = Types.TYPE.STATIC
    var name: String = PoolableComponent.tmpString
    var rectangleBody : RectangleBody? = null
    companion object : ComponentResolver<CDefaultPhysics2d>(CDefaultPhysics2d::class.java)
    override fun reset() {
        rectangleBody = null
        name = PoolableComponent.tmpString
        type = Types.TYPE.STATIC
    }
}