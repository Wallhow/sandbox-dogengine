package dogengine.drawcore.drawfunctions

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import dogengine.Kernel
import dogengine.drawcore.IDrawFunc
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.systems.flexbatch.CSolidQuad

class SolidDrawFunction: IDrawFunc {
    private val pixel = Kernel.dotTexture
    override fun draw(spriteBatch: SpriteBatch, entity: Entity) {
        val drawable = CSolidQuad[entity]
        val tr = CTransforms[entity]
        val width : Float = tr.size.width
        val height : Float = tr.size.height
        val defColor = spriteBatch.color.cpy()

        spriteBatch.apply {
            color = drawable.tint
            draw(pixel, tr.position.x, tr.position.y,
                    (tr.getCenterX() - tr.position.x), (tr.getCenterY() - tr.position.y),
                    width, height,
                    tr.size.scaleX, tr.size.scaleY,
                    tr.angle)
            color = defColor
        }
    }
}