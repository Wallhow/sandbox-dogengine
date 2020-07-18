package sandbox.def.def.world

import com.badlogic.gdx.math.Vector2
import dogengine.map2D.Cell
import sandbox.sandbox.go.objects.ItemList
import sandbox.sandbox.go.objects.ObjectList

interface IWorldManager {
    fun getCell(x: Int, y: Int, nameLayer: String) : Cell
    fun createConstruction(type: ObjectList, position: Vector2)
    fun createItem(type: ItemList, pos: Vector2)

    fun isEmpty(cell: Cell) : Boolean
}