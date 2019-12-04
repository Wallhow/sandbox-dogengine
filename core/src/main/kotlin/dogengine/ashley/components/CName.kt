package dogengine.ashley.components

import com.badlogic.ashley.core.Component
import dogengine.def.ComponentResolver

class CName(val name: String) : Component {
companion object : ComponentResolver<CName>(CName::class.java)
}
