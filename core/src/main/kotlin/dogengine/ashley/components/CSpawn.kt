package dogengine.ashley.components

import com.badlogic.ashley.core.Component
import dogengine.def.ComponentResolver

class CSpawn : Component {
companion object : ComponentResolver<CSpawn>(CSpawn::class.java)
}
