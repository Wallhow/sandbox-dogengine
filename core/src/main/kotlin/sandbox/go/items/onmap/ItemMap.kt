package sandbox.go.items.onmap

import com.badlogic.ashley.core.Entity

interface ItemMap {
    var horizontalLine: Float
    val entity: Entity
    val invID : Int
}