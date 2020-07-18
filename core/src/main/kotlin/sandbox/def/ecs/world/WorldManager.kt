package sandbox.def.ecs.world

import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.components
import dogengine.ecs.components.create
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.map2D.Cell
import dogengine.map2D.Map2D
import dogengine.utils.extension.addEntity
import map2D.TypeData
import sandbox.sandbox.go.objects.ItemList
import sandbox.def.ecs.comp.CToBuild
import sandbox.def.ecs.comp.CToDrop
import sandbox.sandbox.engine
import sandbox.sandbox.go.objects.ObjectList

class WorldManager (private val map: Map2D) : IWorldManager {
    override fun getCell(x: Int, y: Int, nameLayer: String): Cell {
        return map.getLayer(nameLayer).getCell(x,y)
    }

    override fun createConstruction(type: ObjectList, position: Vector2) {
        engine().addEntity {
            components {
                create<CToBuild> { this.type = type }
                create<CTransforms> {
                    this.position.set(position)
                }
            }
        }
    }

    override fun createItem(type: ItemList, pos: Vector2) {
        engine().addEntity {
            components {
                create<CToDrop> { this.type = type }
                create<CTransforms> {
                    this.position.set(pos.x,pos.y)
                }
            }
        }
    }

    override fun isEmpty(cell: Cell): Boolean = cell.data[TypeData.ObjectOn]==null
}