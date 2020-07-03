package dogengine.drawcore

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.g2d.SpriteBatch

interface IDrawFunc {
    fun draw(spriteBatch: SpriteBatch, entity: Entity)
}