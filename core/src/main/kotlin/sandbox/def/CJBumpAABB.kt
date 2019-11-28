package sandbox.def

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.dongbat.jbump.Item
import dogengine.def.ComponentResolver

class CJBumpAABB : Component {
    var item: Item<Entity>? = null
    var worldPosition = Vector2()
    companion object : ComponentResolver<CJBumpAABB>(CJBumpAABB::class.java)
}