package dogengine.drawcore.drawfunctions

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import dogengine.drawcore.CAtlasRegions
import dogengine.drawcore.CTint
import dogengine.drawcore.IDrawFunc
import dogengine.ecs.components.utility.logic.CTransforms

class MultiRegionsDrawFunction: IDrawFunc {
    override fun draw(spriteBatch: SpriteBatch, entity: Entity) {
        val regions = CAtlasRegions[entity].regions
        val direction = CAtlasRegions[entity].directionDraw
        val tr = CTransforms[entity]

        val width : Float = tr.size.width
        val height : Float = tr.size.height
        val defColor = spriteBatch.color.cpy()

        CTint[entity]?.let {
            spriteBatch.color = it.color
        }
        for( i in 0 until regions.size) {
            spriteBatch.apply {
                draw(regions[i], tr.position.x+direction.x*i*width, tr.position.y+direction.y*i*height,
                        (tr.getCenterX() - tr.position.x), (tr.getCenterY() - tr.position.y),
                        width, height,
                        tr.size.scaleX, tr.size.scaleY,
                        tr.angle)
            }
        }
        spriteBatch.color = defColor
    }
}