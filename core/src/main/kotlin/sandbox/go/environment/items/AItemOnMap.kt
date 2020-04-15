package sandbox.go.environment.items

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import dogengine.ecs.components.create
import dogengine.utils.Size
import sandbox.sandbox.def.def.comp.CDrop
import sandbox.sandbox.go.AGameObject
import sandbox.go.environment.ItemList

abstract class AItemOnMap(val itemType: ItemList,
                          var horizontalLine: Float = 0f) : AGameObject(itemType.resourcesName) {
    override val entity: Entity get() = this
    protected fun createCDrop(timeFly: Float, hLine: Float = horizontalLine) {
        create<CDrop> {
            time = timeFly
            y = hLine
            itemID = itemType
        }
    }


    //TODO Удалить
    protected fun defInit(position: Vector2) {
        createCTransform(position.cpy(), Size(24f,24f))
        createCAtlasRegion(itemType.resourcesName)
        createCDrop(0.75f)
        createCUpdate {  }
        horizontalLine = position.y
        engine.addEntity(Shadow(this))
    }
}