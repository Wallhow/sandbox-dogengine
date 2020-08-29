package dogengine.drawcore

import com.badlogic.gdx.graphics.Color
import dogengine.ecs.def.ComponentResolver
import dogengine.ecs.def.PoolableComponent


class CTint : PoolableComponent {
    companion object:ComponentResolver<CTint>(CTint::class.java)
    var color : Color = Color.WHITE.cpy()
    override fun reset() {
        color.set(1f,1f,1f,1f)
    }
}