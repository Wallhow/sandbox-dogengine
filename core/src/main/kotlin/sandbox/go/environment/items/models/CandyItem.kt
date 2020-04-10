package sandbox.go.environment.items.models

import com.badlogic.gdx.math.Vector2
import sandbox.go.environment.items.AItemOnMap
import sandbox.go.environment.ItemList

class CandyItem(position: Vector2) : AItemOnMap(ItemList.CANDY) {
    init {
        defInit(position)
    }
}