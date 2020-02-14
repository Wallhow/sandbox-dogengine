package sandbox.sandbox.go.environment.drop

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import dogengine.ecs.components.create
import dogengine.ecs.components.draw.CAtlasRegion
import dogengine.ecs.components.utility.CDeleteMe
import dogengine.ecs.components.utility.logic.CTransforms
import dogengine.ecs.components.utility.logic.CUpdate
import dogengine.ecs.components.utility.logic.updateZIndex
import dogengine.utils.Size
import sandbox.go.environment.drop.ADropOnMap
import sandbox.sandbox.go.assetAtlas

class Shadow(drop: ADropOnMap) : Entity() {
    val padd = 5
    init {
        create<CTransforms> {
            position.set(CTransforms[drop].position.x+padd,drop.horizontalLine+1)
            size = Size(CTransforms[drop].size.width, CTransforms[drop].size.height / 2.5f)
            updateZIndex()
        }
        create<CAtlasRegion> {
            atlas = assetAtlas()
            nameRegion = drop.dropID.name_res
            color = Color.DARK_GRAY.apply { a=0.8f }
        }
        create<CUpdate> { func = {
            CTransforms[this@Shadow].position.set(CTransforms[drop].position.x+padd,drop.horizontalLine+1)
            if(CDeleteMe[drop] != null) {
                this@Shadow.create<CDeleteMe>()
            }
        } }
    }
}