package dogengine.drawcore.drawfunctions

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import dogengine.drawcore.IDrawFunc
import dogengine.ecs.components.draw.CDraw
import dogengine.ecs.components.utility.logic.CTransforms

open class BatchDrawFunction : IDrawFunc {
    override fun draw(spriteBatch: SpriteBatch, entity: Entity) {
        val drawable = CDraw[entity]
        if(drawable.texture == null) return
        val tr = CTransforms[entity]
        val width : Float = tr.size.width
        val height : Float = tr.size.height
        val defColor = spriteBatch.color.cpy()

        spriteBatch.apply {
            color = drawable.tint
            draw(drawable.texture, tr.position.x+drawable.offsetX, tr.position.y+drawable.offsetY,
                    (tr.getCenterX() - tr.position.x), (tr.getCenterY() - tr.position.y),
                    width, height,
                    tr.size.scaleX, tr.size.scaleY,
                    tr.angle)
            color = defColor
        }
    }
}