package sandbox.sandbox.go.environment.models

import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.draw.CAtlasRegion
import dogengine.redkin.physicsengine2d.variables.Types
import dogengine.utils.Size
import sandbox.go.environment.drop.models.RockDrop
import sandbox.sandbox.def.def.comp.CHealth
import sandbox.sandbox.go.environment.AGOMap
import sandbox.sandbox.go.environment.drop.dropOnMap
import sandbox.sandbox.go.items.ObjectList

class Rock(position: Vector2) : AGOMap("rock") {
    override val dropID: ObjectList = ObjectList.ROCK
    private var currentIdx = 0

    init {
        createCAtlasRegion()
        val tex = getAtlasRegion()
        createCTransform(position, Size(tex.regionWidth.toFloat(), tex.regionHeight * 1f))
        createCPhysicsDef(type = Types.TYPE.DYNAMIC)
        createCUpdate {
            val idx = 6 - (CHealth[this@Rock].health / 3).toInt()
            CAtlasRegion[this@Rock].index = if (idx < 5) {
                idx
            } else 5
        }
        createCHealth(15f)
        CHealth[this].shot = {
            if (currentIdx != CAtlasRegion[this@Rock].index) {
                currentIdx = CAtlasRegion[this@Rock].index
                dropOnMap(0, 1,ObjectList.ROCK)
            }

        }
    }
}