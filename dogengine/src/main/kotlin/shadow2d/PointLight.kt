package dogengine.shadow2d

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion

class PointLight {
    var x = 0f
    var y = 0f
    var texture: TextureRegion? = null
    var color = Color.WHITE.cpy()
}