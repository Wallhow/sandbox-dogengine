package sandbox.sandbox.def.particles

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion

class Material {
    fun reset() {
        color.set(1f,1f,1f,1f)
        texture = null
    }

    val color: Color = Color.WHITE.cpy()
    var texture: TextureRegion? = null
}