package dogengine.ecs.systems.draw

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.math.Vector2
import com.google.inject.Inject
import dogengine.ecs.components.draw.C3DIn2D
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.visible.CHide
import dogengine.ecs.systems.SystemPriority

class SRender3DIn2D @Inject constructor(val camera: OrthographicCamera): IteratingSystem(Family.all(CTransforms::class.java, C3DIn2D::class.java).exclude(CHide::class.java).get()) {
    private val modelBatch: ModelBatch = ModelBatch()
    private val env = Environment().apply { set(ColorAttribute(ColorAttribute.AmbientLight,0.6f,0.6f,0.6f,1f))
        add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))}

    init {
        camera.near = 0f
        camera.far = 1000f
        priority = SystemPriority.DRAW-7
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val cTransforms = CTransforms[entity]
        val pos1 = cTransforms.position
        val halfsize = Vector2(cTransforms.size.halfWidth,cTransforms.size.halfHeight)
        val c3d = C3DIn2D[entity]
        val rotate = cTransforms.angle

        c3d.vTranslate.set(pos1.x+halfsize.x,pos1.y,c3d.vTranslate.z)

        c3d.model.apply {
            transform.setToRotation(1f,0f,0f,c3d.vLocalRotateAngle.x)
            transform.setToRotation(0f,1f,0f,c3d.vLocalRotateAngle.y)
            transform.setToRotation(0f,0f,1f,rotate)
            transform.setTranslation(c3d.vTranslate.x,c3d.vTranslate.y,c3d.vTranslate.z)
            transform.rotate(1f,0f,0f,c3d.vGlobalRotateAngle.x)
            transform.rotate(0f,1f,0f,c3d.vGlobalRotateAngle.y)
            transform.rotate(0f,0f,1f,c3d.vGlobalRotateAngle.z)
        }

        modelBatch.begin(camera)
        modelBatch.render(c3d.model,env)
        modelBatch.end()
    }
}