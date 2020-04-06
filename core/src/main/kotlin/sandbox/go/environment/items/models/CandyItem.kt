package sandbox.go.environment.items.models

import com.badlogic.gdx.math.Vector2
import sandbox.go.environment.items.AItemOnMap
import sandbox.go.environment.ObjectList

class CandyItem(position: Vector2) : AItemOnMap(ObjectList.CANDY) {
    init {
        defInit(position)
    }
}