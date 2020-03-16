package dogengine.ecs.components.draw

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import dogengine.ecs.def.ComponentResolver

class C3DIn2D(val model: ModelInstance) : Component {
    companion object : ComponentResolver<C3DIn2D>(C3DIn2D::class.java)
    val vGlobalRotateAngle = Vector3.Zero.cpy()
    val vLocalRotateAngle = Vector3.Zero.cpy()
    val vTranslate = Vector3.Zero.cpy().apply { z = -250f }
}