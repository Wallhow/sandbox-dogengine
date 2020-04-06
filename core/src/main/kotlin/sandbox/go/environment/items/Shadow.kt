package sandbox.go.environment.items

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import dogengine.ecs.components.create
import dogengine.ecs.components.draw.CAtlasRegion
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.logic.CUpdate
import dogengine.ecs.components.utility.logic.updateZIndex
import dogengine.utils.Size
import sandbox.sandbox.go.assetAtlas

class Shadow(item: AItemOnMap) : Entity() {
    val padd = 5
    init {
        create<CTransforms> {
            position.set(CTransforms[item].position.x+padd,item.horizontalLine+1)
            size = Size(CTransforms[item].size.width, CTransforms[item].size.height / 2.5f)
            updateZIndex()
        }
        create<CAtlasRegion> {
            atlas = assetAtlas()
            nameRegion = item.itemType.name_res
            color = Color.DARK_GRAY.apply { a=0.8f }
        }
        create<CUpdate> { func = {
            CTransforms[this@Shadow].position.set(CTransforms[item].position.x+padd,item.horizontalLine+1)
            if(CDeleteMe[item] != null) {
                this@Shadow.create<CDeleteMe>()
            }
        } }
    }
}