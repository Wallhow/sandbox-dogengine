package sandbox.sandbox.dev.ecs.sys

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import dogengine.Kernel
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.systems.SystemPriority
import dogengine.utils.GameCamera
import dogengine.utils.Size
import dogengine.utils.extension.get
import dogengine.utils.gdxSchedule
import dogengine.utils.log
import sandbox.sandbox.dev.ecs.comp.CSpriteStacking

class SSpriteStacking : IteratingSystem (Family.all(CSpriteStacking::class.java).get()) {
    private val sb = SpriteBatch()
    private val cam = Kernel.getInjector()[GameCamera::class.java]
    init {
        priority = SystemPriority.DRAW+200
    }
    private var acc = 0f
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val spr= CSpriteStacking[entity]
        val paddingY = spr.paddingY
        val scl = spr.scale
        val (texture,position,size) = entity.getComponentsForDrawSpriteStacking()
        val angle = spr.angle
        //Calculate up vector direction from camera rotation
        val upVector = Vector2(MathUtils.cos(acc - MathUtils.PI/2f), MathUtils.sin(acc - MathUtils.PI/2f)*paddingY)

        val test = size.height/texture.size
        for (i in 1 until texture.size+1) {
            sb.draw(texture[i-1],position.x + upVector.x*i,position.y+(i*upVector.y),
                    (CTransforms[entity].getCenterX() - position.x), (CTransforms[entity].getCenterY() - position.y),
                    size.width,size.height,
                    scl,scl,
                    angle)
        }
    }



    override fun update(deltaTime: Float) {
        acc=(180f+cam.getAngle())*MathUtils.degreesToRadians
        sb.projectionMatrix = cam.getCamera().combined
        sb.begin()
        sb.enableBlending()
        super.update(deltaTime)
        sb.disableBlending()
        sb.end()

    }
}

private fun Entity.getComponentsForDrawSpriteStacking(): ComponentsForDraw {
    val transformComp = CTransforms[this]
    return ComponentsForDraw(CSpriteStacking[this].spriteStack,transformComp.position,transformComp.size)
}
data class ComponentsForDraw(val texture: Array<TextureRegion>,val position: Vector2,val size: Size)