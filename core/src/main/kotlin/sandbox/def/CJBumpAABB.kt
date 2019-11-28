package sandbox.def

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.dongbat.jbump.Item
import dogengine.def.ComponentResolver

class CJBumpAABB(var dynamic: Boolean) : Component {
    var item: Item<Entity>? = null
    var scaleSize : Vector2 = Vector2(1f,1f)
    companion object : ComponentResolver<CJBumpAABB>(CJBumpAABB::class.java)
}