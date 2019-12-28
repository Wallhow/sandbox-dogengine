package sandbox.def

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.BitmapFont
import dogengine.ecs.def.ComponentResolver

class CLabel : Component {
    companion object : ComponentResolver<CLabel>(CLabel::class.java)
    var labelText =""
    var fnt: BitmapFont? = null
}