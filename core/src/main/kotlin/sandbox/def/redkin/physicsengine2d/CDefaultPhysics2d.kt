package sandbox.sandbox.def.redkin.physicsengine2d

import com.badlogic.gdx.math.Vector2
import dogengine.ecs.def.ComponentResolver
import dogengine.es.redkin.physicsengine2d.bodies.RectangleBody
import dogengine.es.redkin.physicsengine2d.variables.Types
import sandbox.dogengine.ecs.components.utility.logic.CTransforms
import sandbox.dogengine.ecs.def.PoolableComponent

class CDefaultPhysics2d : PoolableComponent {
    var type: Types.TYPE = Types.TYPE.STATIC
    var name: String = PoolableComponent.tmpString
    var rectangleBody : RectangleBody? = null
    var offset: Vector2 = Vector2()
    companion object : ComponentResolver<CDefaultPhysics2d>(CDefaultPhysics2d::class.java)
    override fun reset() {
        rectangleBody = null
        name = PoolableComponent.tmpString
        type = Types.TYPE.STATIC
    }
}

fun CDefaultPhysics2d.createBody(tr: CTransforms,x:Float,y:Float,width: Float,height:Float,types: Types.TYPE,name: String) {
    offset.set(x,y)
    this.rectangleBody = RectangleBody(tr.position.x, tr.position.y, width, height,types,name)
}